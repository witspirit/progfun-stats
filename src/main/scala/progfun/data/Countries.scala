package progfun.data

import io.Source


object Countries extends Utilities {

  // a list of iso codes and a list of and corresponding country names
  private def countriesAndIsos(file: String): (List[String], List[String]) = {
    val lines = Source.fromFile(file).getLines().toList
    lines.map { line =>
      val a = line.split("""\|""")
      (a(0), a(1))
    }.unzip
  }

  private val (fullIsos, fullCountries) = countriesAndIsos("dat/countries.dat")
  private val countriesMap = (fullCountries zip fullIsos).toMap

  // make a map of ISO vs ISO3 codes, for population lookup
  private def isoAndIso3s() : (List[String], List[String]) = {
    val lines = Source.fromFile("dat/allCountries.tsv").getLines().toList
    val isos = lines.map(line => line.take(2))
    val iso3s = lines.map(line => line.take(7).drop(4))

    (iso3s, isos)
  }

  private val (iso3s, isos) = isoAndIso3s()
  private val isoToIso3 = (isos zip iso3s).toMap

  // read in population data and do lookup
  private def createIso3ToPopulationMap(): Map[String, Long] = {
    val lines = Source.fromFile("dat/populationByIso3.tsv").getLines().toList
    val iso3 = getColumn(1, lines)
    val population2011 = getColumn(2, lines).map(_ toLong)

    Map((iso3 zip population2011): _*)
  }

  private val popMap = createIso3ToPopulationMap()

  private def constructCountryList() : List[Country] = {
    fullCountries.map(countryName => {
      val iso = countriesMap(countryName)
      val iso3 = isoToIso3 getOrElse (iso, "N/A") // The printed list is complete, but when I don't use getOrElse this thing blows up... ?
      val population = popMap get iso3

      Country(countryName, iso, iso3, population)
    })
  }

  val countries : List[Country] = constructCountryList()

  val countryByName : Map[String, Country] = countries.map(country => (country.fullName, country)).toMap

  val countryByIso : Map[String, Country] = countries.map(country => (country.iso, country)).toMap

  val countryByIso3 : Map[String, Country] = countries.map(country => (country.iso3, country)).toMap
}

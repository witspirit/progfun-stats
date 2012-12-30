package progfun

import data.{Countries, CourseraData}
import graphtypes.{CountryDetailSpec, FlexWorldMapFactory}

object FlexWorldMapDensityGraph extends FlexWorldMapFactory with App{

  private val studentCountByIso : Map[String, Int] = getFreqs(CourseraData.countries).map { case (countryName, frequency) => (Countries.countryByName(countryName).iso, frequency)}.toMap

  private val studentCountForAllCountries : List[(String, Int)] = Countries.countries.map { country =>
    (country.iso, studentCountByIso.getOrElse (country.iso, 0))
  }

  private val data = studentCountForAllCountries.map {
    case (iso, count) =>
      val population = Countries.countryByIso(iso).population

      val studentDensity = if (population.isEmpty) 0 else count / population.get.toDouble

      val mapDetail = "{ count: "+count+", population: "+population.getOrElse(0L)+" }"

      (iso, studentDensity, mapDetail)
  }

  private val isoCode = data.map(_._1)
  private val studentDensity = data.map(_._2)
  private val mapDetail = data.map(_._3)

  /* file name to output to */
  val name = "worldmap-density.html"

  val details: List[CountryDetailSpec] = List(CountryDetailSpec("Number of Students", "count", None), CountryDetailSpec("Population", "population", Some("opacity: 0.5;")))

  val countryDensities = isoCode zip studentDensity

  val countryDetails = isoCode zip mapDetail

  writeHtml()
}

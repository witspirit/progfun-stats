package progfun

import data.{Country, CourseraData}
import graphtypes.WorldMapFactory

object WorldMapDensityGraph extends WorldMapFactory with App{

  private val studentCounts = getFreqs(CourseraData.countries).toMap

  private def totalStudentCount(country: Country) : Int = studentCounts.getOrElse(country.fullName, 0)
  private def population(country: Country) : Long = country.population.getOrElse(0L)

  private def studentDensity(country: Country) : Double =
    if (population(country) == 0L)
      0.0
    else
      totalStudentCount(country) / population(country).toDouble

  /* file name to output to */
  val name = "worldmap-density.html"

  val details: List[CountryDetailSpec] = List(
    CountryDetailSpec("Number of Students", "count", None, totalStudentCount),
    CountryDetailSpec("Population", "population", Some("opacity: 0.5;"), population)
  )

  val countryDensities = studentDensity _

  writeHtml()
}

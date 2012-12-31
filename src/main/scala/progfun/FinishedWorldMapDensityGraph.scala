package progfun

import data.{Country, CourseraData}
import graphtypes.WorldMapFactory

object FinishedWorldMapDensityGraph extends WorldMapFactory with App {

  private val totalStudentCounts = getFreqs(CourseraData.countries).toMap
  private val finishedStudentCounts = getFreqs(CourseraData.finished.map(_.country)).toMap

  private def totalStudentCount(country: Country): Int = totalStudentCounts.getOrElse(country.fullName, 0)
  private def finishedStudentCount(country: Country): Int = finishedStudentCounts.getOrElse(country.fullName, 0)
  private def population(country: Country): Long = country.population.getOrElse(0L)

  private def studentDensity(country: Country): Double =
    if (population(country) == 0L)
      0.0
    else
      finishedStudentCount(country) / population(country).toDouble


  /* file name to output to */
  val name = "worldmap-finisheddensity"

  val details: List[CountryDetailSpec] = List(
    CountryDetailSpec("Number of Students that finished the course", "finishedCount", None, finishedStudentCount),
    CountryDetailSpec("Total number of students", "totalCount", None, totalStudentCount),
    CountryDetailSpec("Population", "population", Some("opacity: 0.5;"), population)
  )

  val countryDensities = studentDensity _

  writeHtml()
}

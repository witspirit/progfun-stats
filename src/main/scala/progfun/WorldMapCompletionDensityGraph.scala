package progfun

import data.{Country, CourseraData}
import graphtypes.WorldMapFactory

object WorldMapCompletionDensityGraph extends WorldMapFactory with App {

  private val totalStudentCounts = getFreqs(CourseraData.countries).toMap
  private val finishedStudentCounts = getFreqs(CourseraData.finished.map(_.country)).toMap


  private val studentCompletionRates = {
    val completionRates : Map[String, Double] = totalStudentCounts
      .filter { case (countryName, count) => count > 10 } // If less than 10 people enrolled, the statistic is not very relevant
      .map { case (countryName, count) =>
        val finishedStudents = finishedStudentCounts.getOrElse(countryName, 0)
        (countryName, finishedStudents / count.toDouble)
      }

    val nonZeroRates = completionRates.filter(_._2 > 0)
    val average = nonZeroRates.map(_._2).sum / nonZeroRates.size

    // Normalize
    completionRates.map { case (countryName, completionRate) => (countryName, completionRate / average) }
  }

  private def totalStudentCount(country: Country): Int = totalStudentCounts.getOrElse(country.fullName, 0)
  private def finishedStudentCount(country: Country): Int = finishedStudentCounts.getOrElse(country.fullName, 0)
  private def normalizedCompletionRates(country: Country): Double = studentCompletionRates.getOrElse(country.fullName, 0.0)

  private def studentCompletionRate(country: Country): Double =
    if (totalStudentCount(country) == 0)
      0.0
    else
      finishedStudentCount(country) / totalStudentCount(country).toDouble

  private def completionPercentage(country: Country): String = (studentCompletionRate(country) * 100).round.toInt + "%"


  /* file name to output to */
  val name = "worldmap-completiondensity"

  val details: List[CountryDetailSpec] = List(
    CountryDetailSpec("Number of Students that finished the course", "finishedCount", None, finishedStudentCount),
    CountryDetailSpec("Total number of students", "totalCount", None, totalStudentCount),
    CountryDetailSpec("Completion Percentage", "completionPercentage", Some("opacity: 0.5;"), completionPercentage)
  )

  val countryDensities = normalizedCompletionRates _

  writeHtml()
}

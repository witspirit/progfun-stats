package progfun

import data.{Countries, CourseraData}
import graphtypes.{CountryDetailSpec, WorldMapFactory}

object FinishedWorldMapDensityGraph extends WorldMapFactory with App{

  private val totalStudentCountByIso : Map[String, Int] = worldInfo.map { case (iso, country) => (iso, (getFreqs(CourseraData.countries).toMap).getOrElse(country.fullName, 0))  }
  private val finishedStudentCountByIso : Map[String, Int] = worldInfo.map { case (iso, country) => (iso, (getFreqs(CourseraData.finished.map(_.country)).toMap).getOrElse(country.fullName, 0))}
  private val populationByIso : Map[String, Long] = worldInfo.map { case (iso, country) => (iso, country.population.getOrElse(0L)) }
  private val studentDensityByIso : Map[String, Double] = worldInfo.map { case (iso, country) =>
    (iso,
     if (populationByIso(iso) == 0L)
       0.0
     else
       finishedStudentCountByIso(iso) / populationByIso(iso).toDouble
    )}

   /* file name to output to */
   val name = "worldmap-finisheddensity.html"

   val details: List[CountryDetailSpec] = List(
     CountryDetailSpec("Number of Students that finished the course", "finishedCount", None, finishedStudentCountByIso),
     CountryDetailSpec("Total number of students", "totalCount", None, totalStudentCountByIso),
     CountryDetailSpec("Population", "population", Some("opacity: 0.5;"), populationByIso))

   val countryDensities = studentDensityByIso

   writeHtml()
 }

package progfun

import data.{Countries, CourseraData}
import graphtypes.{CountryDetailSpec, WorldMapFactory}

object WorldMapDensityGraph extends WorldMapFactory with App{

  private val totalStudentCountByIso : Map[String, Int] = worldInfo.map { case (iso, country) => (iso, (getFreqs(CourseraData.countries).toMap).getOrElse(country.fullName, 0))  }
  private val populationByIso : Map[String, Long] = worldInfo.map { case (iso, country) => (iso, country.population.getOrElse(0L)) }
  private val studentDensityByIso : Map[String, Double] = worldInfo.map { case (iso, country) => (iso, if (populationByIso(iso) == 0L) 0.0 else totalStudentCountByIso(iso) / populationByIso(iso).toDouble)}

  /* file name to output to */
  val name = "worldmap-density.html"

  val details: List[CountryDetailSpec] = List(
    CountryDetailSpec("Number of Students", "count", None, totalStudentCountByIso),
    CountryDetailSpec("Population", "population", Some("opacity: 0.5;"), populationByIso)
  )

  val countryDensities = studentDensityByIso

  writeHtml()
}

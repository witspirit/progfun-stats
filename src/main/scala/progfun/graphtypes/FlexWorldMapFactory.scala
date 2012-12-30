package progfun.graphtypes

import java.io.File
import progfun.data.{Utilities, CourseraData, Countries}

/* Required data input files:
  * dat/countries.dat, dat/allCountries.tsv, dat/populationByIso3.tsv
  *
  * Output files:
  * html/worldmap-density.js, html/worldmap-density-count.js,
  * html/worldmap-density-pop.js
  */
abstract class FlexWorldMapFactory extends GraphFactory with Utilities {

  val studentCountByIso : Map[String, Int] = getFreqs(CourseraData.countries).map { case (countryName, frequency) => (Countries.countryByName(countryName).iso, frequency)}.toMap

  val studentCountForAllCountries : List[(String, Int)] = Countries.countries.map { country =>
    (country.iso, studentCountByIso.getOrElse (country.iso, 0))
  }

  val data = studentCountForAllCountries.map {
    case (iso, count) =>
      val population = Countries.countryByIso(iso).population

      val studentDensity = if (population.isEmpty) 0 else count / population.get.toDouble

      (iso, population.getOrElse(0), count, studentDensity)
  }

  val isoCode = data.map(_._1)
  val population = data.map(_._2)
  val studentCount = data.map(_._3)
  val studentDensity = data.map(_._4)

  // output to directory "html"
  /* Required files: jquery-jvectormap, ../dat/worldmap.js,
   *                 resources/javascript/vectormap.js
   */
  def writeHtml() {
    worldMapToJs(isoCode zip studentDensity, CourseraData.total, "density", "html/worldmap-density.js")
    worldMapToJs(isoCode zip studentCount, CourseraData.total, "count", "html/worldmap-density-count.js")
    worldMapToJs(isoCode zip population, CourseraData.total, "population", "html/worldmap-density-pop.js")

    val html =
      <html>
        <head>
          <title>World Map</title>
          <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
          <script src="resources/javascript/jquery-jvectormap-1.1.1.min.js"></script>
          <script src="../dat/worldmap.js"></script>
          <script src="worldmap-density-count.js"></script>
          <script src="worldmap-density-pop.js"></script>
          <script src="worldmap-density.js"></script>
          <script src="http://d3js.org/d3.v3.min.js"></script>
          <link href="resources/stylesheets/jquery-jvectormap-1.1.1.css" rel="stylesheet" type="text/css"/>
        </head>
        <body>
          <div id="map"></div>
          <script src="resources/javascript/vectormap.js" type="text/javascript"></script>
        </body>
      </html>
    printToFile(new File(new File("html"), name))(writer => writer.println(html.toString))
  }

  // takes a list of pairs of country ISO and some value and writes it to a file
  def worldMapToJs(data: List[(String, Any)], total: Int, varName: String, outputLoc: String) = {
    printToFile(new File(outputLoc)) {
      p =>
        val objArray = ("var " + varName + " = {" :: data.map {
          case (iso, value) => "\"" + iso + "\": " + value + ","
        })
        val noComma = objArray.last.replace(",", "")
        val out = objArray.dropRight(1) ++ List(noComma) ++ List("};\nvar tot =" + total + ";")
        out.foreach(p.println)
    }
  }

}

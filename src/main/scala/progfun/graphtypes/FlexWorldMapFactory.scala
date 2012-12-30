package progfun.graphtypes

import java.io.File
import progfun.data.{Utilities, CourseraData, Countries}

case class CountryDetailSpec(title: String, property: String, style: Option[String])

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

      val mapDetail = "{ count: "+count+", population: "+population.getOrElse(0L)+" }"

      (iso, studentDensity, mapDetail)
  }

  val isoCode = data.map(_._1)
  val studentDensity = data.map(_._2)
  val mapDetail = data.map(_._3)

  // output to directory "html"
  /* Required files: jquery-jvectormap, ../dat/worldmap.js,
   *                 resources/javascript/vectormap.js
   */
  def writeHtml() {
    val details: List[CountryDetailSpec] = List(CountryDetailSpec("Number of Students", "count", None), CountryDetailSpec("Population", "population", Some("opacity: 0.5;")))

    generateIsoToValueJs(isoCode zip studentDensity, "density", "html/worldmap-density.js")
    generateIsoToValueJs(isoCode zip mapDetail, "countryDetails", "html/countryDetails.js")
    generateDetailDescriptionJs(details, "html/detailDescription.js")

    val html =
      <html>
        <head>
          <title>World Map</title>
          <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
          <script src="resources/javascript/jquery-jvectormap-1.1.1.min.js"></script>
          <script src="../dat/worldmap.js"></script>
          <script src="worldmap-density.js"></script>
          <script src="detailDescription.js"></script>
          <script src="countryDetails.js"></script>
          <script src="http://d3js.org/d3.v3.min.js"></script>
          <link href="resources/stylesheets/jquery-jvectormap-1.1.1.css" rel="stylesheet" type="text/css"/>
        </head>
        <body>
          <div id="map"></div>
          <script src="resources/javascript/vectormap.js" type="text/javascript"></script>
        </body>
      </html>
    printToFile(new File(new File("html"), name))(writer => writer.println(html.toString()))
  }

  // takes a list of pairs of country ISO and some value and writes it to a file
  def generateIsoToValueJs(data: List[(String, Any)], varName: String, outputLoc: String) {
    printToFile(new File(outputLoc)) { p =>
        val dataItems = data.map {
          case (iso, value) => "\"" + iso + "\": " + value
        }.mkString(",\n")

        p.println("var "+varName+" = {")
        p.println(dataItems)
        p.println("};")
    }
  }

  def generateDetailDescriptionJs(details: List[CountryDetailSpec], outputLoc: String) {
      val detailElements = details.map { case CountryDetailSpec(title, property, style) =>
        "{ title: \""+title+"\",\n  property: \""+property+"\",\n  style: "+style.map("\"" + _ + "\"\n").getOrElse("undefined\n") + "}"
      }.mkString(",\n")

    printToFile(new File(outputLoc)) { p =>
      p.println("var details = [")
      p.println(detailElements)
      p.println("];")
    }
  }

}

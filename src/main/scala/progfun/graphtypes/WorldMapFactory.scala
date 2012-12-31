package progfun.graphtypes

import java.io.File
import progfun.data.{Country, Utilities, Countries}


/* Required data input files:
  * dat/countries.dat, dat/allCountries.tsv, dat/populationByIso3.tsv
  *
  * Output files:
  * html/worldmap-density.js, html/worldmap-density-count.js,
  * html/worldmap-density-pop.js
  */
abstract class WorldMapFactory extends GraphFactory with Utilities {

  type ValueProvider = Country => Any

  case class CountryDetailSpec(title: String, property: String, style: Option[String], valueProvider : ValueProvider)

  private val worldInfo : Map[String, Country] = Countries.countryByIso

  def details : List[CountryDetailSpec] // Describes the fields that will be displayed in the country hover - will extract data from details
  val countryDensities : ValueProvider // The dataset that is used to display the color codings in the map

  // output to directory "html"
  /* Required files: jquery-jvectormap, ../dat/worldmap.js,
   *                 resources/javascript/vectormap.js
   */
  def writeHtml() {
    val densities = worldInfo.map { case (iso, country) => (iso, countryDensities(country)) }.toList

    val densityFileName = name+"-density.js"
    val countryDetailsFileName = name+"-countryDetails.js"
    val detailDescriptionFileName = name+"-detailDescription.js"


    val countryDetails = worldInfo.map { case (iso, country) =>
        val properties = details.map { spec => spec.property + ": \"" + spec.valueProvider(country) + "\"" }
        (iso, properties.mkString("{", ", ", "}"))
    }.toList

    generateIsoToValueJs(densities, "density", "html/"+densityFileName)
    generateIsoToValueJs(countryDetails, "countryDetails", "html/"+countryDetailsFileName)
    generateDetailDescriptionJs(details, "html/"+detailDescriptionFileName)

    val html =
      <html>
        <head>
          <title>World Map</title>
          <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
          <script src="resources/javascript/jquery-jvectormap-1.1.1.min.js"></script>
          <script src="../dat/worldmap.js"></script>
          <script src={densityFileName}></script>
          <script src={detailDescriptionFileName}></script>
          <script src={countryDetailsFileName}></script>
          <script src="http://d3js.org/d3.v3.min.js"></script>
          <link href="resources/stylesheets/jquery-jvectormap-1.1.1.css" rel="stylesheet" type="text/css"/>
        </head>
        <body>
          <div id="map"></div>
          <script src="resources/javascript/vectormap.js" type="text/javascript"></script>
        </body>
      </html>
    printToFile(new File(new File("html"), name+".html"))(writer => writer.println(html.toString()))
  }

  // takes a list of pairs of country ISO and some value and writes it to a file
  def generateIsoToValueJs(data: List[(String, Any)], varName: String, outputLoc: String) {
    printToFile(new File(outputLoc)) { p =>
        val dataItems = data.map {
          case (iso, value) => "\"" + iso + "\": " + value
        }.mkString("var "+varName+" = {\n",",\n", "};")

        p.println(dataItems)
    }
  }

  def generateDetailDescriptionJs(details: List[CountryDetailSpec], outputLoc: String) {
      val detailElements = details.map { case CountryDetailSpec(title, property, style, values) =>
        "{ title: \""+title+"\",\n  property: \""+property+"\",\n  style: "+style.map("\"" + _ + "\"\n").getOrElse("undefined\n") + "}"
      }.mkString("var details = [\n",",\n", "];")

    printToFile(new File(outputLoc)) { p => p.println(detailElements) }
  }

}

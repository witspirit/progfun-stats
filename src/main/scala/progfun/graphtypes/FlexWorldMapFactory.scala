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

   // a sorted list of pairs of countries and frequencies, sorted in descending order by frequency
   val countryCount = {
     val xs = getFreqs(CourseraData.countries)
     xs.sortBy(_._2)
       .reverse
       .map { case (countryName, freq) => (countryName, Countries.countryByName(countryName).iso, freq) }
   }

   // for the jvectormap world map
   val countryCountWithZeros = {
     val uninvolved = Countries.countries.filter {
       country => !countryCount.exists { case (countryName, iso2, count) => country.iso == iso2 }
     }.map(country => (country.fullName, country.iso, 0))
     (countryCount ++ uninvolved).sortBy(_._2)
   }

   // takes a list of pairs of country ISO and population and writes it to a file
   def worldMapToJs(data: List[(String, Any)], total: Int, varName: String, outputLoc: String) = {
     printToFile(new File(outputLoc)) { p =>
       val objArray = ("var "+varName+" = {" :: data.map { case (iso, count) => "\""+iso+"\": "+count+"," })
       val noComma = objArray.last.replace(",", "")
       val out = objArray.dropRight(1) ++ List(noComma) ++ List("};\nvar tot ="+total+";")
       out.foreach(p.println)
     }
   }

   val isosCounts: List[(String, Int)] = countryCountWithZeros.map { case (countryName, iso, count) => (iso, count) }

   worldMapToJs(isosCounts, CourseraData.countries.length, "studentData", "dat/worldmap-counts.js")

   val (densCode, densPop) = countryCountWithZeros.map { case (countryName, iso, count) =>
     (iso, Countries.countryByIso(iso).population)
   }.unzip


   val (densCount, dens) = countryCountWithZeros.map { case (countryName, iso, count) =>
     val pop = Countries.countryByIso(iso).population
     if (pop.isEmpty) (count, 0)
     else (count, count / pop.get.toDouble)
   }.unzip

   // output to directory "html"
   /* Required files: jquery-jvectormap, ../dat/worldmap.js,
    *                 resources/javascript/vectormap.js
    */
   def writeHtml(): Unit = {
     worldMapToJs(densCode zip dens, CourseraData.total, "density", "html/worldmap-density.js")
     worldMapToJs(densCode zip densCount, CourseraData.total, "count", "html/worldmap-density-count.js")
     worldMapToJs(densCode zip densPop, CourseraData.total, "population", "html/worldmap-density-pop.js")

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
           <link href="resources/stylesheets/jquery-jvectormap-1.1.1.css" rel="stylesheet" type="text/css" />
         </head>
         <body>
           <div id="map"></div>
           <script src="resources/javascript/vectormap.js" type="text/javascript"></script>
         </body>
       </html>
     printToFile(new File(new File("html"), name))(writer => writer.println(html.toString))
   }

 }

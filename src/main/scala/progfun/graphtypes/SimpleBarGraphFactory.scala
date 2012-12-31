package progfun.graphtypes

import progfun.data.Utilities
import java.io.File

abstract class SimpleBarGraphFactory extends GraphFactory with Utilities {
   /** Label for y-axis */
   def label: String

   // Defaults
   val div = "#graph"
   val width = 640
   val height = 480
   val maxy: Any = "undefined"
   val color = "steelblue"
   val margin = "{top: 20, right: 20, bottom: 80, left: 40}"

   /** Data to be displayed in the bar graph */
   def data: List[(String, AnyVal)]

   protected def csvData: String = {
     val lines = data map { case (name, value) =>
       name + "," + value
     }
     "el1,el2\n" + lines.mkString("\n")
   }

   // output to directory "html"
   def writeHtml(): Unit = {
     val html =
       <html>
         <head>
           <link rel="stylesheet" href="resources/stylesheets/bootstrap.css" type="text/css" />
           <link rel="stylesheet" href="resources/stylesheets/base.css" type="text/css" />
         </head>
         <body>
           <div id="graph">&nbsp;</div>
           <script id="csv" type="text/csv">{ csvData }</script>
           <script src="resources/javascript/jquery.js"></script>
           <script src="resources/javascript/bootstrap-twipsy.js"></script>
           <script src="resources/javascript/d3.js"></script>
           <script src="resources/javascript/simplebargraph.js"></script>
           <script type="text/javascript">
             simpleBarGraphTiltedLabels('{ div }', '{ label }', '{ width }', '{ height }', { maxy }, '{ color }', { margin })
           </script>
         </body>
       </html>
     printToFile(new File(new File("html"), name+".html"))(writer => writer.println(html.toString))
   }
 }

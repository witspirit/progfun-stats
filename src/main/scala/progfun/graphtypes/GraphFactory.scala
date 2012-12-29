package progfun.graphtypes


trait GraphFactory {
  /* Name of HTML file containing generated graph */
  val name: String

  def writeHtml(): Unit
}






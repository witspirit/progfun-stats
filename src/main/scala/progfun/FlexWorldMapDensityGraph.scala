package progfun

import graphtypes.FlexWorldMapFactory

object FlexWorldMapDensityGraph extends FlexWorldMapFactory with App{
  /* file name to output to */
  val name = "flexworldmap-density.html"
  writeHtml()
}

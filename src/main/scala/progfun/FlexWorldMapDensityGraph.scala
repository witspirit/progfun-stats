package progfun

import graphtypes.FlexWorldMapFactory

object FlexWorldMapDensityGraph extends FlexWorldMapFactory with App{
  /* file name to output to */
  val name = "worldmap-density.html"
  writeHtml()
}

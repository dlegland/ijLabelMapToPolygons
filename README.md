# Label Map To Polygon	
Simple plugin for ImageJ that converts a label map into a collection of ROIs integrated 
into the RoiManager.

The plugin has few dependencies:

* ImageJ
* JUnit (only for tests)
* MorphoLibJ (for representation of polygons)


## Installation

Simply add the jar file into the "plugins" directory of ImageJ or Fiji.
Then a new option "Region Boundary Polygons" is available in the 
"Plugins -> LabelMaps Utils" menu.

The plugins displays a dialog that allows to choose the connectivity to use 
(can be either 4 or 8), and the pattern of the names of the regions to create.
Default name pattern is "r%03d", generating region names like "r000", "r001"...
When a given region is bounded by several boundaries (regions with holes, 
or regions with multiple disconnected parts), names are suffixed by the
boundary index: "r023-0", "r023-1".


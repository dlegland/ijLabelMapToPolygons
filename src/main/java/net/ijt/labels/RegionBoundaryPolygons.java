/**
 * 
 */
package net.ijt.labels;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Map;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import inra.ijpb.geometry.Polygon2D;

/**
 * Convert to outer boundary of the binary region present within the current
 * image into a polygon, and add this polygon to the list of current ROIs.
 * 
 * @author dlegland
 *
 */
public class RegionBoundaryPolygons implements PlugIn
{
    @Override
    public void run(String arg)
    {
        // retrieve current image
        ImagePlus imagePlus = IJ.getImage();
        ImageProcessor image = imagePlus.getProcessor();
        
        
        // create the dialog, with operator options
        GenericDialog gd = new GenericDialog("Label Maps To Rois");
        gd.addChoice("Connectivity:", new String[] {"C4", "C8"}, "C4");
        gd.addChoice("Vertex Location:", new String[] {"Corners", "Edge Middles", "Pixel Centers"}, "Corners");
        gd.addStringField("Name Pattern", "r%03d");
        
        // wait for user input
        gd.showDialog();
        // If cancel was clicked, do nothing
        if (gd.wasCanceled())
            return;
        
        // parse options
        int conn = gd.getNextChoiceIndex() == 0 ? 4 : 8;
        int locIndex = gd.getNextChoiceIndex();
        BoundaryTracker.VertexLocation loc = BoundaryTracker.VertexLocation.CORNER;
        if (locIndex == 1) loc = BoundaryTracker.VertexLocation.EDGE_CENTER;
        if (locIndex == 2) loc = BoundaryTracker.VertexLocation.PIXEL;
        String pattern = gd.getNextString();
        
        // compute boundaries
        BoundaryTracker tracker = new BoundaryTracker(conn, loc);
        Map<Integer, ArrayList<Polygon2D>> boundaries = tracker.process(image);
        
        // retrieve RoiManager
        RoiManager rm = RoiManager.getInstance();
        if (rm == null)
        {
            rm = new RoiManager();
        }
        
        // populate RoiManager with PolygonRoi
        for (int label : boundaries.keySet())
        {
            ArrayList<Polygon2D> polygons = boundaries.get(label);
            String name = String.format(pattern, label);
            
            if (polygons.size() == 1)
            {
                PolygonRoi roi = createPolygonRoi(polygons.get(0));
                roi.setName(name);
                rm.addRoi(roi);
            }
            else
            {
                int index = 0;
                for (Polygon2D poly : polygons)
                {
                    PolygonRoi roi = createPolygonRoi(poly);
                    roi.setName(name + "-" + (index++));
                    rm.addRoi(roi);
                }
            }
        }
    }
    
    private static final PolygonRoi createPolygonRoi(Polygon2D poly)
    {
        int nv = poly.vertexNumber();
        float[] vx = new float[nv];        
        float[] vy = new float[nv];
        for (int i = 0; i < nv; i++)
        {
            Point2D p = poly.getVertex(i);
            vx[i] = (float) p.getX();
            vy[i] = (float) p.getY();
        }
        return new PolygonRoi(vx, vy, nv, Roi.POLYGON);
    }
}

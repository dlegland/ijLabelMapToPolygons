/**
 * 
 */
package net.ijt.labels;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Map;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.PlugIn;
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
        
        // compute boundaries
        BoundaryTracker tracker = new BoundaryTracker(4);
        Map<Integer, ArrayList<Polygon2D>> boundaries = tracker.process(image);
        
        // find overlay to update
        Overlay ovr = imagePlus.getOverlay();
        if (ovr == null)
        {
            ovr = new Overlay();
        }
        
        // populate overlay with PolygonRoi
        for (int label : boundaries.keySet())
        {
            ArrayList<Polygon2D> polygons = boundaries.get(label);
            for (Polygon2D poly : polygons)
            {
                PolygonRoi roi = createPolygonRoi(poly);
                ovr.add(roi);
            }
        }
        
        imagePlus.setOverlay(ovr);
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

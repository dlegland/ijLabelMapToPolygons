/**
 * 
 */
package net.ijt.labels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.awt.Point;
import java.util.ArrayList;

import org.junit.Test;

import ij.process.ByteProcessor;
import inra.ijpb.data.image.ImageUtils;
import net.ijt.labels.BoundaryTracker.Direction;
import net.ijt.labels.BoundaryTracker.Position;

/**
 * @author dlegland
 *
 */
public class BoundaryTrackerTest
{
    @Test
    public final void test_moveDown_00()
    {
        ByteProcessor array = new ByteProcessor(4, 4);
        array.set(1, 1, 255);
        array.set(2, 1, 255);
        array.set(1, 2, 255);
        array.set(2, 2, 255);
        
        Direction direction = Direction.DOWN;
        Position pos = new Position(1, 2, direction);
        
        Position pos2 = direction.move(pos, array, 4);
        assertEquals(1, pos2.x);
        assertEquals(2, pos2.y);
        assertEquals(Direction.RIGHT, pos2.direction);
    }
    
    @Test
    public final void test_moveDown_01()
    {
        ByteProcessor array = new ByteProcessor(4, 4);
        array.set(1, 1, 255);
        array.set(2, 1, 255);
        array.set(1, 2, 255);
        array.set(2, 2, 255);
        
        Direction direction = Direction.DOWN;
        Position pos = new Position(1, 1, direction);
        
        Position pos2 = direction.move(pos, array, 4);
        assertEquals(1, pos2.x);
        assertEquals(2, pos2.y);
        assertEquals(Direction.DOWN, pos2.direction);
    }
    
    @Test
    public final void test_moveDown_10_C4()
    {
        ByteProcessor array = new ByteProcessor(4, 4);
        array.set(1, 1, 255);
        array.set(2, 2, 255);
        
        Direction direction = Direction.DOWN;
        Position pos = new Position(1, 1, direction);
        
        Position pos2 = direction.move(pos, array, 4);
        assertEquals(1, pos2.x);
        assertEquals(1, pos2.y);
        assertEquals(Direction.RIGHT, pos2.direction);
    }
    
    @Test
    public final void test_moveDown_10_C8()
    {
        ByteProcessor array = new ByteProcessor(4, 4);
        array.set(2, 1, 255);
        array.set(1, 2, 255);
        
        Direction direction = Direction.DOWN;
        Position pos = new Position(2, 1, direction);
        
        Position pos2 = direction.move(pos, array, 8);
        assertEquals(1, pos2.x);
        assertEquals(2, pos2.y);
        assertEquals(Direction.LEFT, pos2.direction);
    }
    
    @Test
    public final void test_moveDown_11()
    {
        ByteProcessor array = new ByteProcessor(4, 4);
        array.set(2, 1, 255);
        array.set(1, 2, 255);
        array.set(2, 2, 255);
        
        Direction direction = Direction.DOWN;
        Position pos = new Position(2, 1, direction);
        
        Position pos2 = direction.move(pos, array, 4);
        assertEquals(1, pos2.x);
        assertEquals(2, pos2.y);
        assertEquals(Direction.LEFT, pos2.direction);
    }
    
    
    /**
     * Test method for {@link net.ijt.labels.BoundaryTracker#trackBoundaryBinary(ij.process.ByteProcessor, int, int, net.ijt.labels.BoundaryTracker.Direction)}.
     */
    @Test
    public final void testTrackBoundaryBinary_singleSquare()
    {
        ByteProcessor array = new ByteProcessor(4, 4);
        array.set(1, 1, 255);
        array.set(2, 1, 255);
        array.set(1, 2, 255);
        array.set(2, 2, 255);
        
        int x0 = 1;
        int y0 = 1;
        BoundaryTracker.Direction initialDirection = BoundaryTracker.Direction.DOWN;
        
        BoundaryTracker tracker = new BoundaryTracker();
        ArrayList<Point> vertices = tracker.trackBoundaryBinary(array, x0, y0, initialDirection);
        
        assertFalse(vertices.isEmpty());
        assertEquals(8, vertices.size());
    }
    
    /**
     * Test method for {@link net.ijt.labels.BoundaryTracker#trackBoundaryBinary(ij.process.ByteProcessor, int, int, net.ijt.labels.BoundaryTracker.Direction)}.
     */
    @Test
    public final void testTrackBoundaryBinary_ExpandedCorners_C4()
    {
        ByteProcessor array = new ByteProcessor(8, 8);
        ImageUtils.fillRect(array, 2, 2, 4, 4, 255);
        ImageUtils.fillRect(array, 1, 1, 2, 2, 255);
        ImageUtils.fillRect(array, 5, 1, 2, 2, 255);
        ImageUtils.fillRect(array, 1, 5, 2, 2, 255);
        ImageUtils.fillRect(array, 5, 5, 2, 2, 255);
//        ImageUtils.print(array);
        
        int x0 = 1;
        int y0 = 1;
        BoundaryTracker.Direction initialDirection = BoundaryTracker.Direction.DOWN;
        
        BoundaryTracker tracker = new BoundaryTracker();
        ArrayList<Point> vertices = tracker.trackBoundaryBinary(array, x0, y0, initialDirection);
        
        assertFalse(vertices.isEmpty());
        assertEquals(32, vertices.size());
    }
    
    /**
     * Test method for {@link net.ijt.labels.BoundaryTracker#trackBoundaryBinary(ij.process.ByteProcessor, int, int, net.ijt.labels.BoundaryTracker.Direction)}.
     */
    @Test
    public final void testTrackBoundaryBinary_ExpandedCorners_C4_TouchBorders()
    {
        ByteProcessor array = new ByteProcessor(6, 6);
        ImageUtils.fillRect(array, 1, 1, 4, 4, 255);
        ImageUtils.fillRect(array, 0, 0, 2, 2, 255);
        ImageUtils.fillRect(array, 4, 0, 2, 2, 255);
        ImageUtils.fillRect(array, 0, 4, 2, 2, 255);
        ImageUtils.fillRect(array, 4, 4, 2, 2, 255);
//        ImageUtils.print(array);
        
        int x0 = 0;
        int y0 = 0;
        BoundaryTracker.Direction initialDirection = BoundaryTracker.Direction.DOWN;
        
        BoundaryTracker tracker = new BoundaryTracker();
        ArrayList<Point> vertices = tracker.trackBoundaryBinary(array, x0, y0, initialDirection);
        
        assertFalse(vertices.isEmpty());
        assertEquals(32, vertices.size());
    }
    
    /**
     * Test method for {@link net.ijt.labels.BoundaryTracker#trackBoundaryBinary(ij.process.ByteProcessor, int, int, net.ijt.labels.BoundaryTracker.Direction)}.
     */
    @Test
    public final void testTrackBoundaryBinary_ExpandedCorners_C8()
    {
        ByteProcessor array = new ByteProcessor(8, 8);
        ImageUtils.fillRect(array, 2, 2, 4, 4, 255);
        ImageUtils.fillRect(array, 1, 1, 2, 2, 255);
        ImageUtils.fillRect(array, 5, 1, 2, 2, 255);
        ImageUtils.fillRect(array, 1, 5, 2, 2, 255);
        ImageUtils.fillRect(array, 5, 5, 2, 2, 255);
//        ImageUtils.print(array);
        
        int x0 = 1;
        int y0 = 1;
        BoundaryTracker.Direction initialDirection = BoundaryTracker.Direction.DOWN;
        
        BoundaryTracker tracker = new BoundaryTracker(8);
        ArrayList<Point> vertices = tracker.trackBoundaryBinary(array, x0, y0, initialDirection);
        
        assertFalse(vertices.isEmpty());
        assertEquals(32, vertices.size());
    }
    
    /**
     * Test method for {@link net.ijt.labels.BoundaryTracker#trackBoundaryBinary(ij.process.ByteProcessor, int, int, net.ijt.labels.BoundaryTracker.Direction)}.
     */
    @Test
    public final void testTrackBoundaryBinary_ExpandedCorners_C8_TouchBorders()
    {
        ByteProcessor array = new ByteProcessor(6, 6);
        ImageUtils.fillRect(array, 1, 1, 4, 4, 255);
        ImageUtils.fillRect(array, 0, 0, 2, 2, 255);
        ImageUtils.fillRect(array, 4, 0, 2, 2, 255);
        ImageUtils.fillRect(array, 0, 4, 2, 2, 255);
        ImageUtils.fillRect(array, 4, 4, 2, 2, 255);
//        ImageUtils.print(array);
        
        int x0 = 0;
        int y0 = 0;
        BoundaryTracker.Direction initialDirection = BoundaryTracker.Direction.DOWN;
        
        BoundaryTracker tracker = new BoundaryTracker(8);
        ArrayList<Point> vertices = tracker.trackBoundaryBinary(array, x0, y0, initialDirection);
        
        assertFalse(vertices.isEmpty());
        assertEquals(32, vertices.size());
    }
    
}

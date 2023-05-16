/**
 * 
 */
package net.ijt.labels;

import java.awt.Point;
import java.util.ArrayList;

import ij.process.ImageProcessor;

/**
 * Track the boundary of a binary or label image to return a single polygon.
 * 
 * @author dlegland
 *
 */
public class BoundaryTracker
{
    /**
     * The connectivity to use for tracking boundary. Should be either 4 or 8.
     * Default is 4.
     */
    int conn = 4;
    
    interface Direction
    {
        public static final Direction RIGHT = new Direction()
        {
            @Override
            public int[][] coordsShifts()
            {
                return new int[][] {{1, 0}, {1, 1}};
            }
            
            @Override
            public Point getVertex(Position pos)
            {
                return new Point(pos.x, pos.y + 1);
            }
            
            @Override
            public Position turnLeft(Position pos)
            {
                return new Position(pos.x, pos.y, UP);
            }

            @Override
            public Position forward(Position pos)
            {
                return new Position(pos.x + 1, pos.y, RIGHT);
            }

            @Override
            public Position turnRight(Position pos)
            {
                return new Position(pos.x + 1, pos.y + 1, DOWN);
            }
        };
        
        public static final Direction UP = new Direction()
        {
            @Override
            public int[][] coordsShifts()
            {
                return new int[][] {{0, -1}, {1, -1}};
            }
            
            @Override
            public Point getVertex(Position pos)
            {
                return new Point(pos.x + 1, pos.y + 1);
            }
            
            @Override
            public Position turnLeft(Position pos)
            {
                return new Position(pos.x, pos.y, LEFT);
            }

            @Override
            public Position forward(Position pos)
            {
                return new Position(pos.x, pos.y - 1, UP);
            }

            @Override
            public Position turnRight(Position pos)
            {
                return new Position(pos.x + 1, pos.y - 1, RIGHT);
            }
        };
        
        public static final Direction LEFT = new Direction()
        {
            @Override
            public int[][] coordsShifts()
            {
                return new int[][] {{-1, 0}, {-1, -1}};
            }
            
            @Override
            public Point getVertex(Position pos)
            {
                return new Point(pos.x + 1, pos.y);
            }
            
            @Override
            public Position turnLeft(Position pos)
            {
                return new Position(pos.x, pos.y, DOWN);
            }

            @Override
            public Position forward(Position pos)
            {
                return new Position(pos.x - 1, pos.y, LEFT);
            }

            @Override
            public Position turnRight(Position pos)
            {
                return new Position(pos.x - 1, pos.y - 1, UP);
            }
        };
        
        public static final Direction DOWN = new Direction()
        {
            @Override
            public int[][] coordsShifts()
            {
                return new int[][] {{0, +1}, {-1, 1}};
            }
            
            @Override
            public Point getVertex(Position pos)
            {
                return new Point(pos.x, pos.y);
            }
            
            @Override
            public Position turnLeft(Position pos)
            {
                return new Position(pos.x, pos.y, RIGHT);
            }

            @Override
            public Position forward(Position pos)
            {
                return new Position(pos.x, pos.y + 1, DOWN);
            }

            @Override
            public Position turnRight(Position pos)
            {
                return new Position(pos.x - 1, pos.y + 1, LEFT);
            }
        };

        /**
         * Returns a 2-by-2 array corresponding to a pair of coordinates shifts,
         * that will be used to access coordinates of next pixels within
         * configuration.
         * 
         * The first coordinates will be the pixel in the continuation of the
         * current direction. The second coordinate will be the pixel in the
         * opposite current 2-by-2 configuration.
         * 
         * @return a 2-by-2 array corresponding to a pair of coordinates shifts.
         */
        public int[][] coordsShifts();
        
        public Point getVertex(Position pos);
        
        /**
         * Keeps current reference pixel and turn direction by +90 degrees in
         * counter-clockwise direction.
         * 
         * @param pos
         *            the position to update
         * @return the new position
         */
        public Position turnLeft(Position pos);

        /**
         * Updates the specified position by iterating by one step in the
         * current direction.
         * 
         * @param pos
         *            the position to update
         * @return the new position
         */
        public Position forward(Position pos);
        
        /**
         * Keeps current reference pixel and turn direction by -90 degrees in
         * counter-clockwise direction.
         * 
         * @param pos
         *            the position to update
         * @return the new position
         */
        public Position turnRight(Position pos);
    }
    
    
    static final class Position
    {
        int x;
        int y;
        Direction direction;
        
        Position(int x, int y, Direction direction)
        {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
        
        public Point getVertex(Position pos)
        {
            return this.direction.getVertex(this);
        }
        
        @Override
        public boolean equals(Object obj)
        {
            // check class
            if (!(obj instanceof Position))
                return false;
            Position that = (Position) obj;
            
            // check each class member
            if (this.x != that.x)
                return false;
            if (this.y != that.y)
                return false;
            if (this.direction != that.direction)
                return false;
            
            // return true when all tests checked
            return true;
        }
    }
    
    /**
     * Default empty constructor, using Connectivity 4.
     */
    public BoundaryTracker()
    {
    }
    
    /**
     * Constructor that allows to specify connectivity.
     * 
     * @param conn
     *            the connectivity to use (must be either 4 or 8)
     */
    public BoundaryTracker(int conn)
    {
        if (conn != 4 && conn != 8)
        {
            throw new IllegalArgumentException(
                    "Connectivity must be either 4 or 8");
        }
        this.conn = conn;
    }
    
    public ArrayList<Point> trackBoundaryBinary(ImageProcessor array, int x0,
            int y0, Direction initialDirection)
    {
        // retrieve image size
        int sizeX = array.getWidth();
        int sizeY = array.getHeight();
        
        // initialize result array
        ArrayList<Point> vertices = new ArrayList<Point>();
        
        // initialize tracking algo state
        int value = (int) array.getf(x0, y0);
        Position pos0 = new Position(x0, y0, initialDirection);
        Position pos = new Position(x0, y0, initialDirection);
        
        // iterate over boundary until we come back at initial position
        do
        {
            vertices.add(pos.getVertex(pos));
            
            // compute position of the two other points in current 2-by-2 configuration
            int[][] shifts = pos.direction.coordsShifts();
            // the pixel in the continuation of current direction
            int xn = pos.x + shifts[0][0];
            int yn = pos.y + shifts[0][1];
            // the pixel in the diagonal position within current configuration
            int xd = pos.x + shifts[1][0];
            int yd = pos.y + shifts[1][1];
            
            // determine configuration of the two pixels in current direction
            // initialize with false, to manage the case of configuration on the
            // border. In any cases, assume that reference pixel in current
            // position belongs to the array.
            boolean b0 = false;
            if (xn >= 0 && xn < sizeX && yn >= 0 && yn < sizeY)
            {
                b0 = ((int) array.getf(xn, yn)) == value;
            }
            boolean b1 = false;
            if (xd >= 0 && xd < sizeX && yd >= 0 && yd < sizeY)
            {
                b1 = ((int) array.getf(xd, yd)) == value;
            }
            
            if (!b0 && (!b1 || conn == 4))
            {
                // corner configuration -> +90 direction
                pos = pos.direction.turnLeft(pos);
            } else if (b1 && (b0 || conn == 8))
            {
                // reentrant corner configuration -> -90 direction
                pos = pos.direction.turnRight(pos);
            } else if (b0 && !b1)
            {
                // straight border configuration -> same direction
                pos = pos.direction.forward(pos);
            } else
            {
                throw new RuntimeException("Should not reach this part...");
            }
            
//            pos = pos.forward(array, value);
        } while (!pos0.equals(pos));
        
        return vertices;
    }
}

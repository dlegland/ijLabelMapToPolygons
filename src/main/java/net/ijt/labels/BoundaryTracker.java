/**
 * 
 */
package net.ijt.labels;

import java.awt.Point;
import java.util.ArrayList;

import ij.process.ByteProcessor;
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
     * The connectivity to use for tracking boundary.
     */
    int conn = 4;
    
    interface Direction
    {
        public Point getVertex(Position pos);
        
        public Position move(Position pos, ImageProcessor array, int value, int conn);
        
        /**
         * @return the next direction when rotation +90 degrees
         *         counter-clockwise.
         */
        public Direction next();

        /**
         * @return the next direction when rotation -90 degrees
         *         counter-clockwise.
         */
        public Direction previous();
        
        public static final Direction RIGHT = new Direction()
        {
            @Override
            public Point getVertex(Position pos)
            {
                return new Point(pos.x, pos.y + 1);
            }

            @Override
            public Position move(Position pos, ImageProcessor array, int value, int conn)
            {
                // position of the point in the opposite direction within current configuration
                int xn = pos.x + 1;
                int yn = pos.y + 1;
                
                // determine configuration of the two pixels in current direction
                // initialize with false, to manage the case of configuration on the border 
                // (assume false outside)
                boolean b0 = false, b1 = false;
                if (xn < array.getWidth())
                {
                    b0 = ((int) array.getf(xn, pos.y)) == value;
                    b1 = yn < array.getHeight() ? ((int) array.getf(xn, yn)) == value : false;
                }
                
                if (!b0 && (!b1 || conn == 4))
                {
                    // corner configuration -> +90 direction
                    return new Position(pos.x, pos.y, next());
                }
                else if (b1 && (b0 || conn == 8))
                {
                    // reentrant corner configuration -> -90 direction
                    return new Position(pos.x + 1, pos.y + 1, previous());
                }
                else if (b0 && !b1)
                {
                    // straight border configuration -> same direction
                    return new Position(pos.x + 1, pos.y, this);
                }
                else
                {
                    throw new RuntimeException("Should not reach this part...");
                }
            }

            @Override
            public Direction next()
            {
                return UP;
            }

            @Override
            public Direction previous()
            {
                return DOWN;
            }
        };
        
        public static final Direction UP = new Direction()
        {
            @Override
            public Point getVertex(Position pos)
            {
                return new Point(pos.x + 1, pos.y + 1);
            }

            @Override
            public Position move(Position pos, ImageProcessor array, int value, int conn)
            {
                // position of the point in the opposite direction within current configuration
                int xn = pos.x + 1;
                int yn = pos.y - 1;
                
                // determine configuration of the two pixels in current direction
                // initialize with false, to manage the case of configuration on the border 
                // (assume false outside)
                boolean b0 = false, b1 = false;
                if (yn >= 0)
                {
                    b0 = ((int) array.getf(pos.x, yn)) == value;
                    b1 = xn < array.getWidth() ? ((int) array.getf(xn, yn)) == value : false;
                }
                
                if (!b0 && (!b1 || conn == 4))
                {
                    // corner configuration -> +90 direction
                    return new Position(pos.x, pos.y, next());
                }
                else if (b1 && (b0 || conn == 8))
                {
                    // reentrant corner configuration -> -90 direction
                    return new Position(pos.x + 1, pos.y - 1, previous());
                }
                else if (b0 && !b1)
                {
                    // straight border configuration -> same direction
                    return new Position(pos.x, pos.y - 1, this);
                }
                else
                {
                    throw new RuntimeException("Should not reach this part...");
                }
            }
            
            @Override
            public Direction next()
            {
                return LEFT;
            }

            @Override
            public Direction previous()
            {
                return RIGHT;
            }
        };
        
        public static final Direction LEFT = new Direction()
        {
            @Override
            public Point getVertex(Position pos)
            {
                return new Point(pos.x + 1, pos.y);
            }

            @Override
            public Position move(Position pos, ImageProcessor array, int value, int conn)
            {
                // position of the point in the opposite direction within current configuration
                int xn = pos.x - 1;
                int yn = pos.y - 1;
                
                // determine configuration of the two pixels in current direction
                // initialize with false, to manage the case of configuration on the border 
                // (assume false outside)
                boolean b0 = false, b1 = false;
                if (xn >= 0)
                {
                    b0 = ((int) array.get(xn, pos.y)) == value;
                    b1 = yn >= 0 ? ((int) array.getf(xn, yn)) == value : false;
                }
                
                if (!b0 && (!b1 || conn == 4))
                {
                    // corner configuration -> +90 direction
                    return new Position(pos.x, pos.y, next());
                }
                else if (b1 && (b0 || conn == 8))
                {
                    // reentrant corner configuration -> -90 direction
                    return new Position(pos.x - 1, pos.y - 1, previous());
                }
                else if (b0 && !b1)
                {
                    // straight border configuration -> same direction
                    return new Position(pos.x - 1, pos.y, this);
                }
                else
                {
                    throw new RuntimeException("Should not reach this part...");
                }
            }
            
            @Override
            public Direction next()
            {
                return DOWN;
            }

            @Override
            public Direction previous()
            {
                return UP;
            }
        };
        
        public static final Direction DOWN = new Direction()
        {

            @Override
            public Point getVertex(Position pos)
            {
                return new Point(pos.x, pos.y);
            }

            @Override
            public Position move(Position pos, ImageProcessor array, int value, int conn)
            {
                // position of the point in the opposite direction within current configuration
                int xn = pos.x - 1;
                int yn = pos.y + 1;
                
                // determine configuration of the two pixels in current direction
                // initialize with false, to manage the case of configuration on the border 
                // (assume false outside)
                boolean b0 = false, b1 = false;
                if (yn < array.getHeight())
                {
                    b0 = ((int) array.getf(pos.x, yn)) == value;
                    b1 = xn >= 0 ? ((int) array.getf(xn, yn)) == value : false;
                }
                
                if (!b0 && (!b1 || conn == 4))
                {
                    // corner configuration -> +90 direction
                    return new Position(pos.x, pos.y, next());
                }
                else if (b1 && (b0 || conn == 8))
                {
                    // reentrant corner configuration -> -90 direction
                    return new Position(pos.x - 1, pos.y + 1, previous());
                }
                else if (b0 && !b1)
                {
                    // straight border configuration -> same direction
                    return new Position(pos.x, pos.y + 1, this);
                }
                else
                {
                    throw new RuntimeException("Should not reach this part...");
                }
            }
            
            @Override
            public Direction next()
            {
                return RIGHT;
            }

            @Override
            public Direction previous()
            {
                return LEFT;
            }
        };
    }
    
    
    static class Position
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
        
        public Position forward(ImageProcessor array, int value, int conn)
        {
            return this.direction.move(this, array, value, conn);
        }
        
        @Override
        public boolean equals(Object obj)
        {
            // check class
            if (!(obj instanceof Position))
                return false;
            Position that = (Position) obj;
            
            // check each class member
            if (this.x != that.x) return false;
            if (this.y != that.y) return false;
            if (this.direction != that.direction) return false;
            
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
            throw new IllegalArgumentException("Connectivity must be either 4 or 8");
        }
        this.conn = conn;
    }
    
    public ArrayList<Point> trackBoundaryBinary(ByteProcessor array, int x0, int y0, Direction initialDirection)
    {
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
            pos = pos.forward(array, value, this.conn);
        } while (!pos0.equals(pos));
        
        return vertices;
    }
}

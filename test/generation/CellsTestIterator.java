package generation;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

public class CellsTestIterator {
   
    /**
     * Test case: See if iterator works on cells with no walls
     * <p>
     * Method under test: iterator
     * <p>
     * If there are no walls, iterator should provide no sequences.
     * Test uses fixed width and height but iterates through
     * all positions on the maze and exercises all directions.
     */
    @Test
    public final void testIteratorEmptyCells() {
        // create a cells object we can test the iterator on
        int width = 4;
        int height = 5;
        Cells cells = new Cells(width,height);
        // check vertical wall, y coordinate changes, x stays
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (CardinalDirection cd: CardinalDirection.values()) {
                    Iterator<int[]> it = cells.iterator(x, y, cd);
                    assertFalse(it.hasNext()); // no walls, no sequences
                }
            }
        }
        
        
    }
    /**
     * Test case: See if iterator works on cells with all walls up
     * <p>
     * Method under test: iterator
     * <p>
     * If all walls are up, iterator should return sequences of length 1
     * because we hit a crossing wall after each step.
     * Test uses fixed width and height but iterates through
     * all positions on the maze and exercises all directions.
     */
    @Test
    public final void testIteratorCellsAllWallsUp() {
        // create a cells object we can test the iterator on
        int width = 4;
        int height = 5;
        Cells cells = new Cells(width,height);
        cells.initialize();
        // check vertical wall, y coordinate changes, x stays
        CardinalDirection cdBlocked;
        int limit;
        for (CardinalDirection cd: CardinalDirection.values()) {
            // pick the wall that would create a corner such that iterator stops
            // set the limit for the coordinate that changes
            if (cd == CardinalDirection.East || cd == CardinalDirection.West) {
                cdBlocked = CardinalDirection.North;
                limit = height;
            }
            else {
                cdBlocked = CardinalDirection.West;
                limit = width;
            }
            // check iterator for each possible starting location
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Iterator<int[]> it = cells.iterator(x, y, cd);
                    assertTrue(it.hasNext()); // there must be at least one sequence even at outmost position
                    int[] seq;
                    while (it.hasNext()) {
                        seq = it.next();
                        if (cd == CardinalDirection.East || cd == CardinalDirection.West) {
                            isSequenceOfVerticalWalls(cells, cdBlocked, limit, cd, x, seq);
                        }
                        else {
                            isSequenceOfHorizontalWalls(cells, cdBlocked, limit, cd, y, seq);
                        }
                        // length should be 1
                        assertEquals(seq[0]+1,seq[1]);
                    }
                }
            }
        }


    }
    /**
     * Checks if the given interval seq describes a continuous sequence of walls
     * @param cells that carry the walls
     * @param cdBlocked the direction we could see a blocking wall (perpendicular)
     * @param limit the max value for the end position for the y coordinate
     * @param cd the direction we expect to have a wall
     * @param x coordinate, the column in cells
     * @param seq gives two y coordinate values, start and end
     */
    private void isSequenceOfVerticalWalls(Cells cells, CardinalDirection cdBlocked,
            int limit, CardinalDirection cd, int x, int[] seq) {
        // for east, there should be a wall at (x,start)
        assertTrue(cells.hasWall(x,seq[0],cd));
        for (int i = seq[0]; i < seq[1]; i++) {
            assertTrue(cells.hasWall(x,i,cd));
        }
        // we either end at an index that is 
        // width or has no wall in this direction
        // or has a blocking wall
        // TODO: check if this makes sense, corner should be at seq[1]-1
        // note: adjusted by picked opposite direction for seq[1] for cdBlocked
        // as walls are symmetric (represented in both adjacent cells)
        assertTrue(seq[1] == limit ||
                cells.hasNoWall(x,seq[1], cd) ||
                (cells.hasWall(x, seq[1], cdBlocked) &&
                        cells.hasWall(x, seq[1]-1, cdBlocked.oppositeDirection())));
    }
    /**
     * Checks if the given interval seq describes a continuous sequence of walls
     * @param cells that carry the walls
     * @param cdBlocked the direction we could see a blocking wall (perpendicular)
     * @param limit the max value for the end position for the y coordinate
     * @param cd the direction we expect to have a wall
     * @param y coordinate, the row in cells
     * @param seq gives two x coordinate values, start and end
     */
    private void isSequenceOfHorizontalWalls(Cells cells, CardinalDirection cdBlocked,
            int limit, CardinalDirection cd, int y, int[] seq) {
        // for east, there should be a wall at (x,start)
        assertTrue(cells.hasWall(seq[0],y,cd));
        for (int i = seq[0]; i < seq[1]; i++) {
            assertTrue(cells.hasWall(i,y,cd));
        }
        // we either end at an index that is 
        // width or has no wall in this direction
        // or has a blocking wall
        // TODO: check if this makes sense, corner should be at seq[1]-1
        // note: adjusted by picked opposite direction for seq[1] for cdBlocked
        // as walls are symmetric (represented in both adjacent cells)
        assertTrue(seq[1] == limit ||
                cells.hasNoWall(seq[1],y, cd) ||
                (cells.hasWall(seq[1],y, cdBlocked) &&
                        cells.hasWall(seq[1]-1,y, cdBlocked.oppositeDirection())));
    }
    
    
    /**
     * Test case: See if iterator works on cells with sequences of same length
     * <p>
     * Method under test: iterator
     * <p>
     * Test starts with a cell where all walls are up and then deletes
     * walls in a regular manner such that all sequence should be at the same
     * fixed length.
     * The gap between sequences is always of same length as well.
     * Test uses fixed width and height but iterates through
     * all positions on the maze and exercises all directions.
     */
    @Test
    public final void testIteratorCellsAllSequencesSameVertical() {
        // create a cells object we can test the iterator on
        final int seqLength = 3;
        final int gapLength = 2;
        final int total = 3;
        // height must == (seqLength+gapLength)* (#gaps) + seqLength
        final int height = (seqLength+gapLength)*total + seqLength;
        final int width = 4;
        final Cells cells = new Cells(width,height);
        cells.initialize();
        // to start with a sequence of walls and end with a sequence of walls
        punchHolesEast(cells,width,height,seqLength,gapLength);
        // check vertical wall, y coordinate changes, x stays
        CardinalDirection cd = CardinalDirection.East;
        // pick the wall that would create a corner such that iterator stops
        CardinalDirection cdBlocked = CardinalDirection.North;
        // set the limit for the coordinate that changes
        int limit = height;
        
        // check iterator for each possible starting location
        for (int x = 0; x < width-1; x++) {
            int y = 0; // start y always at 0
            int c = 0; // count the sequences
            Iterator<int[]> it = cells.iterator(x, y, cd);
            assertTrue(it.hasNext()); // there must be at least one sequence
            int[] seq;
            while (it.hasNext()) {
                seq = it.next();
                c++;
                isSequenceOfVerticalWalls(cells, cdBlocked, limit, cd, x, seq);
                // length should be same and as given
                assertEquals(seqLength,seq[1]-seq[0]);
            }
            assertEquals("Total number of sequences per column",total+1,c);
        }
        // test same property for opposite direction, here west
        cd = cd.oppositeDirection();
        // note: bounds for iteration change
        // column 0 has border on west, column width-1 has still
        // horizontal walls
        for (int x = 1; x < width-1; x++) {
            int y = 0; // start y always at 0
            int c = 0; // count the sequences
            Iterator<int[]> it = cells.iterator(x, y, cd);
            assertTrue(it.hasNext()); // there must be at least one sequence
            int[] seq;
            while (it.hasNext()) {
                seq = it.next();
                c++;
                isSequenceOfVerticalWalls(cells, cdBlocked, limit, cd, x, seq);
                // length should be same and as given
                assertEquals("x="+x+",y="+y+",s0="+seq[0]+",s1="+seq[1], seqLength,seq[1]-seq[0]);
            }
            assertEquals("Total number of sequences per column",total+1,c);
        }
    }
    /**
     * All columns treated the same, start with a sequence of walls,
     * then a gap, then a sequence and so forth.
     * @param cells
     * @param width
     * @param height
     * @param seqLength
     * @param gapLength
     */
    private void punchHolesEast(final Cells cells, final int width, final int height, final int seqLength, final int gapLength) {
        int c = 0;
        boolean gap = false;
        Wall wall = new Wall(0,0,CardinalDirection.East); // initial values don't matter 
        for (int x = 0; x < width-1; x++) {
            c = 0;
            gap = false;
            for (int y = 0; y < height; y++) {
                // remove all horizontal walls
                if (y < height-1) {
                    wall.setWall(x, y, CardinalDirection.South);
                    cells.deleteWall(wall);
                }
                // remove some vertical walls
                if (gap) {
                    if (c < gapLength) {
                        wall.setWall(x, y, CardinalDirection.East);
                        cells.deleteWall(wall);
                        c++;
                    }
                    else {
                        c=1;
                        gap=false;
                    }
                }
                else {
                    if (c < seqLength) {
                        c++;
                    }
                    else {
                        c=1;
                        gap=true;
                        wall.setWall(x, y, CardinalDirection.East);
                        cells.deleteWall(wall);
                    }
                }
            }
        }   
    }

}

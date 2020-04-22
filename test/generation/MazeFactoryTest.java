package generation;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import generation.Order.Builder;

/**
 * @author Meg Anderson and Trenten Ross
 * These tests are created to exercises functionality of MazeFactory.java to 
 * check that the computed MazeConfiguration (or MazeContainer) has all 
 * properties of a correct maze. These are black box tests which could be
 * edited to be used for all MazeBuilders, although they currently use Ellers
 * with Prims and DFS as comparison algorithms. For each test we test mazes of up
 * to skill level 9 in each test. This means our test cases take a long time 
 * to run, and so we decided that up to 9 would work as representative of up to 15.
 */
public class MazeFactoryTest {
	
	/** This is a test to ensure that two mazes of the same difficulty level, which are not deterministic,
	 * still have different paths through them. We check whether or not they are different through
	 * comparing their distance arrays. They should not have identical distance matrixes  */
	@Test
	public final void testRandomness(){
		//Here we set our builder to use Eller, because that algorithm is what we personally care about, 
		//but this could be any of the algorithms. All skills are checked.
		
		for (int i = 0; i < 10; i ++) {	
			
			StubOrder order1 = new StubOrder(Builder.Eller, i, true, false);  
			MazeConfiguration con1 = order1.order();
			
			StubOrder order2 = new StubOrder(Builder.Eller, i, true, false);
			MazeConfiguration con2 = order2.order();
			
			int[][] dist1 = (con1.getMazedists()).getDists();
			int[][] dist2 = (con2.getMazedists()).getDists();
			
			assertFalse(Arrays.deepEquals(dist1, dist2));
		}
	}
	
	/** This is a test to ensure that two mazes of the same difficulty level, which are deterministic,
	 * have the same path through them. We check whether or not they are the same through
	 * comparing their distance arrays. This also ensures that mazes of the same difficulty are the 
	 * same size. They should have identical distance matrixes.  */
	@Test
	public final void testDeterministic(){
		//Here we set our builder to use Eller, because that algorithm is what we personally care about, 
		//but this could be any of the algorithms. All skills are checked.
		
		for (int i = 0; i < 10; i ++) {	
			
			StubOrder order1 = new StubOrder(Builder.Eller, i, true, true);  
			MazeConfiguration con1 = order1.order();
			
			StubOrder order2 = new StubOrder(Builder.Eller, i, true, true);
			MazeConfiguration con2 = order2.order();
			
			int[][] dist1 = (con1.getMazedists()).getDists();
			int[][] dist2 = (con2.getMazedists()).getDists();
			
			Cells cells1 = con1.getMazecells();
			Cells cells2 = con2.getMazecells();
			
			assertTrue(Arrays.deepEquals(dist1, dist2));
			assertEquals(cells1, cells2);
		}
	}
	
	/** This test ensures that the exit position is located on the outside border of a maze.
	 * And that an exit actually exists.*/
	@Test
	public final void testExitPositions() {
		for (int i = 0; i < 10; i ++) {	
			
			StubOrder order1 = new StubOrder(Builder.Eller, i, true, true);  
			MazeConfiguration con1 = order1.order();
			
			int[] exitPosition = (con1.getMazedists()).getExitPosition();
			
			//Check if it's on the vertical borders
			if ((exitPosition[0] != 0) && (exitPosition[0] != con1.getWidth() - 1)) {
				
			//Check if it's on the horizontal border
				if ((exitPosition[1] != 0) && (exitPosition[1] != con1.getHeight() - 1)) 
					fail();
			}
		}
	}
	
	/** This test ensures that for all interior cells there is a cell with a lower
	 * distance to the exit. Therefore, these perfect mazes are all solvable. */
	@Test
	public final void testIsExitWORooms() {
		for (int i = 0; i < 10; i ++) {
			StubOrder order1 = new StubOrder(Builder.Eller, i, true, false);  
			MazeConfiguration con1 = order1.order();
			int[][] dist1 = (con1.getMazedists()).getDists();
			boolean path;
			int value;
			
			for (int r = 1; r < con1.getWidth() - 1; r ++ ) {
				for (int c = 1; c < con1.getHeight() - 1; c ++) {
					
					value = dist1[r][c];
					path = false;
					
					for (CardinalDirection dir : CardinalDirection.values()) {
					
						if ((dir == CardinalDirection.North) && (dist1[r-1][c] < value)){
							path = true;
						}
						
						if ((dir == CardinalDirection.East) && (dist1[r][c+1] < value)){
							path = true;
						}
						
						if ((dir == CardinalDirection.South) && (dist1[r + 1][c] < value)){
							path = true;
						}
						
						if ((dir == CardinalDirection.West) && (dist1[r][c-1] < value)){
							path = true;
						}
					}
					if (path == false) fail();
				}
			}
		}
	}
	
	/** This test ensures that for all interior cells there is a cell with a lower
	 * distance to the exit. Therefore, these imperfect (with rooms) mazes are 
	 * all solvable. This implicitly tests generate and place rooms.  */
	@Test
	public final void testIsExitWRooms() {
		for (int i = 0; i < 10; i ++) {
			StubOrder order1 = new StubOrder(Builder.Eller, i, false, false);  
			MazeConfiguration con1 = order1.order();
			int[][] dist1 = (con1.getMazedists()).getDists();
			boolean path;
			int value;
			
			for (int r = 1; r < con1.getWidth() - 1; r ++ ) {
				for (int c = 1; c < con1.getHeight() - 1; c ++) {
					
					value = dist1[r][c];
					path = false;
					
					for (CardinalDirection dir : CardinalDirection.values()) {
					
						if ((dir == CardinalDirection.North) && (dist1[r-1][c] < value)){
							path = true;
						}
						
						if ((dir == CardinalDirection.East) && (dist1[r][c+1] < value)){
							path = true;
						}
						
						if ((dir == CardinalDirection.South) && (dist1[r + 1][c] < value)){
							path = true;
						}
						
						if ((dir == CardinalDirection.West) && (dist1[r][c-1] < value)){
							path = true;
						}
					}
					if (path == false) fail();
				}
			}
		}
	}
	
	/** This checks mazes of the same difficulty are the same size. */
	@Test
	public final void testSameSizeIfSameDifficulty() {
		for (int i = 0; i < 10; i ++) {	
			
			StubOrder order1 = new StubOrder(Builder.Eller, i, true, false);  
			MazeConfiguration con1 = order1.order();
			
			StubOrder order2 = new StubOrder(Builder.Eller, i, true, false);
			MazeConfiguration con2 = order2.order();
			
			int[][] dist1 = (con1.getMazedists()).getDists();
			int[][] dist2 = (con2.getMazedists()).getDists();
			
			for (int r = 0; r < dist1.length; r++ ) {
				assertTrue(dist1[r].length == dist2[r].length);
			}

		}
	}
	
	/**This tests to makes sure we actually use Eller's like Ever, by comparing these mazes
	 * to deterministic Prim's and DFS mazes which still should be different.*/
	@Test
	public final void isEllersEvenReal() {
		for (int i = 0; i < 10; i ++) {	
			
			StubOrder order1 = new StubOrder(Builder.Eller, i, true, true);  
			MazeConfiguration con1 = order1.order();
			
			StubOrder order2 = new StubOrder(Builder.Prim, i, true, true);
			MazeConfiguration con2 = order2.order();
			
			StubOrder order3 = new StubOrder(Builder.DFS, i, true, true);
			MazeConfiguration con3 = order3.order();
			
			int[][] dist1 = (con1.getMazedists()).getDists();
			int[][] dist2 = (con2.getMazedists()).getDists();
			int[][] dist3 = (con3.getMazedists()).getDists();
			
			assertFalse(Arrays.deepEquals(dist1, dist2));
			assertFalse(Arrays.deepEquals(dist1, dist3));
		}
	}
	
	/**This test determines that there are no isolations in the code. The default distance calculation
	 * is Integer's max value*/
	@Test
	public final void isolations() {
		for (int i = 0; i < 10; i ++) {
			
			StubOrder order1 = new StubOrder(Builder.Eller, i, true, true);  
			MazeConfiguration con1 = order1.order();
			int[][] dist1 = (con1.getMazedists()).getDists();
			
			for (int r = 0; r < con1.getWidth(); r ++ ) {
				for (int c = 0; c < con1.getHeight(); c ++) {
					assertTrue(dist1[r][c] != Integer.MAX_VALUE);
				}
			}
		}
	}
}


/* To generate a new maze:
 * 	1. Create stub order and set:
 * 		a. builder
 * 		b. deterministic
 * 		c. skill
 * 		d. perfect
 * 	2. Tell it to start generating the maze
 * 		a. Call stub.factory.order(stub)
 * 			- stub's factory creates new thread
 * 	3. Call stub.factory.waitTillDelivered()
 * 			- when finished, factory's builder calls stub.deliver,
 * 			  which updates mazeConfig to the new maze
 * 	4. MazeConfiguration maze = stub.mazeConfig
 * 
 * Actually, just wrote steps 2-4 into a method stub.order() 
 * 
 */



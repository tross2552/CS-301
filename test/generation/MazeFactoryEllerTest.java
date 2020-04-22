package generation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

/**
 * These are whitebox tests cases for the functionality of MazeBuilderEller. All pass. 
 * @author  Meg Anderson and Trenten Ross
 * @version 2.0
 * @since   2017-09-23
 */
public class MazeFactoryEllerTest { 
	

/*Methods of MazeFactoryEller 
 * 1. MergeRight
 * 2. MergeDown
 * 3. MakeSets
 * 4. MergelastRow*/
	
	/**This tests the createNewSets method in it's most basic state: that the first row has
	 * a unique integer value in each cell starting at 1. */
	
	@Test
	public final void testCreateNewSets() {
		MazeBuilderEller test = new MazeBuilderEller();
		test.height = 10;
		test.width = 10;
		test.cells = new Cells(test.width, test.height);
		test.cells.initialize();
		test.cR = new int[test.width];
		test.createNewSets(0);
		int[] compare = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		assert(Arrays.equals(test.cR, compare));
	}
	
	/**This tests the createNewSets method ability to skip over existing sets.
	 * Therefore if a cell already has a value, the count should continue contiguously over
	 * that value without changing it.  */
	
	@Test
	public final void testCreateNewSetsWithExistingSets() {
		MazeBuilderEller test = new MazeBuilderEller();
		test.height = 10;
		test.width = 10;
		test.cells = new Cells(test.width, test.height);
		test.cells.initialize();
		test.cR = new int[test.width];
		test.cR[1] = 11;
		int[] compare = {0, 11, 0, 0, 0, 0, 0, 0, 0, 0};
		assert(Arrays.equals(test.cR, compare));
		test.createNewSets(0);
		int[] compare2 = {1, 11, 2, 3, 4, 5, 6, 7, 8, 9};
		assert(Arrays.equals(test.cR, compare2));
	}
	
	/**This tests the createNewSets method ability to handle end behavior. It gives a row
	 * that has sets only in the first and last positions, and ensures that the method
	 * correctly fills in the rows between them. */
	
	@Test
	public final void testCreateNewSetsWithExistingSetsEndBehavior() {
		MazeBuilderEller test = new MazeBuilderEller();
		test.height = 10;
		test.width = 10;
		test.cells = new Cells(test.width, test.height);
		test.cells.initialize();
		test.cR = new int[test.width];
		test.cR[0] = 11;
		test.cR[9] = 12;
		int[] compare = {11, 0, 0, 0, 0, 0, 0, 0, 0, 12};
		assert(Arrays.equals(test.cR, compare));
		test.createNewSets(0);
		int[] compare2 = {11, 1, 2, 3, 4, 5, 6, 7, 8, 12};
		assert(Arrays.equals(test.cR, compare2));
	}
	
	/**This tests the mergeRightwards methods ability correctly place values in the same set based on
	 * an array list of positions. If two cells are placed in the same set they should contain the same 
	 * integer value. */
	
	@Test
	public final void testMergeRightwards() {
		MazeBuilderEller test = new MazeBuilderEller();
		test.height = 10;
		test.width = 10;
		test.cells = new Cells(test.width, test.height);
		test.cells.initialize();
		test.cR = new int[test.width];
		test.createNewSets(0);
		ArrayList<Integer> moves = new ArrayList<>(5);
		for(int i = 0; i < test.cR.length; i = i + 2) {
			moves.add(i);
		}
		int[] compare = {1, 1, 3, 3, 5, 5, 7, 7, 9, 9};
		test.mergeRightwards(0, moves);
		assert(Arrays.equals(test.cR, compare));
	}
	
	/**This tests the mergeRightwards methods ability correctly place values in the same set based on
	 * an array list of positions, even if the array has existing complex sets.  */
	
	@Test
	public final void testMergeRightwardsExtended() {
		MazeBuilderEller test = new MazeBuilderEller();
		test.height = 10;
		test.width = 10;
		test.cells = new Cells(test.width, test.height);
		test.cells.initialize();
		test.cR = new int[test.width];
		test.createNewSets(0);
		ArrayList<Integer> moves = new ArrayList<>();
		for(int i = 0; i < test.cR.length; i = i + 2) {
			moves.add(i);
		}
		test.mergeRightwards(0, moves);
		int[] compare = {1, 1, 3, 3, 5, 5, 7, 7, 9, 9};
		assert(Arrays.equals(test.cR, compare));
		
		ArrayList<Integer> moves2 = new ArrayList<>();
		moves2.add(1);
		moves2.add(5);
		test.mergeRightwards(0, moves2);
		int[] compare2 = {1, 1, 1, 1, 5, 5, 5, 5, 9, 9};
		assert(Arrays.equals(test.cR, compare2));
		
	}
	
	/**This tests the mergeRightwards methods ability correctly place values in the same set based on
	 * an array list of positions, even if the array has existing complex sets. It also has the ability
	 * to merge an entire row. */
	
	@Test
	public final void testMergeRightwardsExtended2() {
		MazeBuilderEller test = new MazeBuilderEller();
		test.height = 10;
		test.width = 10;
		test.cells = new Cells(test.width, test.height);
		test.cells.initialize();
		test.cR = new int[test.width];
		test.createNewSets(0);
		ArrayList<Integer> moves = new ArrayList<>();
		for(int i = 0; i < test.cR.length; i = i + 2) {
			moves.add(i);
		}
		test.mergeRightwards(0, moves);
		int[] compare = {1, 1, 3, 3, 5, 5, 7, 7, 9, 9};
		assert(Arrays.equals(test.cR, compare));
		
		ArrayList<Integer> moves2 = new ArrayList<>();
		moves2.add(1);
		moves2.add(5);
		test.mergeRightwards(0, moves2);
		int[] compare2 = {1, 1, 1, 1, 5, 5, 5, 5, 9, 9};
		assert(Arrays.equals(test.cR, compare2));
		
		ArrayList<Integer> moves3 = new ArrayList<>();
		moves3.add(3);
		test.mergeRightwards(0, moves3);
		int[] compare3 = {1, 1, 1, 1, 1, 1, 1, 1, 9, 9};
		assert(Arrays.equals(test.cR, compare3));
		
		ArrayList<Integer> moves4 = new ArrayList<>();
		moves4.add(7);
		test.mergeRightwards(0, moves4);
		int[] compare4 = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		assert(Arrays.equals(test.cR, compare4));
		
	}
	
	/**This tests the mergeRightwards methods ability to pass over the same spot twice, as is 
	 * possible given randomPositions, and not throw an error.   */

	@Test
	public final void testMergeRightwardsSamePositionTwice() {
		MazeBuilderEller test = new MazeBuilderEller();
		test.height = 10;
		test.width = 10;
		test.cells = new Cells(test.width, test.height);
		test.cells.initialize();
		test.cR = new int[test.width];
		test.createNewSets(0);
		ArrayList<Integer> moves = new ArrayList<>();
		for(int i = 0; i < test.cR.length; i = i + 2) {
			moves.add(i);
		}
		test.mergeRightwards(0, moves);
		int[] compare = {1, 1, 3, 3, 5, 5, 7, 7, 9, 9};
		assert(Arrays.equals(test.cR, compare));
		
		ArrayList<Integer> moves2 = new ArrayList<>();
		moves2.add(0);
		test.mergeRightwards(0, moves2);
		assert(Arrays.equals(test.cR, compare));
		
		
	}
	
	/**This tests the mergeDownwards ability to correctly merge down every individual set. In this case
	 * all cells are their own set because no horizontal connections have been made, and so the nR should be 
	 * identical to the cR.  */
	
	@Test
	public final void testMergeDownwardsBasic() {
		MazeBuilderEller test = new MazeBuilderEller();
		test.height = 10;
		test.width = 10;
		test.cells = new Cells(test.width, test.height);
		test.cells.initialize();
		test.cR = new int[test.width];
		test.nR = new int[test.width];
		test.createNewSets(0);
		ArrayList<Integer> moves = new ArrayList<>();
		for(int i = 0; i < test.cR.length; i ++) {
			moves.add(i);
		}
		test.mergeDownwards(0, moves);
		int[] compare = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		assert(Arrays.equals(test.nR, compare));	
	}
	
	/**This tests the mergeDownwards ability to merge every set at least once in a random Position.   */
	
	@Test
	public final void testMergeDownwardsRandom() {
		MazeBuilderEller test = new MazeBuilderEller();
		test.height = 10;
		test.width = 10;
		test.cells = new Cells(test.width, test.height);
		test.cells.initialize();
		test.cR = new int[test.width];
		test.nR = new int[test.width];
		test.createNewSets(0);
		ArrayList<Integer> moves = new ArrayList<>();
		for(int i = 0; i < test.cR.length; i = i + 2) {
			moves.add(i);
		}
		test.generateDownPositions(moves);
		test.mergeDownwards(0, moves);
		for(int i = 0; i < test.cR.length; i = i + 2) {
			assert(test.nR[i]!=0 || test.nR[i+1]!=0);	
		}
		
	}
	
	/**This tests the mergeDownwards ability to correctly merge the first and last cells.   */
	
	@Test
	public final void testMergeDownwardsEndBehavior() {
		MazeBuilderEller test = new MazeBuilderEller();
		test.height = 10;
		test.width = 10;
		test.cells = new Cells(test.width, test.height);
		test.cells.initialize();
		test.cR = new int[test.width];
		test.nR = new int[test.width];
		test.createNewSets(0);
		ArrayList<Integer> moves = new ArrayList<>();
		moves.add(0);
		moves.add(9);
		test.mergeDownwards(0, moves);
		int[] compare = {1, 0, 0, 0, 0, 0, 0, 0, 0, 10};
		assert(Arrays.equals(test.nR, compare));
		
	}
	
	/**This tests the mergeLastRow's ability to correctly merge all unique sets in the last row, using
	 * a call to mergeRightwards. In this case, every cell is a unique set so all cells should be 
	 * merged.    */
	
	@Test
	public final void testMergeLastRowBasic() {
		MazeBuilderEller test = new MazeBuilderEller();
		test.height = 10;
		test.width = 10;
		test.cells = new Cells(test.width, test.height);
		test.cells.initialize();
		test.cR = new int[test.width];
		test.createNewSets(0);
		int[] compare = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		assert(Arrays.equals(test.cR, compare));
		
		test.mergeLastRow();
		int[] compare2 = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		assert(Arrays.equals(test.cR, compare2));
		
	}
	
	/**This tests the mergeLastRow's ability to correctly merge all unique sets in the last row, using
	 * a call to mergeRightwards. In this case, every cell is not a unique set but all cells should be 
	 * merged still.    */
	
	@Test
	public final void testMergeLastRowExtended() {
		MazeBuilderEller test = new MazeBuilderEller();
		test.height = 10;
		test.width = 10;
		test.cells = new Cells(test.width, test.height);
		test.cells.initialize();
		test.cR = new int[test.width];
		test.createNewSets(0);
		ArrayList<Integer> moves = new ArrayList<>(5);
		for(int i = 0; i < test.cR.length; i = i + 2) {
			moves.add(i);
		}
		int[] compare = {1, 1, 3, 3, 5, 5, 7, 7, 9, 9};
		test.mergeRightwards(0, moves);
		assert(Arrays.equals(test.cR, compare));
		
		test.mergeLastRow();
		int[] compare2 = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		assert(Arrays.equals(test.cR, compare2));
		
		
		
	}
	
}


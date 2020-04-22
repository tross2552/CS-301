package generation;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class has the responsibility to create a maze of given dimensions (width, height) 
 * using Eller's algorithm. This class extends MazeBuilder, and overrides the generatePathways()
 * method within MazeBuilder.
 * The MazeBuilder implements Runnable such that it can be run a separate thread.
 * The MazeFactory has a MazeBuilder and handles the thread management.   
 * 
 * MazeBuilder is refactored code from Maze.java by Paul Falstad, www.falstad.com, 
 * Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper, MazeBuilderEller class written by Meg Anderson and Trenten Ross
 * @author  Meg Anderson and Trenten Ross
 * @version 2.0
 * @since   2017-09-23
 */

public class MazeBuilderEller extends MazeBuilder{
	
	
	//cR stands for current Row. This is the row where horizontal connections will be added.
	//nR stands for new Row. This is the row into which vertical connections are being added. 
	//it was unnecessary to have anything more then two rows of arrays. 
	int[] cR;
	int[] nR;
	
	//This increments to create a new "marker" for sets.
	int setCount = 1; 
	
	/**Default constructor, which outputs that Eller's algorithm is being used to 
	 * generate the maze. The super() constructor generates a random instance to
	 * create the maze. */
	public MazeBuilderEller() {
		super();
		System.out.println("MazeBuilderEller uses Eller's algorithm to generate maze.");
	}
	
	/**Deterministic Constructor, which outputs that Eller's algorithm is being used to 
	 * generate the maze. Within the super() constructor, the seed for the random class
	 * is set. We arbitrarily decided the seed would be set to 4, but any number would
	 * have worked. 
	 * @param deterministic a boolean to set whether the maze is random or not*/
	
	public MazeBuilderEller(boolean deterministic) {
		super(deterministic);
		System.out.println("MazeBuilderEller uses Eller's algorithm to generate maze.");
	}
	
	/** This is where Eller's algorithm is actually implemented. This overrides the 
	 * generatePathways() method of MazeBuilder. The randMergeCount was rewritten to
	* make the maze appear less biased: now it must attempt to merge at least a 1/3
	* of length number of times. This does not guarantee successful merges, however, as
	* the same position could be chosen every single time.*/
	
	@Override
	protected void generatePathways() {
		
		cR = new int[width];
		nR = new int[width];
		
		ArrayList<Integer> positions = new ArrayList(); //positions determine where a row will merge two sets together.
		int randMergeCount; //randMergeCount determines how many times a row will merge two sets together
		
		//r is for row so Meg doesn't have to ask again
		for (int r = 0; r < height; r++) {
			
			createNewSets(r);
			
			randMergeCount = random.nextIntWithinInterval(width / 3, width-1);
			for (int c = 0; c < randMergeCount; c ++) {
				positions.add(random.nextIntWithinInterval(0, width-2));
			}
			
			mergeRightwards(r, positions);
			
			if (r < height - 1) {
				
				generateDownPositions(positions);
				
				mergeDownwards(r, positions);
			}

			else {
				mergeLastRow();
			}
			
			//This sets the cR to the next row nR, and leaves the old cR for the garbage collector. 
			cR = nR;
			nR = new int[width];
		}
	}
	
	
	/**This loop accomplishes Steps 1. & 4. Set each cell of the first row to a 
	* new int value (setCount) indicating that is in its own set. If this isn't 
	* the first row, each cell not already in a set will also be given a new number 
	* to indicate it's in a new set. If a cell is in a room it is zero, because 
	* that is the default int initialization.
	* @param int r the row within the maze this acts upon.*/
	protected void createNewSets(int r) {
		
		for (int c = 0; c < width; c ++) {
			if ((cR[c] == 0) && (!(cells.isInRoom(c, r)))) 
				cR[c] = setCount++;
		}
		
	}
	
	/** This loop accomplishes Step 2: Randomly merging sets RIGHTWARDS a random 
	* number of times at random positions or at positions determined by passed 
	* in ArrayList. This allows for easier testing. 
	* @param int r the row within the maze this acts upon.
	* @param ArrayList<Integer> positions an array list of positions to merge at 
	* created for easier testing.  */
	
	protected void mergeRightwards(int r, ArrayList<Integer> positions) {
		
		
		int value;
		int randomPosition;
		Wall wall;
		
		while(!positions.isEmpty()) {
			randomPosition = positions.remove(0); //pull each position to merge from
			value = cR[randomPosition + 1];
			
			//This loop means if two cells are already in the same set it just continues. 
			if (value == cR[randomPosition]) { 
				continue;
			}
			
			for (int i = randomPosition + 1; i < width; i ++ ) {
				if (cR[i] == value) {
					cR[i] = cR[randomPosition];
					wall = new Wall(randomPosition, r, CardinalDirection.East);
					cells.deleteWall(wall);
				}
				else break;
			}
		}
	}
	
	/** This is a helper method for mergeDownwards. For each set in the current row it generates
	 * between 1 and set length number of positions to merge from, ensuring that all sets are 
	 * merged downwards at least once. It then appends these to an array list.
	 * @param ArrayList<Integer> positions an array list of positions to merge downwards at, 
	 * created for easier testing. */
	
	protected void generateDownPositions(ArrayList<Integer> positions) {
		int k=0;//upper bound for generation
		for(int c = 0; c < width; c = k + 1) { //lower bound for generation 
			
			
			for(k = c; k < width; k++) { //set k to upper bound of set
				if(cR[c]!=cR[k])
					break;
			}
			
			k--;
			
			int randMergeCount = random.nextIntWithinInterval(1, 1 + k - c);
			
			for(int i = 0;i<randMergeCount;i++) //add batch of values per set
				positions.add(random.nextIntWithinInterval(c, k)); //append new value
		}
	}
	
	/**This loop accomplishes Step 3. Randomly merge ALL sets DOWNWARDS the number of times
	 * specified within generateDownPositions at randomPositions within each set.  
	 * @param int r the row within the maze this acts upon.
	 * @param ArrayList<Integer> positions an array list of positions to merge at, 
	 * created for easier testing. */
	
	protected void mergeDownwards(int r, ArrayList<Integer> positions) {
		
		int randomPosition;
		Wall wall;
		
		while(!positions.isEmpty()) {
			randomPosition = positions.remove(0); //pull each position to merge from
			nR[randomPosition] = cR[randomPosition];
			wall = new Wall(randomPosition, r, CardinalDirection.South);
			cells.deleteWall(wall);
		}
	}
	

	/**This is the last row case code, in which it merges everything not already in a set 
	 * It does this by calling mergeRightwards on all sets. */
	
	protected void mergeLastRow() {

		ArrayList<Integer> positions = new ArrayList<>();
		
		for (int c = 0; c < width - 1; c ++) {
			if (cR[c] != cR[c + 1]) {
				 positions.add(c);
			}
		}
		
		mergeRightwards(height - 1, positions);
	}
}

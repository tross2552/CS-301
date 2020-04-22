package falstad;

import generation.CardinalDirection;
import generation.Factory;
import generation.MazeConfiguration;
import generation.MazeContainer;
import generation.MazeFactory;
import generation.Order;

/**
 * This is a stub class created to directly manipulate order fields for the sake of testing.
 * It simulates the behavior of MazeController when passed into MazeFactory. 
 * @author  Meg Anderson and Trenten Ross
 * @version 2.0
 * @since   2017-09-23
 */

public class StubMazeController extends MazeController implements Order {
	
	
	public int skill;
	public Builder builder;
	public boolean perfect;
	public Factory factory;
	public boolean deterministic;
	int angle;
	
	// Filename if maze is loaded from file
	private String filename;
	
	public StubMazeController() {
		setBuilder(Order.Builder.DFS); 
		mazeConfig = new MazeContainer();
		factory = new MazeFactory();
		filename = null;
		setCurrentDirection(1,0); //east
	}
	
	public StubMazeController(Builder b, int s, boolean p, boolean d) {
		this.setBuilder(b);
		this.setSkillLevel(s);
		this.setPerfect(p); 
		this.setDeterministic(d);
		mazeConfig = new MazeContainer();
		setCurrentDirection(1,0); //east
	}
	
	public StubMazeController(String filename) {
		this();
		this.filename = filename;
		setCurrentDirection(1,0); //east
	}
	
	/**
	 * Loads maze from file and returns a corresponding maze configuration.
	 * @param filename
	 */
	private MazeConfiguration loadMazeConfigurationFromFile(String filename) {
		// load maze from file
		MazeFileReader mfr = new MazeFileReader(filename) ;
		// obtain MazeConfiguration
		return mfr.getMazeConfiguration();
	}
	
	/**
	 * Method to initialize internal attributes. Called separately from the constructor. 
	 */
	@Override
	public void init() {
		// special case: load maze from file
		if (null != filename) {
			//setState(StateGUI.STATE_GENERATING);
			// push results into controller, imitating maze factory delivery
			deliver(loadMazeConfigurationFromFile(filename));
			// reset filename, next round will be generated again
			filename = null;
			return;
		}
		// common case: generate maze with some algorithm
		assert null != factory : "MazeController.init: factory must be present";
		//state = StateGUI.STATE_TITLE;
	}
	
	/**
	 * based on current field values, generate
	 * a new maze
	 */
	public MazeConfiguration order() {
		factory.order(this);
		factory.waitTillDelivered();
		return mazeConfig;
		
	}

	@Override
	public int getSkillLevel() {
		return this.skill;
	}

	public void setSkillLevel(int skill) {
		this.skill = skill;
	}

	@Override
	public Builder getBuilder() {
		
		return this.builder;
	}
	
	public void setBuilder(Builder builder) {
		this.builder = builder;
	}

	@Override
	public boolean isPerfect() {
		
		return this.perfect;
	}
	
	public void setPerfect(boolean perfect) {
		this.perfect = perfect;
	}
	
	public boolean isDeterministic() {
		
		return this.deterministic;
	}
	
	public void setDeterministic(boolean deterministic) {
		this.deterministic = deterministic;
		factory = new MazeFactory(deterministic);
	}	
	
	public MazeConfiguration getMazeConfig() {
		return mazeConfig;
	}
	
	
	/**
	 * Helper method for walk()
	 * @param dir
	 * @return true if there is no wall in this direction
	 */
	@Override
	protected boolean checkMove(int dir) {
		CardinalDirection cd = null;
		switch (dir) {
		case 1: // forward
			cd = getCurrentDirection();
			break;
		case -1: // backward
			cd = getCurrentDirection().oppositeDirection();
			break;
		default:
			throw new RuntimeException("Unexpected direction value: " + dir);
		}
		//return mazeConfig.getMazecells().hasNoWall(px, py, cd);
		return !mazeConfig.hasWall(px, py, cd);
	}

	/**
	 * Performs a rotation with 4 intermediate views, 
	 * updates the screen and the internal direction
	 * @param dir for current direction
	 */
	synchronized private void rotate(int dir) {
		final int originalAngle = angle;
		final int steps = 4;

		for (int i = 0; i != steps; i++) {
			angle = originalAngle + dir*(90*(i+1))/steps; 
		}
		
		setCurrentDirection((int) Math.cos(radify(angle)), (int) Math.sin(radify(angle))) ;
	}
		
	/**
	 * Moves in the given direction with 4 intermediate steps,
	 * updates the screen and the internal position
	 * @param dir, only possible values are 1 (forward) and -1 (backward)
	 */
	private void walk(int dir) {
		if (!checkMove(dir))
			return;
		setCurrentPosition(px + dir*dx, py + dir*dy) ;
	}
	
	
	
	/**
	 * Method incorporates all reactions to keyboard input in original code, 
	 * The simple key listener calls this method to communicate input.
	 */
	@Override
	public boolean keyDown(UserInput key, int value) {
		operateGameInPlayingState(key);
		return true;
	}
	
	/**
	 * Method responds to user input when the user explores the maze. 
	 * @precondition current state is STATE_PLAY
	 * @param key is the user input 
	 */
	private void operateGameInPlayingState(UserInput key) {
		// react to input for directions and interrupt signal (ESCAPE key)	
		// react to input for displaying a map of the current path or of the overall maze (on/off toggle switch)
		// react to input to display solution (on/off toggle switch)
		// react to input to increase/reduce map scale
		switch (key) {
		case Up: // move forward
			walk(1);
			if (isOutside(px,py)) {
				won=true;
			}
			break;
		case Left: // turn left
			rotate(1);
			break;
		case Right: // turn right
			rotate(-1);
			break;
		case Jump: // make a step forward even through a wall
			// go to position if within maze
			if (mazeConfig.isValidPosition(px + dx, py + dy)) {
				setCurrentPosition(px + dx, py + dy) ;
			}
			break;
		default: break;
		} // end of internal switch statement for playing state
	}
	
	@Override
	public void deliver(MazeConfiguration mazeConfig) {
		this.mazeConfig = mazeConfig ;
		// obtain starting position
		int[] start = mazeConfig.getStartingPosition() ;
		setCurrentPosition(start[0],start[1]) ;
	}
	
	@Override
	public void switchToFinishScreen() {
		
	}
	
	

	

}

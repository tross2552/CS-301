package falstad;

import falstad.Constants.StateGUI;
import generation.CardinalDirection;
import generation.Cells;
import generation.Factory;
import generation.MazeConfiguration;
import generation.MazeContainer;
import generation.MazeFactory;
import generation.Order;

import java.awt.Graphics;
import java.awt.Panel;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

/**
 * Class handles the user interaction. 
 * It implements a state-dependent behavior that controls the display and reacts to key board input from a user. 
 * At this point user keyboard input is first dealt with a key listener (SimpleKeyListener)
 * and then handed over to a MazeController object by way of the keyDown method.
 *
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class MazeController implements Order {
	// Follows a variant of the Model View Controller pattern (MVC).
	// This class acts as the controller that gets user input and operates on the model.
	// A MazeConfiguration acts as the model and this class has a reference to it.
	protected MazeConfiguration mazeConfig ; 
	// Deviating from the MVC pattern, the controller has a list of viewers and 
	// notifies them if user input requires updates on the UI.
	// This is normally the task of the model in the MVC pattern.
	
	// views is the list of registered viewers that get notified
	final private ArrayList<Viewer> views = new ArrayList<Viewer>() ; 
	// all viewers share access to the same graphics object, the panel, to draw on
	private MazePanel panel ;
	private static JPanel titlePanel;
	static MazeApplication app;
		

	//values used when drawing the end screen
	boolean won = true;
	float batteryLevel;
	int steps;
	
	// state keeps track of the current GUI state, one of STATE_TITLE,...,STATE_FINISH, mainly used in redraw()
	private StateGUI state;
	// possible values are defined in Constants
	// user can navigate 
	// title -> generating -(escape) -> title
	// title -> generation -> play -(escape)-> title
	// title -> generation -> play -> finish -> title
	// STATE_PLAY is the main state where the user can navigate through the maze in a first person view

	private int percentdone = 0; 		// describes progress during generation phase
	private boolean showMaze;		 	// toggle switch to show overall maze on screen
	private boolean showSolution;		// toggle switch to show solution in overall maze on screen
	private boolean mapMode; // true: display map of maze, false: do not display map of maze
	// map_mode is toggled by user keyboard input, causes a call to draw_map during play mode

	// current position and direction with regard to MazeConfiguration
	protected int px, py ; // current position on maze grid (x,y)
	protected int dx, dy;  // current direction

	// current position and direction with regard to graphics view
	// graphics has intermediate views for a smoother experience of turns
	private int viewx, viewy; // current position
	private int viewdx, viewdy; // current view direction, more fine grained than (dx,dy)
	private int angle; // current viewing angle, east == 0 degrees
	//static final int viewz = 50;    
	private int walkStep; // counter for intermediate steps within a single step forward or backward
	private Cells seencells; // a matrix with cells to memorize which cells are visible from the current point of view
	// the FirstPersonDrawer obtains this information and the MapDrawer uses it for highlighting currently visible walls on the map

	// about the maze and its generation
	private int skill; // user selected skill level, i.e. size of maze
	private Builder builder; // selected maze generation algorithm
	private boolean perfect; // selected type of maze, i.e. 
	// perfect == true: no loops, i.e. no rooms
	// perfect == false: maze can support rooms
	
	// The factory is used to calculate a new maze configuration
	// The maze is computed in a separate thread which makes 
	// communication with the factory slightly more complicated.
	// Check the factory interface for details.
	protected Factory factory;
	
	// Filename if maze is loaded from file
	private String filename;
	
	//private int zscale = Constants.VIEW_HEIGHT/2;
	private RangeSet rset;
	
	// debug stuff
	private boolean deepdebug = false;
	private boolean allVisible = false;
	private boolean newGame = false;

	
	/**
	 * Constructor
	 * Default setting for maze generating algorithm is DFS.
	 */
	public MazeController() {
		super() ;
		setBuilder(Order.Builder.DFS); 
		panel = new MazePanel() ;
		mazeConfig = new MazeContainer();
		factory = new MazeFactory() ;
		filename = null;
		setCurrentDirection(1,0); //east
	}
	/**
	 * Constructor that also selects a particular generation method
	 */
	public MazeController(Order.Builder builder)
	{
		super() ;
		setBuilder(builder) ;
		panel = new MazePanel() ;
		mazeConfig = new MazeContainer();
		factory = new MazeFactory() ;
		filename = null;
		setCurrentDirection(1,0); //east
	}
	/**
	 * Constructor to read maze from file
	 * @param filename
	 */
	public MazeController(String filename) {
		super();
		setBuilder(Order.Builder.DFS); 
		panel = new MazePanel() ;
		mazeConfig = new MazeContainer() ;
		factory = new MazeFactory(); // no factory needed but to allow user to play another round 
		this.filename = filename;
		setCurrentDirection(1,0); //east
	}
	
	static void setTitlePanel() {
		titlePanel = new JPanel();
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
	public void init() {
		// special case: load maze from file
		if (null != filename) {
			setState(StateGUI.STATE_GENERATING);
			rset = new RangeSet();
			panel.initBufferImage() ;
			addView(new MazeView(this)) ;
			// push results into controller, imitating maze factory delivery
			deliver(loadMazeConfigurationFromFile(filename));
			// reset filename, next round will be generated again
			filename = null;
			return;
		}
		// common case: generate maze with some algorithm
		assert null != factory : "MazeController.init: factory must be present";
		state = StateGUI.STATE_TITLE;
		rset = new RangeSet();
		panel.initBufferImage() ;
		/*addView(new MazeView(this)) ;
		notifyViewerRedraw() ;*/
	}
	
	public MazeConfiguration getMazeConfiguration() {
		return mazeConfig ;
	}
	protected StateGUI getState(){
		return state;
	}
	
	///////////// methods for state transitions in UI automaton /////////////////////////////////////////
	// user can navigate 
	// title -> generating -(escape) -> title
	// title -> generation -> play -(escape)-> title
	// title -> generation -> play -> finish -> title
	// STATE_PLAY is the main state where the user can navigate through the maze in a first person view

	/**
	 * Switches to generating screen. 
	 * Uses the factory to start the generation of a maze with a background thread.
	 * This transition is only possible from the title screen. 
	 * @param key is user input, gives skill level to determine the width, height and number of rooms for the new maze
	 */
	private void switchToGeneratingScreen(int key) {
		assert state == StateGUI.STATE_TITLE : "MazeController.switchToGeneratingScreen: unexpected current state " + state ;
		// switch state and update screen
		setState(StateGUI.STATE_GENERATING);
		percentdone = 0;
		notifyViewerRedraw() ;
		// translate key into skill level if possible
		int skill = 0 ; // legal default value
		if (key >= '0' && key <= '9') {
			skill = key - '0';
		}
		if (key >= 'a' && key <= 'f') {
			skill = key - 'a' + 10;
		}
		// set fields to specify order
		setSkillLevel(skill) ;
		// generation method already set in constructor method
		setPerfect(false); // allow for rooms
		// make maze factory produce a maze 
		// operates with background thread
		// method returns immediately, 
		// maze will be delivered later by calling this.deliver method
		factory.order(this) ;
	}
	// precondition: setSkillLevel has been called before
	private void switchToGeneratingScreen() {
		assert state == StateGUI.STATE_TITLE : "MazeController.switchToGeneratingScreen: unexpected current state " + state ;
		app.remove(titlePanel);
		app.add(panel);
		app.validate();
		//titlePanel.setVisible(false);
		// switch state and update screen
		setState(StateGUI.STATE_GENERATING);
		percentdone = 0;
		notifyViewerRedraw() ;
		// generation method already set in constructor method
		setPerfect(false); // allow for rooms
		// make maze factory produce a maze 
		// operates with background thread
		// method returns immediately, 
		// maze will be delivered later by calling this.deliver method
		factory.order(this) ;
	}
	
	/**
	 * Switches to playing state, registers appropriate views, updates screen.
	 * This transition is only possible from the generating screen. 
	 */
	private void switchToPlayingScreen() {
		assert state == StateGUI.STATE_GENERATING : "MazeController.switchToPlayingScreen: unexpected current state " + state ;
		// set the current state for the state-dependent behavior
		setState(StateGUI.STATE_PLAY);
		cleanViews() ;
		// register views for the new maze
		// reset map_scale in mapdrawer to a value of 10
		addView(new FirstPersonDrawer(Constants.VIEW_WIDTH,Constants.VIEW_HEIGHT, Constants.MAP_UNIT,
				Constants.STEP_SIZE, seencells, mazeConfig.getRootnode())) ;
		
		// order of registration matters, code executed in order of appearance!
		addView(new MapDrawer(Constants.VIEW_WIDTH,Constants.VIEW_HEIGHT,Constants.MAP_UNIT,
				Constants.STEP_SIZE, seencells, 10, this)) ;

		notifyViewerRedraw() ;
	}
	/**
	 * Switches to title screen, possibly canceling maze generation.
	 * This transition is possible from several screens.
	 */
	private void switchToTitleScreen(boolean cancelOrder) {
		//System.out.println("switchToTitleScreen: param == " + cancelOrder) ;
		if (cancelOrder) {
			factory.cancel();
		}
		setState(StateGUI.STATE_TITLE);
		notifyViewerRedraw() ;
		
		app.remove(panel);
		app.add(titlePanel);
		app.validate();
	}
	/**
	 * Switches to title screen, possibly canceling maze generation.
	 * This transition is only possible from the playing screen
	 * by making a forward or backward move through the exit outside
	 * of the maze.
	 */
	protected void switchToFinishScreen() {
		assert state == StateGUI.STATE_PLAY : "MazeController.switchToFinishScreen: unexpected current state " + state ;
		setState(StateGUI.STATE_FINISH);
		
		notifyViewerRedraw() ;
		
	}
	/////////////////////////////// Methods for the Model-View-Controller Pattern /////////////////////////////
	/**
	 * Register a view
	 */
	public void addView(Viewer view) {
		views.add(view) ;
	}
	/**
	 * Unregister a view
	 */
	public void removeView(Viewer view) {
		views.remove(view) ;
	}
	/**
	 * Remove obsolete FirstPersonDrawer and MapDrawer
	 */
	private void cleanViews() {
		// go through views and remove viewers as needed
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			if ((v instanceof FirstPersonDrawer)||(v instanceof MapDrawer))
			{
				it.remove() ;
			}
		}

	}
	/**
	 * Notify all registered viewers to redraw their graphics
	 */
	protected void notifyViewerRedraw() {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			Graphics g = panel.getBufferGraphics() ;
			// viewers draw on the buffer graphics
			if (null == g) {
				System.out.println("Maze.notifierViewerRedraw: can't get graphics object to draw on, skipping redraw operation") ;
			}
			else {
				if (state==StateGUI.STATE_FINISH && v instanceof MazeView) {
					MazeView v2;
					v2 = (MazeView) v;
					v2.won = won;
					v2.batteryLevel = batteryLevel;
					v2.steps = steps;
					v2.redraw(g, state, px, py, viewdx, viewdy, walkStep, Constants.VIEW_OFFSET, rset, angle) ;
				}
				else v.redraw(g, state, px, py, viewdx, viewdy, walkStep, Constants.VIEW_OFFSET, rset, angle) ;
			}	
		}
		// update the screen with the buffer graphics
		panel.update() ;
	}
	/** 
	 * Notify all registered viewers to increment the map scale
	 */
	private void notifyViewerIncrementMapScale() {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			v.incrementMapScale() ;
		}
		// update the screen with the buffer graphics
		panel.update() ;
	}
	/** 
	 * Notify all registered viewers to decrement the map scale
	 */
	private void notifyViewerDecrementMapScale() {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			v.decrementMapScale() ;
		}
		// update the screen with the buffer graphics
		panel.update() ;
	}
	////////////////////////////// get methods ///////////////////////////////////////////////////////////////
	boolean isInMapMode() { 
		return mapMode ; 
	} 
	boolean isInShowMazeMode() { 
		return showMaze ; 
	} 
	boolean isInShowSolutionMode() { 
		return showSolution ; 
	} 
	public String getPercentDone(){
		return String.valueOf(percentdone) ;
	}
	public Panel getPanel() {
		return panel ;
	}
	public static JPanel getTitlePanel() {
		return titlePanel ;
	}
	////////////////////////////// set methods ///////////////////////////////////////////////////////////////
	////////////////////////////// Actions that can be performed on the maze model ///////////////////////////
	protected void setCurrentPosition(int x, int y)
	{
		px = x ;
		py = y ;
	}
	protected void setCurrentDirection(int x, int y)
	{
		dx = x ;
		dy = y ;
	}
	protected int[] getCurrentPosition() {
		int[] result = new int[2];
		result[0] = px;
		result[1] = py;
		return result;
	}
	protected CardinalDirection getCurrentDirection() {
		return CardinalDirection.getDirection(dx, dy);
	}

	/////////////////////// Methods for debugging ////////////////////////////////
	private void dbg(String str) {
		//System.out.println(str);
	}

	private void logPosition() {
		if (!deepdebug)
			return;
		dbg("x="+viewx/Constants.MAP_UNIT+" ("+
				viewx+") y="+viewy/Constants.MAP_UNIT+" ("+viewy+") ang="+
				angle+" dx="+dx+" dy="+dy+" "+viewdx+" "+viewdy);
	}
	
	//////////////////////// Methods for move and rotate operations ///////////////
	final double radify(int x) {
		return x*Math.PI/180;
	}
	/**
	 * Helper method for walk()
	 * @param dir
	 * @return true if there is no wall in this direction
	 */
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
			throw new RuntimeException("Unexpexted direction value: " + dir);
		}
		//return mazeConfig.getMazecells().hasNoWall(px, py, cd);
		return !mazeConfig.hasWall(px, py, cd);
	}
	/**
	 * Redraw and wait, used to obtain a smooth appearance for rotate and move operations
	 */
	private void slowedDownRedraw() {
		notifyViewerRedraw() ;
		try {
			Thread.currentThread().sleep(25);
		} catch (Exception e) { }
	}
	/**
	 * Intermediate step during rotation, updates the screen
	 */
	private void rotateStep() {
		angle = (angle+1800) % 360;
		viewdx = (int) (Math.cos(radify(angle))*(1<<16));
		viewdy = (int) (Math.sin(radify(angle))*(1<<16));
		slowedDownRedraw();
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
			// add 1/4 of 90 degrees per step 
			// if dir is -1 then subtract instead of addition
			angle = originalAngle + dir*(90*(i+1))/steps; 
			rotateStep();
		}
		setCurrentDirection((int) Math.cos(radify(angle)), (int) Math.sin(radify(angle))) ;
		logPosition();
	}
	/**
	 * Moves in the given direction with 4 intermediate steps,
	 * updates the screen and the internal position
	 * @param dir, only possible values are 1 (forward) and -1 (backward)
	 */
	synchronized private void walk(int dir) {
		if (!checkMove(dir))
			return;
		// walkStep is a parameter of the redraw method in FirstPersonDrawer
		// it is used there for scaling steps
		// so walkStep is implicitly used in slowedDownRedraw which triggers the redraw
		// operation on all listed viewers
		for (int step = 0; step != 4; step++) {
			walkStep += dir;
			slowedDownRedraw();
		}
		setCurrentPosition(px + dir*dx, py + dir*dy) ;
		walkStep = 0;
		logPosition();
	}

	/**
	 * checks if the given position is outside the maze
	 * @param x
	 * @param y
	 * @return true if position is outside, false otherwise
	 */
	protected boolean isOutside(int x, int y) {
		return !mazeConfig.isValidPosition(x, y) ;
	}

	public enum UserInput {ReturnToTitle, Start, Up, Down, Left, Right, Jump, ToggleLocalMap, ToggleFullMap, ToggleSolution, ZoomIn, ZoomOut, KillBattery };
	/**
	 * Method incorporates all reactions to keyboard input in original code, 
	 * The simple key listener calls this method to communicate input.
	 */
	public boolean keyDown(UserInput key, int value) {
		String s1 = "before: px,py:" + px + "," + py + " dir:" + dx + "," + dy;
		// possible inputs for key set by enumerated type UserInput
		// depending on the current state of the GUI, inputs have different effects
		// implemented as a little automaton that switches state and performs necessary actions
		switch (state) {
		case STATE_TITLE:
			// if screen shows title page, keys describe level of expertise
			// create a maze according to the user's selected level
			// user types wrong key, just use 0 as a possible default value
			if (key == UserInput.Start) {
				setSkillLevel(value);
				switchToGeneratingScreen();
			}
			else {
				System.out.println("MazeController:unexpected command:" + key);
			}
			break;
			// if we are currently generating a maze, recognize interrupt signal (ESCAPE key)
			// to stop generation of current maze
		case STATE_GENERATING:
			if (key == UserInput.ReturnToTitle) {
				switchToTitleScreen(true);
			}
			break;
		case STATE_PLAY:
			// if user explores maze, 
			// react to input for directions and interrupt signal (ESCAPE key)	
			// react to input for displaying a map of the current path or of the overall maze (on/off toggle switch)
			// react to input to display solution (on/off toggle switch)
			// react to input to increase/reduce map scale
			operateGameInPlayingState(key);
			// debug
			/*
			if (this.mazeConfig.isValidPosition(px, py)) {
				String s2 = "after: px,py:" + px + "," + py + " dir:" + dx + "," + dy;
				System.out.println("mc.keydown:" + s1 + "  " + s2);
				String s3 = "Walls at: px,py:" + px + "," + py;
				s3 += " ESWN: " + mazeConfig.hasWall(px, py, CardinalDirection.East) +
						"," + mazeConfig.hasWall(px, py, CardinalDirection.South) +
						"," + mazeConfig.hasWall(px, py, CardinalDirection.West) +
						"," + mazeConfig.hasWall(px, py, CardinalDirection.North);
				System.out.println("mc.keydown:" + s3);
			}
			*/
			//
			break ;
		case STATE_FINISH:
			// if we are finished, return to initial state with title screen	
			switchToTitleScreen(false);
			break;
		} 
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
		case Start: // misplaced, do nothing
			break;
		case Up: // move forward
			walk(1);
			if (isOutside(px,py)) {
				//switchToFinishScreen();
				won=true;
			}
			break;
		case Left: // turn left
			rotate(1);
			break;
		case Right: // turn right
			rotate(-1);
			break;
		case Down: // move backward
			walk(-1);
			if (isOutside(px,py)) {
				switchToFinishScreen();
				won=true;
			}
			break;
		case ReturnToTitle: // escape to title screen
			switchToTitleScreen(false);
			break;
		case Jump: // make a step forward even through a wall
			// go to position if within maze
			if (mazeConfig.isValidPosition(px + dx, py + dy)) {
				setCurrentPosition(px + dx, py + dy) ;
				notifyViewerRedraw() ;
			}
			break;
		case ToggleLocalMap: // show local information: current position and visible walls
			// precondition for showMaze and showSolution to be effective
			// acts as a toggle switch
			mapMode = !mapMode; 		
			notifyViewerRedraw() ; 
			break;
		case ToggleFullMap: // show the whole maze
			// acts as a toggle switch
			showMaze = !showMaze; 		
			notifyViewerRedraw() ; 
			break;
		case ToggleSolution: // show the solution as a yellow line towards the exit
			// acts as a toggle switch
			showSolution = !showSolution; 		
			notifyViewerRedraw() ;
			break;
		case ZoomIn: // zoom into map
			notifyViewerIncrementMapScale() ;
			notifyViewerRedraw() ; // seems useless but it is necessary to make the screen update
			break ;
		case ZoomOut: // zoom out of map
			notifyViewerDecrementMapScale() ;
			notifyViewerRedraw() ; // seems useless but it is necessary to make the screen update
			break ;
		} // end of internal switch statement for playing state
	}

	////////// set methods for fields ////////////////////////////////
	private void setSkillLevel(int skill) {
		this.skill = skill ;
	}

	void setBuilder(Builder builder) {
		this.builder = builder ;
	}

	private void setPerfect(boolean perfect) {
		this.perfect = perfect ;
	}
	/**
	 * Sets the internal state of the game state.
	 * Method checks if state transition is as expected.
	 * @param newState the state to set
	 */
	protected void setState(StateGUI newState) {
		// check if transition is as expected
		// null -> STATE_TITLE: game starts, initialization
		// TITLE->GENERATING: game switches to generating screen
		// GENERATING -> TITLE: escape button, reset game
		// GENERATING -> PLAYING: maze generation finished, start game
		// PLAYING -> TITLE: escape button, reset game
		// PLAYING -> FINISH: game over
		// FINISH -> TITLE: reset game
		
		// special case: game starts
		if (null == state) {
			// newState should be initial state in actual game
			// may be different for testing purposes
			if (newState != StateGUI.STATE_TITLE) {
				System.out.println("Warning: MazeController.StateGUI: automaton starts in state: " + newState);
			}
			state = newState; // update operation
			return;
		}
		// state != null, safe to use switch statement
		String msg = "MazeController.setState: illegal state transition: " + state + " to " + newState;
		switch (state) {
		case STATE_TITLE:
			assert (newState == StateGUI.STATE_GENERATING) : msg ;	
			break;
		case STATE_GENERATING:
			assert (newState == StateGUI.STATE_TITLE || newState == StateGUI.STATE_PLAY) : msg ;
			break;
		case STATE_PLAY:
			assert (newState == StateGUI.STATE_TITLE || newState == StateGUI.STATE_FINISH) : msg ;
			break;
		case STATE_FINISH:
			assert (newState == StateGUI.STATE_TITLE) : msg ;	
			break;
		default: 
			throw new RuntimeException("Inconsistent enum type") ;
		}
		// update operation
		state = newState;
	}
	///////////////// methods to implement Order interface //////////////
	@Override
	public int getSkillLevel() {
		return skill;
	}
	@Override
	public Builder getBuilder() {
		return builder ;
	}
	@Override
	public boolean isPerfect() {
		return perfect;
	}
	@Override
	public void deliver(MazeConfiguration mazeConfig) {
		this.mazeConfig = mazeConfig ;
		
		// WARNING: DO NOT REMOVE, USED FOR GRADING PROJECT ASSIGNMENT
		if (Cells.deepdebugWall)
		{   // for debugging: dump the sequence of all deleted walls to a log file
			// This reveals how the maze was generated
			mazeConfig.getMazecells().saveLogFile(Cells.deepedebugWallFileName);
		}
		////////
		
		// adjust internal state of maze model
		// visibility settings
		showMaze = false ;
		showSolution = false ;
		mapMode = false;
		// init data structure for visible walls
		seencells = new Cells(mazeConfig.getWidth()+1,mazeConfig.getHeight()+1) ;
		// obtain starting position
		int[] start = mazeConfig.getStartingPosition() ;
		setCurrentPosition(start[0],start[1]) ;
		// set current view direction and angle
		setCurrentDirection(1, 0) ; // east direction
		viewdx = dx<<16; 
		viewdy = dy<<16;
		angle = 0; // angle matches with east direction, hidden consistency constraint!
		walkStep = 0; // counts incremental steps during move/rotate operation
		
		// update screens for playing state
		switchToPlayingScreen();
	}
	/**
	 * Allows external increase to percentage in generating mode.
	 * Internal value is only update if it exceeds the last value and is less or equal 100
	 * @param percentage gives the new percentage on a range [0,100]
	 * @return true if percentage was updated, false otherwise
	 */
	@Override
	public void updateProgress(int percentage) {
		if (percentdone < percentage && percentage <= 100) {
			percentdone = percentage;
			if (state == StateGUI.STATE_GENERATING)
			{
				notifyViewerRedraw() ;
			}
			else
				dbg("Warning: Receiving update request for increasePercentage while not in generating state, skip redraw.") ;
		}
	}
}




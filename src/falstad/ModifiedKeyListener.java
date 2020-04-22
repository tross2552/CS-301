package falstad;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import falstad.MazeController.UserInput;

/**
 * Class implements a translation for the user input handled by the MazeController class. 
 * The MazeApplication attaches the listener to the GUI, such that user keyboard input
 * flows from GUI to the listener.keyPressed to the MazeController.keyDown method.
 *
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class ModifiedKeyListener implements KeyListener {

	private Container parent ;
	private ManualDriver driver ;
	
	ModifiedKeyListener(Container parent, MazeController controller){
		this.parent = parent;
		this.driver = new ManualDriver(controller);
	}
	/**
	 * Translate keyboard input to the corresponding operation for 
	 * the MazeController.keyDown method.
	 */
	@Override
	public void keyPressed(KeyEvent arg0) {
		int key = arg0.getKeyChar();
		int code = arg0.getKeyCode();
		
		//Possible operations for UserInput based on enum
		// {ReturnToTitle, Start, 
		// Up, Down, Left, Right, Jump, 
		// ToggleLocalMap, ToggleFullMap, ToggleSolution, 
		// ZoomIn, ZoomOut };
		MazeController.UserInput uikey = null;
		int value = 0;
			
		// translate keyboard input into operation for MazeController
		switch (key) {
		case ('w' & 0x1f): // Ctrl-w makes a step forward even through a wall
			uikey = UserInput.Jump;
			break;
		case '\t': case 'm': // show local information: current position and visible walls
			// precondition for showMaze and showSolution to be effective
			// acts as a toggle switch
			uikey = UserInput.ToggleLocalMap;
			break;
		case 'z': // show a map of the whole maze
			// acts as a toggle switch
			uikey = UserInput.ToggleFullMap;
			break;
		case 's': // show the solution on the map as a yellow line towards the exit
			// acts as a toggle switch
			uikey = UserInput.ToggleSolution;
			break;
		case '+': case '=': // zoom into map
			uikey = UserInput.ZoomIn;
			break ;
		case '-': // zoom out of map
			uikey = UserInput.ZoomOut;
			break ;
		case KeyEvent.VK_ESCAPE: // is 27
			uikey = UserInput.ReturnToTitle;
			break;
		case 'h': // turn left
			uikey = UserInput.Left;
			break;
		case 'j': // move backward
			uikey = UserInput.Down;
			break;
		case 'k': // move forward
			uikey = UserInput.Up;
			break;
		case 'l': // turn right
			uikey = UserInput.Right;
			break;
		case 't': // turn right
			uikey = UserInput.KillBattery;
			break;
		case KeyEvent.CHAR_UNDEFINED: // fall back if key is undefined but code is
			// char input for 0-9, a-f skill-level
			if ((KeyEvent.VK_0 <= code && code <= KeyEvent.VK_9) || (KeyEvent.VK_A <= code && code <= KeyEvent.VK_Z)){
				if (code >= '0' && code <= '9') {
					value = code - '0';
					uikey = UserInput.Start;
				}
				if (code >= 'a' && code <= 'f') {
					value = code - 'a' + 10;
					uikey = UserInput.Start;
				}
			} else {
				if (KeyEvent.VK_ESCAPE == code)
					uikey = UserInput.ReturnToTitle;
				if (KeyEvent.VK_UP == code)
					uikey = UserInput.Up;
				if (KeyEvent.VK_DOWN == code)
					uikey = UserInput.Down;
				if (KeyEvent.VK_LEFT == code)
					uikey = UserInput.Left;
				if (KeyEvent.VK_RIGHT == code)
					uikey = UserInput.Right;
			}
			break;
		default:
			// check ranges of values as possible selections for skill level
			if (key >= '0' && key <= '9') {
				value = key - '0';
				uikey = UserInput.Start;
			} else
			if (key >= 'a' && key <= 'f') {
				value = key - 'a' + 10;
				uikey = UserInput.Start;
			} else
				System.out.println("SimpleKeyListener:Error: cannot match input key:" + key);
			break;
		}
		// don't let bad input proceed
		if (null == uikey) {
			System.out.println("SimpleKeyListener: ignoring unmatched keyboard input: key=" + key + " code=" + code);
			return;
		}
		
		assert (0 <= value && value <= 15);		
		// feed user input into controller
		// value is only used in combination with uikey == Start
		
		
		try {
			driver.manualDrive(uikey,0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parent.repaint() ;
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// nothing to do
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// NOTE FOR THIS TYPE OF EVENT IS getKeyCode always 0, so Escape etc is not recognized	
		// this is why we work with keyPressed
	}
	
	public void startManual(int level) {
		driver.r.resetRobot();
		((ManualDriver) driver).manualDrive(UserInput.Start, level);
	}
	
	public void startAuto(String name, MazeController controller) {
		if (name.equals("Wizard")) {
			driver = new Wizard(controller);
			try {
				driver.r.resetRobot();
				driver.drive2Exit();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (name.equals("Wall Follower")) {
			driver = new WallFollower(controller);
			try {
				driver.r.resetRobot();
				driver.drive2Exit();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		else if (name.equals("Pledge")) {
			driver = new Pledge(controller);
			try {
				driver.r.resetRobot();
				driver.drive2Exit();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }

}


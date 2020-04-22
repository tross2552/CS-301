package falstad;

import java.util.Arrays;

import falstad.Constants.StateGUI;
import falstad.MazeController.UserInput;
import falstad.Robot.Direction;
import falstad.Robot.Turn;
import generation.CardinalDirection;

public class Pledge extends ManualDriver implements RobotDriver {
	
	public Pledge() {
		super();
	}
	
	public Pledge(MazeController controller) {
		super(controller);
	}
	
	/**This method is a replacement for it's super class Manual Driver's method. If the user selects any move key
	 * it will drive the robot to the exit. This means to start the robot's automatic driver method the user
	 * must press any movement key to start it on it's way. If they select to toggle the map or solution they
	 * can do it before they start the robot*/
	
	@Override
	public void manualDrive(UserInput uikey, int value) {
		
		r.getMazeController().batteryLevel = r.initialBatteryLevel - r.getBatteryLevel();
		r.getMazeController().steps = r.getOdometerReading();
		
		if (r.getMazeController().getState() == StateGUI.STATE_PLAY){
			switch(uikey) {
				case Up:
				case Left:
				case Right:
				case Down:
				try {
					drive2Exit();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					break;
				case KillBattery:
					r.setBatteryLevel(1);
					break;
				default:
					r.getMazeController().keyDown(uikey, value);
			}
			
			r.getMazeController().batteryLevel = r.initialBatteryLevel - r.getBatteryLevel();
			r.getMazeController().steps = r.getOdometerReading();
			
			//TODO: refactor this to place it inside of BasicRobot
			if (r.isCurrentlyOutside() || r.hasStopped()) {
				r.getMazeController().won = r.isCurrentlyOutside();
				r.resetRobot();
				r.getMazeController().switchToFinishScreen();
			}
		}
		
		else r.getMazeController().keyDown(uikey, value);
	}
	
	/** This method navigates the robot to the exit. It moves along a left wall, until it finds a solution. 
	 *  If you tried to start this on an isolated wall section, which I'm sure you will, to should effectively
	 *  navigate around an isolated section and move on.  */
	
	public boolean drive2Exit() throws Exception {
		
		CardinalDirection defaultDir = r.getCurrentDirection();
		int count = 0;
		
		while (!r.hasStopped() && !r.isCurrentlyOutside()) {
				
			if (r.distanceToObstacle(Direction.FORWARD) != 0){
				assert(r.getCurrentDirection() == defaultDir);
				r.move(1, false);
			}
			
			else do {
					
					if (count == 0) {
						r.rotate(Turn.RIGHT);
						count -= 1;
					}
						
					while ((r.distanceToObstacle(Direction.FORWARD) == 0) && (r.distanceToObstacle(Direction.LEFT) == 0) && !r.hasStopped()) {
						r.rotate(Turn.RIGHT);
						count -= 1;
					}
						
					if (r.distanceToObstacle(Direction.LEFT) > 0) {
						//Add code into BasicRobot so that it updates if the bot's hit a wall
						r.rotate(Turn.LEFT);
						count += 1;
						r.move(1, false);
					}
					
					else {
						r.move(1, false);
					}
					
			} while (count != 0 && !r.hasStopped() && !r.isCurrentlyOutside());
			
		}
		
		
		return r.isCurrentlyOutside();
		
	}
}

package falstad;

import java.util.Arrays;

import falstad.Constants.StateGUI;
import falstad.MazeController.UserInput;
import falstad.Robot.Direction;
import falstad.Robot.Turn;
import generation.CardinalDirection;

public class Wizard extends ManualDriver implements RobotDriver {
	
	public Wizard() {
		super();
	}
	
	public Wizard(MazeController controller) {
		super(controller);
	}
	
	/**This method navigates the robot to the exit. It moves towards whatever position is closest to the exit.
	 * It gets this information from the maze configurations get distance to exit method. */
	
	@Override
	public boolean drive2Exit() throws Exception {
		r.resetRobot();
		
		while (!r.isCurrentlyOutside() && !r.hasStopped()) {
			
			CardinalDirection turnTo = CardinalDirection.East;
			for(Robot.Direction dirR: Robot.Direction.values()) {
				int[] currPos = r.getCurrentPosition();
				if(r.distanceToObstacle(dirR)!=0) {
					
					CardinalDirection dir = r.translateDirection(dirR);
					
					if(r.distanceToObstacle(dirR)==r.INFINITY) {
						turnTo=dir;
						break;
					}
					
					int[] change = dir.getDirection();//turns out this method is flipped vertically
					int[] checkPos = null;
					try {
						checkPos=r.getCurrentPosition();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					checkPos[0]+=change[0];
					checkPos[1]-=change[1]; //account for being flipped
					
					int distCurr = r.getMazeController().mazeConfig.getDistanceToExit(currPos[0], currPos[1]);
					int distCheck = r.getMazeController().mazeConfig.getDistanceToExit(checkPos[0], checkPos[1]);
					
					if(distCheck<distCurr) {
						turnTo=dir;
						break;
					}
				}
			}
			Turn face = r.translateCardinal(turnTo);
			if (face != null) r.rotate(face);
			r.move(1, false);
		}
		return r.isCurrentlyOutside();
		
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

			if (r.isCurrentlyOutside() || r.hasStopped()) {
				r.getMazeController().won = r.isCurrentlyOutside();
				r.resetRobot();
				r.getMazeController().switchToFinishScreen();
			}
		}
		
		else r.getMazeController().keyDown(uikey, value);
	}

}

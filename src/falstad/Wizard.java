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
	
	@Override
	public boolean drive2Exit() throws Exception {
		System.out.println("started");
		System.out.println(r.mazeControl.getBuilder());
		while (!r.mazeControl.isOutside(r.mazeControl.getCurrentPosition()[0], r.mazeControl.getCurrentPosition()[1]) 
				&& !r.hasStopped()) {
			System.out.println("Loop Iteration -");
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
					
					int distCurr = r.mazeControl.mazeConfig.getDistanceToExit(currPos[0], currPos[1]);
					int distCheck = r.mazeControl.mazeConfig.getDistanceToExit(checkPos[0], checkPos[1]);
					
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
		return r.mazeControl.isOutside(r.mazeControl.getCurrentPosition()[0], r.mazeControl.getCurrentPosition()[1]);
		
	}
	
	@Override
	public void manualDrive(UserInput uikey, int value) {
		
		r.mazeControl.batteryLevel = r.initialBatteryLevel - r.getBatteryLevel();
		r.mazeControl.steps = r.getOdometerReading();
		
		if (r.mazeControl.getState() == StateGUI.STATE_PLAY){
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
					r.mazeControl.keyDown(uikey, value);
			}
			r.mazeControl.batteryLevel = r.initialBatteryLevel - r.getBatteryLevel();
			r.mazeControl.steps = r.getOdometerReading();
			//TODO: refactor this to place it inside of BasicRobot
			if (r.mazeControl.isOutside(r.mazeControl.getCurrentPosition()[0], r.mazeControl.getCurrentPosition()[1]) || r.hasStopped()) {
				r.mazeControl.won = r.mazeControl.isOutside(r.mazeControl.getCurrentPosition()[0], r.mazeControl.getCurrentPosition()[1]);
				r.resetRobot();
				r.mazeControl.switchToFinishScreen();
			}
		}
		
		else r.mazeControl.keyDown(uikey, value);
	}
	
	/**This method tells the robot which direction to turn in in order to face a given position
	 * TODO: consider whether this would be better placed inside of basicRobot*/
	
	private Turn facePosition(int[] cn) {
		
		//Go North
		if (cn[1] > r.mazeControl.getCurrentPosition()[0]) {
			return r.translateCardinal(CardinalDirection.North);
		}
		
		//Go South
		else if (cn[1] < r.mazeControl.getCurrentPosition()[0]) {
			return r.translateCardinal(CardinalDirection.South);
		}
		
		//Go West
		else if (cn[0] > r.mazeControl.getCurrentPosition()[1]) {
			return r.translateCardinal(CardinalDirection.East);
		}
		
		//Go East
		else if (cn[0] < r.mazeControl.getCurrentPosition()[1]) {
			return r.translateCardinal(CardinalDirection.West);
		}
		
		return null;
	}

}

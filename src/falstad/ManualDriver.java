/**
 * Class-responsibility-collaboration card
 * Name: ManualDriver
 * Collaborators: 
 * 		is a Driver
 * 		uses a KeyListener
 * 		has a 	Robot r;
 * 				int width;
 * 				int height;
 * 				int distance;
 * Responsibilities:
 * 1. Sets robot
 * 2. Sets dimensions
 * 3. Sets distance to exit
 * 4. Provides info on efficiency of driver algorithm/player
 * 5. Interprets keylistener input
 * */

/**TODO: [] setDistance error catching
 * 	    	 [] drive2Exit */

package falstad;

import generation.Distance;
import falstad.Constants.StateGUI;
import falstad.MazeController.UserInput;
import falstad.Robot.Turn;

public class ManualDriver implements RobotDriver {

	BasicRobot r;
	int width;
	int height;
	Distance distances;
	int currDistance;
	
	public ManualDriver() {
		r = new BasicRobot();
	}
	
	public ManualDriver(MazeController controller) {
		r = new BasicRobot(controller);
	}
	
	@Override
	public void setRobot(Robot r) {
		this.r = (BasicRobot)r;

	}
	
	/**This is the "helper" method we create for the manual driver, it accepts
	 * information from our modified key listener class, and interprets those keys
	 * to tell Basic Robot what to do. The value is always discarded, because it 
	 * is a remnant of when this feed directly into mazeController
	 * @param UserInput the key the user selects
	 * @param int an integer value that is discarded */
	
	public void manualDrive(UserInput uikey, int value) {
		
		r.mazeControl.batteryLevel = r.initialBatteryLevel - r.getBatteryLevel();
		r.mazeControl.steps = r.getOdometerReading();
		
		if (r.mazeControl.getState() == StateGUI.STATE_PLAY){
			switch(uikey) {
				case Up:
					r.move(1, true);
					break;
				case Left:
					r.rotate(Turn.LEFT);
					break;
				case Right:
					r.rotate(Turn.RIGHT);
					break;
				case Down:
					r.rotate(Turn.AROUND);
					break;
				case KillBattery:
					r.setBatteryLevel(1);
					break;
				default:
					r.mazeControl.keyDown(uikey, value);
			}
			
			//TODO: refactor this to place it inside of BasicRobot
			if (r.mazeControl.isOutside(r.mazeControl.getCurrentPosition()[0], r.mazeControl.getCurrentPosition()[1]) || r.hasStopped()) {
				r.mazeControl.won = r.mazeControl.isOutside(r.mazeControl.getCurrentPosition()[0], r.mazeControl.getCurrentPosition()[1]);
				r.resetRobot();
				r.mazeControl.switchToFinishScreen();
			}
		}
		
		else r.mazeControl.keyDown(uikey, value);
		
	}


	@Override
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void setDistance(Distance d) {

	}

	/**In this implementation, manual, this does nothing. It just returns false 
	 * because it is not capable of driving you to the exit*/
	@Override
	public boolean drive2Exit() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getEnergyConsumption() {
		return r.initialBatteryLevel - r.getBatteryLevel();
	}

	@Override
	public int getPathLength() {
		return r.getOdometerReading();
	}


}

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
import falstad.MazeController.UserInput;

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
	//TODO: consider whether the down key should turn it around
	public void manualDrive(UserInput uikey, int value) {
		r.mazeControl.batteryLevel=r.initialBatteryLevel-r.getBatteryLevel();
		r.mazeControl.steps=r.getOdometerReading();
		if (!r.hasStopped()) {
			switch(uikey) {
				case Up:
					r.move(1, true);
					break;
				case Left:
					r.rotate(falstad.Robot.Turn.LEFT);
					break;
				case Right:
					r.rotate(falstad.Robot.Turn.RIGHT);
					break;
				case Down:
					r.rotate(falstad.Robot.Turn.AROUND);
					break;
				case KillBattery:
					r.setBatteryLevel(1);
					break;
				default:
					r.mazeControl.keyDown(uikey, value);
			}
		}
		else {
			r.mazeControl.won=false;
			r.mazeControl.switchToFinishScreen();
		}
		
	}


	@Override
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void setDistance(Distance d) {

	}

	@Override
	public boolean drive2Exit() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getEnergyConsumption() {
		BasicRobot br = (BasicRobot)r;
		return br.initialBatteryLevel - br.getBatteryLevel();
	}

	@Override
	public int getPathLength() {
		return r.getOdometerReading();
	}


}

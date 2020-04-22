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
	
	public boolean drive2Exit() throws Exception {
		
		CardinalDirection defaultDir = r.getCurrentDirection();
		int count = 0;
		
		while (!r.hasStopped() && 
				(!r.mazeControl.isOutside(r.mazeControl.getCurrentPosition()[0], r.mazeControl.getCurrentPosition()[1]))) {
				
			if (r.distanceToObstacle(Direction.FORWARD) != 0){
				assert(r.getCurrentDirection() == defaultDir);
				System.out.println(Arrays.toString(r.getCurrentPosition()));
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
						System.out.println(Arrays.toString(r.getCurrentPosition()));
						r.move(1, false);
					}
					
					else {
						System.out.println(Arrays.toString(r.getCurrentPosition()));
						r.move(1, false);
					}
					
			} while (count != 0 && !r.hasStopped() && !r.mazeControl.isOutside(r.mazeControl.getCurrentPosition()[0], r.mazeControl.getCurrentPosition()[1]));
			
		}
		
		
		return r.mazeControl.isOutside(r.mazeControl.getCurrentPosition()[0], r.mazeControl.getCurrentPosition()[1]);
		
	}
}

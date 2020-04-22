package falstad;

import static org.junit.Assert.*;
import falstad.Robot.Turn;

import org.junit.Test;

import generation.CardinalDirection;
import generation.Cells;
import generation.MazeBuilderEller;

/**
 * These are whitebox tests cases for the functionality of MazeBuilderEller. All pass. 
 * @author  Meg Anderson and Trenten Ross
 * @version 2.0
 * @since   2017-10-17
 */
public class BasicRobotTest { 
	

/*Methods of BasicRobot 
 * 1. COOL rotate
 * 2. COOL move
 * 3. TRIVIAL getCurrentPosition
 * 4. TRIVIAL setMaze
 * 5. COOL isAtExit
 * 6. COOL canSeeExit
 * 7. COOL distanceToObstacle
 * 8. HIDDEN translateDirection
 * 9. COOL isInsideRoom
 * 10. TRIVIAL hasRoomSensor
 * 11. TRIVIAL getCurrentDirection
 * 12. TRIVIAL getBatteryLevel
 * 13. TRIVIAL setBatteryLevel
 * 14. TRIVIAL getOdometerReading
 * 15. TRIVIAL resetOdometer
 * 16. TRIVIAL gteEnergyforFullRotation
 * 17. TRIVIAL getEnergyForStepForward
 * 18. TRIVIAL hasStopped
 * 19. TRIVIAL hasDistanceSensor*/
	
	String workingdir = System.getProperty("user.dir")+"/test/data/input.xml";
	
	/**This test is fairly self explanatory: when loading the maze, the robot should never be able to see 
	 * the exit in any direction, or be at the Exit, upon initialization. */
	@Test
	public final void testCanSeeExit() {
		StubMazeController m = new StubMazeController(workingdir);
		m.init();
		//m.order();
		BasicRobot r = new BasicRobot(m);
		assertFalse(r.isAtExit());
		assertFalse(r.canSeeExit(falstad.Robot.Direction.LEFT));
		assert(r.getBatteryLevel() == r.initialBatteryLevel - r.senseCost);
		assertFalse(r.canSeeExit(falstad.Robot.Direction.RIGHT));
		assert(r.getBatteryLevel() == r.initialBatteryLevel - 2*r.senseCost);
		assertFalse(r.canSeeExit(falstad.Robot.Direction.FORWARD));
		assert(r.getBatteryLevel() == r.initialBatteryLevel - 3*r.senseCost);
		assertFalse(r.canSeeExit(falstad.Robot.Direction.BACKWARD));
		assert(r.getBatteryLevel() == r.initialBatteryLevel - 4*r.senseCost);
	}
	
	/**This test is also fairly self explanatory: the robot always begins facing East, and then should
	 * change direction with a rotation. This should also use up battery. */
	@Test
	public final void testBasicRotateLeft() {
		StubMazeController m = new StubMazeController(workingdir);
		m.init();
		BasicRobot r = new BasicRobot(m);
		assert(r.getCurrentDirection() == CardinalDirection.East);
		r.rotate(falstad.Robot.Turn.LEFT);
		assert(r.getBatteryLevel() == r.initialBatteryLevel - r.rotateCost);
		assert(r.getCurrentDirection() == CardinalDirection.North);
		r.rotate(falstad.Robot.Turn.LEFT);
		assert(r.getBatteryLevel() == r.initialBatteryLevel - 2*r.rotateCost);
		assert(r.getCurrentDirection() == CardinalDirection.West);
		r.rotate(falstad.Robot.Turn.LEFT);
		assert(r.getBatteryLevel() == r.initialBatteryLevel - 3*r.rotateCost);
		assert(r.getCurrentDirection() == CardinalDirection.South);
		assert(r.getBatteryLevel() != r.initialBatteryLevel);
	}
	
	/**Test to check the rotate right method in the same manor as the rotate Left. This should also use
	 * up battery */
	@Test
	public final void testBasicRotateRight() {
		StubMazeController m = new StubMazeController(workingdir);
		m.init();
		BasicRobot r = new BasicRobot(m);
		assert(r.getCurrentDirection() == CardinalDirection.East);
		r.rotate(falstad.Robot.Turn.RIGHT);
		assert(r.getBatteryLevel() == r.initialBatteryLevel - r.rotateCost);
		assert(r.getCurrentDirection() == CardinalDirection.South);
		r.rotate(falstad.Robot.Turn.RIGHT);
		assert(r.getBatteryLevel() == r.initialBatteryLevel - 2*r.rotateCost);
		assert(r.getCurrentDirection() == CardinalDirection.West);
		r.rotate(falstad.Robot.Turn.RIGHT);
		assert(r.getBatteryLevel() == r.initialBatteryLevel - 3*r.rotateCost);
		assert(r.getCurrentDirection() == CardinalDirection.North);
		assert(r.getBatteryLevel() != r.initialBatteryLevel);
	}
	
	/**Test to check the rotate around method in the same manor as the rotate Left and Right.
	 * The down key triggers the robot to turn around.  */
	@Test
	public final void testBasicRotateAround() {
		StubMazeController m = new StubMazeController(workingdir);
		m.init();
		BasicRobot r = new BasicRobot(m);
		assert(r.getCurrentDirection() == CardinalDirection.East);
		r.rotate(falstad.Robot.Turn.AROUND);
		assert(r.getBatteryLevel() == r.initialBatteryLevel - 2*r.rotateCost);
		assert(r.getCurrentDirection() == CardinalDirection.West);
		r.rotate(falstad.Robot.Turn.AROUND);
		assert(r.getBatteryLevel() == r.initialBatteryLevel - 4*r.rotateCost);
		assert(r.getCurrentDirection() == CardinalDirection.East);
	}
	
	/**This test relies upon knowledge of the preset maze in order to test both the move method and the 
	 * distanceToObstacle method. The preset maze begins with a long straight stretch of 11 cells.*/
	@Test
	public final void testMoveAndDistanceToObstacles() {
		StubMazeController m = new StubMazeController(workingdir);
		m.init();
		BasicRobot r = new BasicRobot(m);
		assert(r.distanceToObstacle(Robot.Direction.FORWARD) == 11);
		assert(r.distanceToObstacle(Robot.Direction.LEFT) == 0);
		assert(r.distanceToObstacle(Robot.Direction.RIGHT) == 0);
		assert(r.distanceToObstacle(Robot.Direction.BACKWARD) == 0);
		r.move(11, true);
		assert(r.distanceToObstacle(falstad.Robot.Direction.FORWARD) == 0);
		System.out.println(r.distanceToObstacle(Robot.Direction.LEFT));
		System.out.println(r.distanceToObstacle(Robot.Direction.RIGHT));
		assert(r.distanceToObstacle(Robot.Direction.LEFT) == 2);
		assert(r.distanceToObstacle(Robot.Direction.RIGHT) == 0);
		assert(r.distanceToObstacle(Robot.Direction.BACKWARD) == 11);
		r.rotate(falstad.Robot.Turn.LEFT);
		assert(r.distanceToObstacle(falstad.Robot.Direction.FORWARD) == 2);
		assert(r.distanceToObstacle(Robot.Direction.LEFT) == 11);
		assert(r.distanceToObstacle(Robot.Direction.RIGHT) == 0);
		assert(r.distanceToObstacle(Robot.Direction.BACKWARD) == 0);
		r.move(2, true);
		assert(r.distanceToObstacle(falstad.Robot.Direction.FORWARD) == 0);
		assert(r.distanceToObstacle(Robot.Direction.LEFT) == 0);
		assert(r.distanceToObstacle(Robot.Direction.RIGHT) == 3);
		assert(r.distanceToObstacle(Robot.Direction.BACKWARD) == 2);
		r.rotate(falstad.Robot.Turn.RIGHT);
		assert(r.distanceToObstacle(falstad.Robot.Direction.FORWARD) == 3);
		assert(r.distanceToObstacle(Robot.Direction.LEFT) == 0);
		assert(r.distanceToObstacle(Robot.Direction.RIGHT) == 2);
		assert(r.distanceToObstacle(Robot.Direction.BACKWARD) == 0);
		r.move(2, true);
		assert(r.distanceToObstacle(falstad.Robot.Direction.FORWARD) == 1);
		assert(r.distanceToObstacle(Robot.Direction.LEFT) == 5);
		assert(r.distanceToObstacle(Robot.Direction.RIGHT) == 1);
		assert(r.distanceToObstacle(Robot.Direction.BACKWARD) == 2);
		assert(r.isInsideRoom() == true);
	}
	
	/**This test relies upon knowledge of the preset maze in order to force the robot
	 * to move into a wall. This should yield a particular output in our implementation, 
	 * saying that the bot can't move forward anymore. When there is no longer a manual 
	 * driver this will be useful for troubleshooting, because under a manual 
	 * implementation this does not stop the game, because the user can just rotate the 
	 * bot out of it's predicament. */
	@Test
	public final void testMoveInInvalidDirection() {
		StubMazeController m = new StubMazeController(workingdir);
		m.init();
		BasicRobot r = new BasicRobot(m);
		int[] currPos = new int[2];
		try {
			currPos = r.getCurrentPosition();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		r.rotate(falstad.Robot.Turn.LEFT);
		assert(r.distanceToObstacle(falstad.Robot.Direction.FORWARD) == 0);
		r.move(1, true);	
		int[] newPos = new int[2];
		try {
			newPos = r.getCurrentPosition();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert(currPos[0] == newPos[0]);
		assert(currPos[1] == newPos[1]);
	}
	
	/**This ensures the robot stops once the battery have been exhausted. The initial Battery of
	 * a basic robot is 3000. To rotate costs 3, and so the robot's battery will be empty after
	 * a full 1000 rotations. This rotates the robot round n round and then checks it has stopped */
	@Test
	public final void testBatteryLevel() {
		StubMazeController m = new StubMazeController(workingdir);
		m.init();
		BasicRobot r = new BasicRobot(m);
		for ( int i = 0; i < 1001; i ++)
			r.rotate(falstad.Robot.Turn.LEFT);
		assertTrue(r.hasStopped());	
	}
	
	/**This test ensures that for every forward movement the odomoeter increments by one */
	@Test
	public final void testMovewithOdometer() {
		StubMazeController m = new StubMazeController(workingdir);
		m.init();
		BasicRobot r = new BasicRobot(m);
		r.move(5, true);
		assert(r.distanceToObstacle(falstad.Robot.Direction.FORWARD) > 0);
		assert(r.getOdometerReading() == 5);
	}
	
	/**This test ensures that for every forward movement the odomoeter increments by one, but
	 * also that the odometer can be reset*/
	@Test
	public final void testResetOdometer() {
		StubMazeController m = new StubMazeController(workingdir);
		m.init();
		BasicRobot r = new BasicRobot(m);
		r.move(5, true);
		assert(r.distanceToObstacle(falstad.Robot.Direction.FORWARD) > 0);
		assert(r.getOdometerReading() == 5);
		r.resetOdometer();
		assert(r.getOdometerReading() == 0);
	}
	
}


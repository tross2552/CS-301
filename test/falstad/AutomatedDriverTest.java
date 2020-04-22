package falstad;

import static org.junit.Assert.fail;

import org.junit.Test;

import falstad.MazeController.UserInput;
import generation.CardinalDirection;

public abstract class AutomatedDriverTest<T extends ManualDriver> { //essentially blackbox testing
	
	//driver algorithm being tested, set in @BeforeClass
	
	T driver;
	
	String mazeDir = System.getProperty("user.dir")+"/test/data/input.xml";
	
	
	//create the test maze and ensure robot solves it
	@Test
	public final void testCanSolveTestMaze() {
		StubMazeController m = new StubMazeController(mazeDir);
		m.init();
		
		driver.r.setMaze(m);
		try {
			assert(driver.drive2Exit());
		}
		catch(Exception e) {
			e.printStackTrace();
			fail("Driver failed somehow?");
		}
	}
	
	//ensure that player input is ignored (I /think/ that's right) 
	@Test
	public final void noManualDrive() {
		StubMazeController m = new StubMazeController(mazeDir);
		m.init();
		driver.r.setMaze(m);
		CardinalDirection dir = driver.r.currentDirection;
		driver.manualDrive(UserInput.Left, 0);
		assert(driver.r.currentDirection==dir);
	}
	
	//create same maze twice and ensure robot takes same path
	@Test
	public final void testPathDeterministic() {
		StubMazeController m = new StubMazeController(mazeDir);
		m.init();
		driver.r.setMaze(m);
		try {
			assert(driver.drive2Exit());
		}
		catch(Exception e) {
			e.printStackTrace();
			fail("Driver failed somehow?");
		}
		int distance = driver.r.getOdometerReading();
		float battery = driver.r.getBatteryLevel();
		
		driver.r = new BasicRobot();
		
		m = new StubMazeController(mazeDir);
		m.init();
		driver.r.setMaze(m);
		try {
			assert(driver.drive2Exit());
		}
		catch(Exception e) {
			e.printStackTrace();
			fail("Driver failed somehow?");
		}
		
		//TODO: find a better way to test this
		assert(distance == driver.r.getOdometerReading());
		assert(battery == driver.r.getBatteryLevel());
		
	}
}

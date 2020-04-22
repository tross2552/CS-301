package falstad;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WallFollowerTest extends AutomatedDriverTest<WallFollower> {
	
	@Before
	public final void setDriver() {
		driver = new WallFollower();
	}
	
	@After
	public final void resetRobot() {
		driver.r.resetRobot();
	}
	
	//if it starts following isolated set of walls, should follow until battery eventually dies
	@Test
	public final void testCanNotSolveMazeWithCycles() {
		
	}
	
}

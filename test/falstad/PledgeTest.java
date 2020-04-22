package falstad;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PledgeTest extends AutomatedDriverTest<Pledge> {
	
	@Before
	public final void setDriver() {
		//set the driver used in AutomatedDriverTest and here
		driver = new Pledge();
	}
	
	@After
	public final void resetRobot() {
		driver.r.resetRobot();
	}
	
	@Test
	public final void testCanSolveMazeWithCycles() {
		MazeController m = new StubMazeController();
		driver.r.setMaze(m);
		
		try {
			assert(driver.drive2Exit());
		}
		catch(Exception e) {
			e.printStackTrace();
			fail("Driver failed somehow?");
		}
	}
	
	
	
}

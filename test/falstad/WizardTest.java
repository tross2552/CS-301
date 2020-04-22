package falstad;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WizardTest extends AutomatedDriverTest<Wizard> {
	
	@Before
	public final void setDriver() {
		//set the driver used in AutomatedDriverTest and here
		driver = new Wizard();
	}
	
	@After
	public final void resetRobot() {
		driver.r.resetRobot();
	}
	
	//@Test
	//public final void testPathIsShortest() {
		
	//}
}

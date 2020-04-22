package falstad;


//import java.awt.event.KeyListener;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.image.BufferedImage;
import java.io.File;
import falstad.Constants.StateGUI;


/**
 * Implements the screens that are displayed whenever the game is not in 
 * the playing state. The screens shown are the title screen, 
 * the generating screen with the progress bar during maze generation,
 * and the final screen when the game finishes.
 * @author pk
 *
 */
public class MazeView extends DefaultViewer {

	// need to know the maze model to check the state 
	// and to provide progress information in the generating state
	private MazeController controller ;
	boolean won;
	int steps;
	float batteryLevel;
	ModifiedKeyListener listener;
	
	public MazeView(MazeController c) {
		super() ;
		controller = c ;
	}

	@Override
	public void redraw(MazePanel panel, StateGUI state, int px, int py, int view_dx,
			int view_dy, int walk_step, int view_offset, RangeSet rset, int ang) {
		//dbg("redraw") ;
		switch (state) {
		case STATE_TITLE:
			redrawTitle(panel);
			break;
		case STATE_GENERATING:
			redrawGenerating(panel);
			break;
		case STATE_PLAY:
			// skip this one
			break;
		case STATE_FINISH:
			redrawFinish(panel, won);
			break;
		}
	}
	
	/**This method creates the components of the JPanel, and displays them. IT also sends the information 
	 * gathered from the other comboboxes to the maze controller. It also sends commands to create a new
	 * RobotDriver in the key listener.*/
	
	void setTitlePanel(MazePanel panel) {
		
		panel.setTitlePanel(controller, listener);
		
	}
		
	
	private void dbg(String str) {
		System.out.println("MazeView:" + str);
	}
	
	// 
	
	/**
	 * Helper method for redraw to draw the title screen, screen is hard coded
	 * @param  gc graphics is the off screen image
	 */
	void redrawTitle(MazePanel panel) {
		// produce white background
		panel.setColor(panel.white);
		panel.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		// write the title 
		panel.setFont(panel.largeBannerFont);
		panel.setColor(panel.red);
		panel.centerString(panel,"MAZE", 100);
		// write the reference to falstad
		panel.setColor(panel.blue);
		panel.setFont(panel.smallBannerFont);
		panel.centerString(panel,"by Paul Falstad", 160);
		panel.centerString(panel, "www.falstad.com", 190);
		// write the instructions
		panel.setColor(panel.black);
		panel.centerString(panel, "To start, select a skill level.", 250);
		panel.centerString(panel, "(Press a number from 0 to 9,", 300);
		panel.centerString(panel, "or a letter from A to F)", 320);
		panel.centerString(panel, "Version 2.0", 350);
	}
	/**
	 * Helper method for redraw to draw final screen, screen is hard coded
	 * @param gc graphics is the off screen image
	 */
	void redrawFinish(MazePanel panel, boolean won) {
		
		// produce blue background
		String txt = won ? "You won!" : "You lost...";
		panel.setColor(won ? panel.blue : panel.red);
		panel.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		if(!won) {
			
			String workingdir = System.getProperty("user.dir");
			File file = new File(workingdir+"/Pictures/darkLOLs.jpg") ;
			
			panel.drawImage(file);
		}
		// write the title 
		panel.setFont(panel.largeBannerFont);
		panel.setColor(panel.yellow);
		panel.centerString(panel, txt, 100);
		// write some extra blufb
		panel.setColor(panel.orange);
		panel.setFont(panel.smallBannerFont);
		if(won)  {
			panel.centerString(panel, "Congratulations!", 160);
		}
		panel.centerString(panel, "Energy Consumed: "+batteryLevel, 190);
		panel.centerString(panel, "Steps Travelled: "+steps, 220);
		// write the instructions
		panel.setColor(panel.white);
		panel.centerString(panel, "Hit any key to restart", 300);
	}

	/**
	 * Helper method for redraw to draw screen during phase of maze generation, screen is hard coded
	 * only attribute percentdone is dynamic
	 * @param gc graphics is the off screen image
	 */
	void redrawGenerating(MazePanel panel) {
		// produce yellow background
		panel.setColor(panel.yellow);
		panel.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		// write the title 
		panel.setFont(panel.largeBannerFont);
		panel.setColor(panel.red);
		panel.centerString(panel, "Building maze", 150);
		panel.setFont(panel.smallBannerFont);
		// show progress
		panel.setColor(panel.black);
		if (null != controller)
			panel.centerString(panel, controller.getPercentDone()+"% completed", 200);
		else
			panel.centerString(panel, "Error: no controller, no progress", 200);
		// write the instructions
		panel.centerString(panel, "Hit escape to stop", 300);
	}
	

}

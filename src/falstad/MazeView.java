package falstad;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import falstad.Constants.StateGUI;
import generation.Order.Builder;

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
	public void redraw(Graphics gc, StateGUI state, int px, int py, int view_dx,
			int view_dy, int walk_step, int view_offset, RangeSet rset, int ang) {
		//dbg("redraw") ;
		switch (state) {
		case STATE_TITLE:
			redrawTitle(gc);
			break;
		case STATE_GENERATING:
			redrawGenerating(gc);
			break;
		case STATE_PLAY:
			// skip this one
			break;
		case STATE_FINISH:
			redrawFinish(gc, won);
			break;
		}
	}
	
	void setTitlePanel() {
		MazeController.setTitlePanel();
		
		Container pane=MazeController.getTitlePanel();
		
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
	    
	    JLabel jlabel = new JLabel("MAZE");
	    jlabel.setFont(new Font("Courier New", Font.BOLD, 40)); 
	    jlabel.setForeground(Color.RED);
	    jlabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	    pane.add(jlabel);
	    
	    JLabel author = new JLabel("originally by Paul Falstad || " + "www.falstad.com");
	    author.setFont(new Font("Times New Roman",Font.PLAIN,15));
	    author.setForeground(Color.BLUE);
	    author.setAlignmentX(Component.CENTER_ALIGNMENT);
	    pane.add(author);
	    
	    JLabel prof = new JLabel("refactored by Peter Kemper,");
	    prof.setFont(new Font("Times New Roman",Font.PLAIN,15));
	    prof.setForeground(Color.BLACK);
	    prof.setAlignmentX(Component.CENTER_ALIGNMENT);
	    pane.add(prof);
	    
	    JLabel us = new JLabel("Meg Anderson, and Trenten Ross");
	    us.setFont(new Font("Times New Roman",Font.PLAIN,15));
	    us.setForeground(Color.BLACK);
	    us.setAlignmentX(Component.CENTER_ALIGNMENT);
	    pane.add(us);
	    
	    JLabel breathe = new JLabel(" ");
	    breathe.setFont(new Font("Times New Roman",Font.PLAIN,10));
	    pane.add(breathe);
	    
	    String skillText = "Select a skill level";
	    //String [] skills = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
	    Integer [] skills = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
	    final JComboBox<Integer> skillLevelBox = new JComboBox<Integer>(skills);
	    skillLevelBox.setToolTipText("Select a difficulty level");
	    skillLevelBox.setAlignmentX(Component.CENTER_ALIGNMENT);
	    JPanel pSkillLevelBox = new JPanel(new BorderLayout());
	    pSkillLevelBox.setBorder(new TitledBorder(skillText));
	    pSkillLevelBox.add(skillLevelBox);
	    pSkillLevelBox.add(Box.createRigidArea(new Dimension(100,0)),BorderLayout.WEST);
	    pSkillLevelBox.add(Box.createRigidArea(new Dimension(100,0)),BorderLayout.EAST);
	    pSkillLevelBox.setPreferredSize(new Dimension(350, 50));
	    pSkillLevelBox.setBackground(Color.WHITE);
	    pane.add(pSkillLevelBox);
	    
	    String algoText = "Select a maze generation algorithm";
	    String [] algo = {"Eller", "Prim", "Depth-First Search", "Load Test Maze"};
	    final JComboBox<String> algoBox = new JComboBox<String>(algo);
	    algoBox.setToolTipText("Select a generation Algorithm");
	    algoBox.setAlignmentX(Component.CENTER_ALIGNMENT);
	    JPanel pAlgoBox = new JPanel(new BorderLayout());
	    pAlgoBox.setBorder(new TitledBorder(algoText));
	    pAlgoBox.add(algoBox);
	    pAlgoBox.add(Box.createRigidArea(new Dimension(100,0)),BorderLayout.WEST);
	    pAlgoBox.add(Box.createRigidArea(new Dimension(100,0)),BorderLayout.EAST);
	    pAlgoBox.setBackground(Color.WHITE);
	    pane.add(pAlgoBox);
	
	    //        Dimension size = new Dimension(50, algoBox.getPreferredSize().height);
	
	    //        algoBox.setPreferredSize(size);
	    //pane.add(algoBox);
	    
	    String driverText = "Select a maze solving algorithm";
	    String [] drivers = {"Manual", "Wizard", "Wall Follower", "Pledge"};
	    final JComboBox<String> driverBox = new JComboBox<String>(drivers);
	    driverBox.setToolTipText("CHOOSE YOUR WEAPON");
	    algoBox.setAlignmentX(Component.CENTER_ALIGNMENT);
	    JPanel pDriverBox = new JPanel(new BorderLayout());
	    pDriverBox.setBorder(new TitledBorder(driverText));
	    pDriverBox.add(driverBox);
	    pDriverBox.add(Box.createRigidArea(new Dimension(100,0)),BorderLayout.WEST);
	    pDriverBox.add(Box.createRigidArea(new Dimension(100,0)),BorderLayout.EAST);
	    pDriverBox.setBackground(Color.WHITE);
	    pane.add(pDriverBox);
	    
	    
	    //JPanel pStartBox = new JPanel(new BorderLayout());
	    JButton button = new JButton("Start?");
	    button.setAlignmentX(Component.CENTER_ALIGNMENT);
	    button.addMouseListener( new MouseAdapter() {
	    		@Override
	    		public void mousePressed(MouseEvent me) {
	    			int level = (int) skillLevelBox.getSelectedItem();
	    			String generation = (String) algoBox.getSelectedItem();
	    			String driver = (String) driverBox.getSelectedItem();
	    			
	    			if (generation.equals("Eller")) 
	    				controller.setBuilder(Builder.Eller);
	    			else if (generation.equals("Prim"))
	    				controller.setBuilder(Builder.Prim);
	    			else if (generation.equals("Depth-First Search"))
	    				controller.setBuilder(Builder.DFS);
	    			else if (generation.equals("Load Test Maze")) {
	    				String workingdir = System.getProperty("user.dir");
	    				String file = workingdir + "/test/data/input.xml";
	    				controller = new MazeController(file);
	    				controller.init();
	    			}
	    			
	    			RobotDriver drive = null;
	    			if (driver.equals("Manual")) {
	    				drive = new ManualDriver();
	    				listener.startManual(level);
	    			}
	    			else if (driver.equals("Wizard")) {
	    				drive = new Wizard();
	    				try {
							drive.drive2Exit();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	    			}
	    			else if (driver.equals("Wall Follower")) {
	    				drive = new WallFollower();
	    				try {
							drive.drive2Exit();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	    			}
	    				
	    			else if (driver.equals("Pledge")) {
	    				drive = new Pledge();
	    				try {
							drive.drive2Exit();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	    			}
	    		}
	    });
	    pane.add(button);
	    //pane.add(pStartBox);
		
	}
		
	
	private void dbg(String str) {
		System.out.println("MazeView:" + str);
	}
	
	// 
	
	/**
	 * Helper method for redraw to draw the title screen, screen is hard coded
	 * @param  gc graphics is the off screen image
	 */
	void redrawTitle(Graphics gc) {
		// produce white background
		gc.setColor(Color.white);
		gc.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		// write the title 
		gc.setFont(largeBannerFont);
		FontMetrics fm = gc.getFontMetrics();
		gc.setColor(Color.red);
		centerString(gc, fm, "MAZE", 100);
		// write the reference to falstad
		gc.setColor(Color.blue);
		gc.setFont(smallBannerFont);
		fm = gc.getFontMetrics();
		centerString(gc, fm, "by Paul Falstad", 160);
		centerString(gc, fm, "www.falstad.com", 190);
		// write the instructions
		gc.setColor(Color.black);
		centerString(gc, fm, "To start, select a skill level.", 250);
		centerString(gc, fm, "(Press a number from 0 to 9,", 300);
		centerString(gc, fm, "or a letter from A to F)", 320);
		centerString(gc, fm, "Version 2.0", 350);
	}
	/**
	 * Helper method for redraw to draw final screen, screen is hard coded
	 * @param gc graphics is the off screen image
	 */
	void redrawFinish(Graphics gc, boolean won) {
		
		// produce blue background
		String txt = won ? "You won!" : "You lost...";
		Color color = won ? Color.blue : Color.red;
		gc.setColor(color);
		gc.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		if(!won) {
			
			String workingdir = System.getProperty("user.dir");
			File file = new File(workingdir+"/Pictures/darkLOLs.jpg") ;
			BufferedImage image = null;
			try {
				image = ImageIO.read(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			gc.drawImage(image, 0, 0, Constants.VIEW_WIDTH-16, Constants.VIEW_HEIGHT-16, 0, 0, image.getWidth(), image.getHeight(), null);
		}
		// write the title 
		gc.setFont(largeBannerFont);
		FontMetrics fm = gc.getFontMetrics();
		gc.setColor(Color.yellow);
		centerString(gc, fm, txt, 100);
		// write some extra blufb
		gc.setColor(Color.orange);
		gc.setFont(smallBannerFont);
		fm = gc.getFontMetrics();
		if(won)  {
			centerString(gc, fm, "Congratulations!", 160);
		}
		centerString(gc, fm, "Energy Consumed: "+batteryLevel, 190);
		centerString(gc, fm, "Steps Travelled: "+steps, 220);
		// write the instructions
		gc.setColor(Color.white);
		centerString(gc, fm, "Hit any key to restart", 300);
	}

	/**
	 * Helper method for redraw to draw screen during phase of maze generation, screen is hard coded
	 * only attribute percentdone is dynamic
	 * @param gc graphics is the off screen image
	 */
	void redrawGenerating(Graphics gc) {
		// produce yellow background
		gc.setColor(Color.yellow);
		gc.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		// write the title 
		gc.setFont(largeBannerFont);
		FontMetrics fm = gc.getFontMetrics();
		gc.setColor(Color.red);
		centerString(gc, fm, "Building maze", 150);
		gc.setFont(smallBannerFont);
		fm = gc.getFontMetrics();
		// show progress
		gc.setColor(Color.black);
		if (null != controller)
			centerString(gc, fm, controller.getPercentDone()+"% completed", 200);
		else
			centerString(gc, fm, "Error: no controller, no progress", 200);
		// write the instructions
		centerString(gc, fm, "Hit escape to stop", 300);
	}
	
	private void centerString(Graphics g, FontMetrics fm, String str, int ypos) {
		g.drawString(str, (Constants.VIEW_WIDTH-fm.stringWidth(str))/2, ypos);
	}

	final Font largeBannerFont = new Font("TimesRoman", Font.BOLD, 48);
	final Font smallBannerFont = new Font("TimesRoman", Font.BOLD, 16);

}

package falstad;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.*;

import falstad.MazeController.UserInput;
import generation.Order.Builder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Add functionality for double buffering to an AWT Panel class.
 * Used for drawing a maze.
 * 
 * @author pk
 *
 */
public class MazePanel extends Panel  {
	/* Panel operates a double buffer see
	 * http://www.codeproject.com/Articles/2136/Double-buffer-in-standard-Java-AWT
	 * for details
	 */
	// bufferImage can only be initialized if the container is displayable,
	// uses a delayed initialization and relies on client class to call initBufferImage()
	// before first use
	private Image bufferImage;  
	private Graphics2D graphics; // obtained from bufferImage, 
	// graphics is stored to allow clients to draw on same graphics object repeatedly
	// has benefits if color settings should be remembered for subsequent drawing operations
	public static JPanel titlePanel;
	
	
	//All of the colors we use
	final Color red = Color.red;
	final Color blue = Color.blue;
	final Color yellow = Color.yellow;
	final Color orange = Color.orange;
	final Color white = Color.white;
	final Color black = Color.black;
	final Color darkGray = Color.darkGray;
	final Color gray = Color.gray;
	final Color lightBlue = new Color(135, 206, 250);
	final Color floor = new Color(143, 188, 143);
	
	
	/**
	 * Constructor. Object is not focusable.
	 */
	public MazePanel() {
		setFocusable(false);
		bufferImage = null; // bufferImage initialized separately and later
		graphics = null;
		
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	/**
	 * Method to draw the buffer image on a graphics object that is
	 * obtained from the superclass. The method is used in the MazeController.
	 * Warning: do not override getGraphics() or drawing might fail. 
	 */
	public void update() {
		paint(getGraphics());
	}
	

	/**
	 * Draws the buffer image to the given graphics object.
	 * This method is called when this panel should redraw itself.
	 */
	@Override
	public void paint(Graphics g) {
		if (null == g) {
			System.out.println("MazePanel.paint: no graphics object, skipping drawImage operation");
		}
		else {
			g.drawImage(bufferImage,0,0,null);	
		}
	}

	public void initBufferImage() {
		bufferImage = createImage(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		if (null == bufferImage)
		{
			System.out.println("Error: creation of buffered image failed, presumedly container not displayable");
		}
	}
	/**
	 * Obtains a graphics object that can be used for drawing.
	 * The object internally stores the graphics object and will return the
	 * same graphics object over multiple method calls. 
	 * To make the drawing visible on screen, one needs to trigger 
	 * a call of the paint method, which happens 
	 * when calling the update method. 
	 * @return graphics object to draw on, null if impossible to obtain image
	 */
	public Graphics getBufferGraphics() {
		if (null == graphics) {
			// instantiate and store a graphics object for later use
			if (null == bufferImage)
				initBufferImage();
			if (null == bufferImage)
				return null;
			graphics = (Graphics2D) bufferImage.getGraphics();
			if (null == graphics) {
				System.out.println("Error: creation of graphics for buffered image failed, presumedly container not displayable");
			}
			// success case
			
			//System.out.println("MazePanel: Using Rendering Hint");
			// For drawing in FirstPersonDrawer, setting rendering hint
			// became necessary when lines of polygons 
			// that were not horizontal or vertical looked ragged
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		}
		
		return graphics;
	}
	
	public void setColor(Color c) {
		this.getBufferGraphics().setColor(c);
	}
	
	public void fillRect(int x, int y,  int width, int height) {
		this.getBufferGraphics().fillRect(x, y, width, height);
	}
	
	public void centerString(MazePanel panel, String str, int ypos) {
		FontMetrics fm = panel.getFontMetrics(panel.getBufferGraphics().getFont());
		panel.getBufferGraphics().drawString(str, (Constants.VIEW_WIDTH-fm.stringWidth(str))/2, ypos);
	}
	
	public void drawImage(File file) {
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		this.getBufferGraphics().drawImage(image, 0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, 0, 0, image.getWidth(), image.getHeight(), null);
	}
	
	public void setFont(Font f) {
		this.getBufferGraphics().setFont(f);
	}
	
	public void fillPolygon(int[] xpos, int[] ypos, int sides) {
		this.getBufferGraphics().fillPolygon(xpos, ypos, sides);
	}
	
	public static void setTitlePanel(MazeController controller, ModifiedKeyListener listener) {
		
		titlePanel = new JPanel();
		
		titlePanel.setBackground( Color.WHITE );
		titlePanel.setPreferredSize(new Dimension(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT));
		
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
	    
	    JLabel jlabel = new JLabel("MAZE");
	    jlabel.setFont(new Font("Courier New", Font.BOLD, 40)); 
	    jlabel.setForeground(Color.RED);
	    jlabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	    titlePanel.add(jlabel);
	    
	    JLabel author = new JLabel("originally by Paul Falstad || " + "www.falstad.com");
	    author.setFont(new Font("Times New Roman",Font.PLAIN,15));
	    author.setForeground(Color.BLUE);
	    author.setAlignmentX(Component.CENTER_ALIGNMENT);
	    titlePanel.add(author);
	    
	    JLabel prof = new JLabel("refactored by Peter Kemper,");
	    prof.setFont(new Font("Times New Roman",Font.PLAIN,15));
	    prof.setForeground(Color.BLACK);
	    prof.setAlignmentX(Component.CENTER_ALIGNMENT);
	    titlePanel.add(prof);
	    
	    JLabel us = new JLabel("Meg Anderson, and Trenten Ross");
	    us.setFont(new Font("Times New Roman",Font.PLAIN,15));
	    us.setForeground(Color.BLACK);
	    us.setAlignmentX(Component.CENTER_ALIGNMENT);
	    titlePanel.add(us);
	    
	    JLabel breathe = new JLabel(" ");
	    breathe.setFont(new Font("Times New Roman",Font.PLAIN,10));
	    titlePanel.add(breathe);
	    
	    String skillText = "Select a skill level";
	    //String [] skills = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
	    Integer [] skills = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
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
	    pSkillLevelBox.setFocusable(false);
	    titlePanel.add(pSkillLevelBox);
	    
	    String algoText = "Select a maze generation algorithm";
	    String [] algo = {"Eller", "Prim", "Depth-First Search"};
	    final JComboBox<String> algoBox = new JComboBox<String>(algo);
	    algoBox.setToolTipText("Select a generation Algorithm");
	    algoBox.setAlignmentX(Component.CENTER_ALIGNMENT);
	    JPanel pAlgoBox = new JPanel(new BorderLayout());
	    pAlgoBox.setBorder(new TitledBorder(algoText));
	    pAlgoBox.add(algoBox);
	    pAlgoBox.add(Box.createRigidArea(new Dimension(100,0)),BorderLayout.WEST);
	    pAlgoBox.add(Box.createRigidArea(new Dimension(100,0)),BorderLayout.EAST);
	    pAlgoBox.setBackground(Color.WHITE);
	    pAlgoBox.setFocusable(false);
	    titlePanel.add(pAlgoBox);
	
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
	    pDriverBox.setFocusable(false);
	    titlePanel.add(pDriverBox);
	    
	    
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
	    			
	    			RobotDriver drive = null;
	    			if (driver.equals("Manual")) {
	    				drive = new ManualDriver();
	    				listener.startManual(level);
	    			}
	    			else {
	    				
	    				controller.keyDown(UserInput.Start, level);
	    				listener.startAuto(driver,controller);
	    			}
	    			
	    		}
	    });
	    button.setFocusable(false);
	    titlePanel.add(button);
	    //pane.add(pStartBox);
		
	}
	

	final Font largeBannerFont = new Font("TimesRoman", Font.BOLD, 48);
	final Font smallBannerFont = new Font("TimesRoman", Font.BOLD, 16);


	public void drawLine(int nx1, int ny1, int nx2, int ny2) {
		this.getBufferGraphics().drawLine(nx1, ny1, nx2, ny2);
		
	}

	public void fillOval(int i, int j, int cirsiz, int cirsiz2) {
		this.getBufferGraphics().fillOval(i, j, cirsiz, cirsiz2);
		
	}
	
	public static JPanel getTitlePanel() {
		return titlePanel;
	}

}


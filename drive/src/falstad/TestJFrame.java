/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package falstad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;

/*
 * BoxLayoutDemo.java requires no other files.
 */

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import generation.Order.Builder;

public class TestJFrame {
    public static void addComponentsToPane(Container pane) {
        
    		
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
        			
        			Builder build = null;
        			if (generation.equals("Eller"))
        				build = Builder.Eller;
        			else if (generation.equals("Prim"))
        				build = Builder.Prim;
        			else if (generation.equals("Depth-First Search"))
        				build = Builder.DFS;
        			
        			RobotDriver drive = null;
        			if (driver.equals("Manual"))
        				drive = new ManualDriver();
        			else if (driver.equals("Wizard"))
        				drive = new Wizard();
        			else if (driver.equals("Wall Follower"))
        				drive = new WallFollower();
        			else if (driver.equals("Pledge"))
        				drive = new Pledge();
        			
        			
	
        		}
        });
        pane.add(button);
        //pane.add(pStartBox);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("");
        Dimension size = new Dimension(400, 400);
        frame.setPreferredSize(size);
        frame.getContentPane().setBackground( Color.WHITE );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}

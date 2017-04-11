package com.usc.hanafuda.screens;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.usc.hanafuda.HClient;
import com.usc.hanafuda.MyGame;

public class MenuScreen extends JPanel {
	private JButton singlePlayButton;
	private JButton howToButton;
	ImageIcon icon;
	Image scaledImage;
	BufferedImage originalImage = null;
	
	
	public MenuScreen (final MyGame myGame, HClient client) {
		// Set layout manager
		this.setLayout (new GridBagLayout());
		
		// Create panel to show in the center
		JPanel center = new JPanel (new GridLayout (2,1));
		center.setOpaque (false);
		add (center);
		
		// Create image for center of menu
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("haha.png"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Set background of center panel (Hanafuda title)
		JLabel imageLabel = new JLabel (new ImageIcon (image));
		JLabel backgroundLabel = new JLabel();
		backgroundLabel.setIcon (icon);
		center.add (imageLabel);
		
		// Create panel for menu buttons
		JPanel south = new JPanel();
		center.add (south);		
		south.setOpaque (false);
		
		// Set up play button
		singlePlayButton = new JButton("Play"); 
		singlePlayButton.setFont (new Font ("TimesRoman", Font.BOLD, 30));
		singlePlayButton.addActionListener (new ActionListener() {
			public void actionPerformed (ActionEvent aa) {
				// When the play button is clicked, create a game screen; set in MyGame to access later
				GameScreen gs = new GameScreen (myGame, client);
				myGame.setGameScreen(gs);
				myGame.setPanel(gs);
			}
		});

		// Set up how to button
		howToButton = new JButton ("How To");
		howToButton.setFont (new Font ("TimesRoman", Font.BOLD, 30));
		howToButton.addActionListener (new ActionListener() {
			public void actionPerformed (ActionEvent ae) {
				new HowToScreen();
			}
		});
		
		// Set preferred sizes of buttons
		singlePlayButton.setPreferredSize(new Dimension(150, 100));
		howToButton.setPreferredSize(new Dimension(150, 100));
		
		// Add buttons to their panel
		south.add (singlePlayButton);
		south.add (howToButton);
		
	}
	
	
	protected void paintComponent (Graphics g) {
		super.paintComponent (g);
		
		try {
			// Background image
			originalImage = ImageIO.read(new File("hanafudaBG.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		scaledImage = originalImage.getScaledInstance (this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
		g.drawImage (scaledImage, 0, 0, this.getWidth(), this.getHeight(), this);

	}
	

}

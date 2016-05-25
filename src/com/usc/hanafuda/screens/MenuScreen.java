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

import com.usc.hanafuda.MyGame;

public class MenuScreen extends JPanel {
	
	
	private JButton singlePlayButton;
	//private JButton multiPlayButton;
	private JButton howToButton;
	ImageIcon icon;
	Image scaledImage;
	BufferedImage originalImage = null;
	
	public MenuScreen(final MyGame myGame) {
		
		this.setLayout(new GridBagLayout());
		JPanel center = new JPanel(new GridLayout(2,1));
		
		center.setOpaque(false);
		add(center);
		BufferedImage image = null;
		
		//Image scaledImage = originalImage.getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
		try {
			image = ImageIO.read(new File("haha.png"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JLabel imageLabel = new JLabel(new ImageIcon(image));
		JLabel backgroundLabel = new JLabel();
		backgroundLabel.setIcon(icon);
		center.add(imageLabel);
		
		JPanel south = new JPanel();
		center.add(south);
		
		south.setOpaque(false);
		singlePlayButton = new JButton("Play"); 
		singlePlayButton.setFont(new Font("TimesRoman", Font.BOLD, 30));
		singlePlayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aa) {
				GameScreen gs = new GameScreen (myGame);
				myGame.setGameScreen(gs);
				myGame.setPanel(gs);
				
			}
		});

		
		howToButton = new JButton("How To");
		howToButton.setFont(new Font("TimesRoman", Font.BOLD, 30));
		howToButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				new HowToScreen();
			}
		});
		singlePlayButton.setPreferredSize(new Dimension(150, 100));
		//multiPlayButton.setPreferredSize(new Dimension(150, 100));
		howToButton.setPreferredSize(new Dimension(150, 100));
		
		south.add(singlePlayButton);
		//south.add(multiPlayButton);
		south.add(howToButton);

		
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		
		try {
			originalImage = ImageIO.read(new File("hanafudaBG.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		scaledImage = originalImage.getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
		g.drawImage(scaledImage, 0, 0, this.getWidth(), this.getHeight(), this);
		//g.setColor(new Color(246, 244, 186));
		
		//g.fillRect(0, 0, 1400, 1000);

	}
	

}

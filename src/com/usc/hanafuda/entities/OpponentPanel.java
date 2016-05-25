package com.usc.hanafuda.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import com.usc.hanafuda.HClient;
import com.usc.hanafuda.screens.GameScreen;

public class OpponentPanel extends JPanel implements Runnable{

	private JButton showCapturedCard;
	private JLabel nameLabel;
	private static JLabel numCards;
	private static JLabel scoreLabel;
	private JLabel userScore;
	private String playerName = " ";
	private static int cardLeft = 8;
	private int score = 0;
	private static OpponentCollectionPanel opponentCollectionPanel;
	private static BufferedImage cardFaceDown;
	private static JPanel cardPanel;
	private GameScreen gameScreen;
	static HClient hClient;
	static boolean refreshFlag = false;
	
	Lock lock = new ReentrantLock();
	
	public OpponentPanel(HClient hClient, GameScreen gs){
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.playerName = playerName;
		this.hClient =  hClient;
		this.gameScreen=gs;
		cardLeft = 8;
		
		nameLabel = new JLabel(playerName + " ");
		nameLabel.setFont(new Font("Monotype Corsiva", Font.PLAIN, 30));
		this.add(nameLabel);
		this.add(Box.createHorizontalGlue());
		showCapturedCard = new JButton("Show Captured Cards");
		showCapturedCard.setMaximumSize(new Dimension(160,40));
		opponentCollectionPanel = new OpponentCollectionPanel();
//		opponentCollectionPanel.setEditable(false);
		
 
		int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
	    int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
		final JScrollPane jsp = new JScrollPane(opponentCollectionPanel, v , h);
		this.add(showCapturedCard);
		
		showCapturedCard.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				JFrame popup = new JFrame("Opponent's Collection Cards");
				popup.setDefaultCloseOperation(popup.DISPOSE_ON_CLOSE);
				popup.setSize(500,300);
				popup.setLocation(100, 100);
				popup.setVisible(true);
				popup.setResizable(true);
				popup.add(jsp);
				
			}
		});
		cardPanel = new JPanel();
		cardPanel.setBackground(Color.LIGHT_GRAY);
		add(cardPanel);

				
		
		//this.add(jsp);
		this.add(Box.createHorizontalGlue());
		
		JPanel eastPanel = new JPanel();
		eastPanel.setBackground(Color.LIGHT_GRAY);
		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
		
		scoreLabel = new JLabel("Score: " + Integer.toString(score) + " ");
		scoreLabel.setFont(new Font("Monotype Corsiva", Font.PLAIN, 30));
		eastPanel.add(scoreLabel);
		
		numCards = new JLabel("Cards left: " + cardLeft + " ");
		numCards.setFont(new Font("Monotype Corsiva", Font.PLAIN, 30));
		eastPanel.add(numCards);
		
		this.add(eastPanel);
		this.setBorder(BorderFactory.createLineBorder(Color.darkGray, 3));
		this.setBackground(Color.LIGHT_GRAY);
		this.setPreferredSize(new Dimension (1150, 200));
		this.setMinimumSize(new Dimension (1150, 200));
		this.setMaximumSize(new Dimension (1150, 200));
		
		try {
			cardFaceDown = ImageIO.read(new File("deck.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Thread t1 = new Thread(this);
		t1.start();
		
		refreshOpponentHand();
//		addToCapturedCard("deck.png");//function test out
		//throwCard();//function test out
	}
	public void run(){
		while(true){

			lock.lock();
			this.repaint();
			this.validate();
			this.revalidate();
			this.repaint();
//			if(refreshFlag ==true){
//				refreshOpponnetHand();
//				refreshFlag=false;
//			}
			lock.unlock();
			
//			collectionPanel.repaint();
//			collectionPanel.revalidate();
						
		}
	}
	public static  void setScore(int score){
		scoreLabel.setText("Score: " + Integer.toString(score));
	}
	public static OpponentCollectionPanel returnOpponentCollectionPanel(){
		return opponentCollectionPanel;
	}
	public void incScore(int incBy){
		score = score + incBy;
		userScore.setText(Integer.toString(score) + " ");
	}
	public void discardCard(){
		cardLeft--;
		numCards.setText("Cards left: " + cardLeft + " ");
		cardPanel.removeAll();
		refreshOpponentHand();
	}
	
//	public void addToCapturedCard(String imagePath){
//		opponentCollectionPanel.insertIcon(new ImageIcon(imagePath));
//		
//	}
	
	
	public static void refreshOpponentHand(){
		cardPanel.removeAll();

		for(int i = 0; i < cardLeft; i++){
			cardPanel.add(new JLabel(new ImageIcon(cardFaceDown)));
		}
		numCards.setText("Cards left: " + cardLeft + " ");
		cardLeft--;
		
		refreshFlag=true;
	}
	
}

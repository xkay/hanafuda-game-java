package com.usc.hanafuda.entities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import com.usc.hanafuda.HClient;
import com.usc.hanafuda.MyGame;
import com.usc.hanafuda.handlers.MyAssetHandler;
import com.usc.hanafuda.screens.GameScreen;

public class HandPanel extends JPanel implements Runnable{
	static boolean aCardIsUp = false;
	static ArrayList<CardButton> cardButtonList;
	public final int gap = 100;
	private static int score = 0;
	private static JLabel playerScore;
	private static  CollectionPanel collectionPanel;
	private String myName;
	private String opponentName;
	private GameScreen gameScreen;
	static HClient hClient;
	
	private static int numMatchingCards =-1;
	private static Card currentSelectedHandCard =null;
	Lock lock = new ReentrantLock();
	private  static boolean refreshFlag = false;
	private static boolean removeAllCardButtons = false;
	private static boolean disableButtons = false;
	
	public HandPanel(HClient hClient, GameScreen gs){
		
		this.gameScreen = gs;
		this.setBackground(Color.yellow);
		this.myName = hClient.getUserName();
		this.hClient =  hClient;
		this.setPreferredSize(new Dimension (1150, 200));
		this.setMinimumSize(new Dimension (1150, 200));
		this.setMaximumSize(new Dimension (1150, 200));
		this.setLayout(null);
		this.setBorder(BorderFactory.createLineBorder(Color.darkGray, 3));
		
		
		JLabel playerName = new JLabel(myName);
		playerName.setBounds(860, 10, 200, 50);
		playerName.setFont(new Font("Monotype Corsiva", Font.PLAIN, 30));
		this.add(playerName);
		playerScore = new JLabel("Score: " + Integer.toString(score));
		playerScore.setBounds(860, 35, 250, 100);
		playerScore.setFont(new Font("Monotype Corsiva", Font.PLAIN, 30));
		this.add(playerScore);
		
		// collection panel
		JButton showCapturedBtn = new JButton("Show Captured Cards");
		showCapturedBtn.setBounds(860,110,160,40);
		this.add(showCapturedBtn);
		collectionPanel = new CollectionPanel();
		
		int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
	    int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
		final JScrollPane jsp = new JScrollPane(collectionPanel, v , h);
		
		showCapturedBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				JFrame popup = new JFrame("Your Collection Cards");
				popup.setDefaultCloseOperation(popup.DISPOSE_ON_CLOSE);
				popup.setSize(500,300);
				popup.setLocation(700,400);
				popup.setVisible(true);
				popup.setResizable(true);				
				popup.add(jsp);
			}
		});

		cardButtonList = new ArrayList<CardButton>();
		initialDeal();
		refreshDisplay();
		
		Thread t = new Thread (this); // added by X
		t.start();
		
	}
	public void run(){
		while(true){
//			System.out.println("panel thread running");
			validate();
			this.repaint();
			
			this.revalidate();
			this.repaint();
			
			lock.lock();

			if(removeAllCardButtons == true){
				
				removeAllCardButtons();
				removeAllCardButtons = false;
				
			}
			if(refreshFlag ==true){
				refreshDisplay();
				refreshFlag=false;
			}

			
			if(hClient.getMyTurn() && !removeAllCardButtons){
				for(int j=0;j<HandPanel.cardButtonList.size();j++){

					HandPanel.cardButtonList.get(j).setEnabled(true);
					this.repaint();
					this.validate();
				}
			}
			else if(!hClient.getMyTurn() && !removeAllCardButtons){
				for(int j=0;j<HandPanel.cardButtonList.size();j++){

					HandPanel.cardButtonList.get(j).setEnabled(false);
					this.repaint();
					this.validate();
				}
			}

			lock.unlock();
		
			
		}
	}
//	public static synchronized void setCardButtonStatus(boolean currentPlaying){
//		if(currentPlaying) disableButtons =false;
//		disableButtons = true;
//
//	}
	public static CollectionPanel returnCollectionPanel(){
		return collectionPanel;
	}
	public static void resetNumMatchingCards(){
		numMatchingCards =-1;
	}
	
	public static int returnNumMatchingCards(){
		return numMatchingCards;
	}
	
	public void initialDeal(){
		ArrayList<Card> hand = hClient.getHand();		
		for(int i = 0 ; i < hand.size(); i++){
			final CardButton cb = new CardButton();			
			cb.setCardImage(hand.get(i));			
			cb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent aa) {
					for(int j=0;j<HandPanel.cardButtonList.size();j++){
						HandPanel.cardButtonList.get(j).moveDown();
					}
					refreshDisplay();
					Card c = ((CardButton) aa.getSource()).returnCard();

						highlightMatchingCards(c);
						currentSelectedHandCard =c;
						
						cb.moveUp();		
						refreshDisplay();

				}
			});			
			cardButtonList.add(cb);	

		}
	}

	
	public synchronized static void refreshHand(){
		System.out.println("refreshing hand");
		ArrayList<Card> hand = hClient.getHand();
		
		removeAllCardButtons = true;
		while (removeAllCardButtons==true){
			System.out.println("waiting for hand card buttons to be removed");
		}
		
		cardButtonList.clear();
		System.out.println("cleared hand card Buttons");
		for(int i = 0 ; i < hand.size(); i++){
			final CardButton cb = new CardButton();			
			cb.setCardImage(hand.get(i));			
			cb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent aa) {
					for(int j=0;j<HandPanel.cardButtonList.size();j++){
						HandPanel.cardButtonList.get(j).moveDown();
					}
					
					Card c = ((CardButton) aa.getSource()).returnCard();

						highlightMatchingCards(c);
						currentSelectedHandCard =c;						
						cb.moveUp();
						
						
				}
			});			
			cardButtonList.add(cb);	

		}
		refreshFlag = true;
		

	}
	public static synchronized Card returnCurrentSelectedHandCard(){
		return currentSelectedHandCard;
	}
	
	public static void highlightMatchingCards(Card c){
		unhighlightAllCards();
		
		
		ArrayList<Card> matchingCards = hClient.getMatchingCards(c);
		System.out.println("Matching Card size: " +matchingCards.size());
		numMatchingCards = matchingCards.size();
		for(int i = 0 ; i <matchingCards.size(); i++){
			for(int j=0;j<FieldPanel.cardButtonList.size();j++){
				if((matchingCards.get(i)).isMatch((FieldPanel.cardButtonList.get(j)).returnCard())){
//					System.out.println("Matching card Number : "+ ((FieldPanel.cardButtonList.get(i)).getCard()).getId());
					FieldPanel.cardButtonList.get(j).setGlow();
					FieldPanel.cardButtonList.get(j).repaint();
					
				}
			}


		}
		
	}
	public static void unhighlightAllCards(){
			for(int j=0;j<FieldPanel.cardButtonList.size();j++){
				FieldPanel.cardButtonList.get(j).unsetGlow();
				FieldPanel.cardButtonList.get(j).repaint();
			}
	}

	public static void setScore(int i){
		score = i;
		playerScore.setText("Score: " + Integer.toString(score));
	}
	
	public synchronized void removeAllCardButtons(){
		System.out.println("removing all card buttons");
		for(int i = 0 ; i < HandPanel.cardButtonList.size(); i++){
			//System.out.println("card " +  40+i*60 );
			this.remove(HandPanel.cardButtonList.get(i));
			//cardButtonList.get(i).setLocation(40+i*60 ,40);	
		}
		this.revalidate();
	}
	
	public synchronized void refreshDisplay(){
		
		for(int i = 0 ; i < cardButtonList.size(); i++){
			//System.out.println("card " +  40+i*60 );
			this.add(cardButtonList.get(i));
			//cardButtonList.get(i).setLocation(40+i*60 ,40);
			cardButtonList.get(i).setBounds(40+i*gap, cardButtonList.get(i).getNewX(), MyAssetHandler.WIDTH, MyAssetHandler.HEIGHT );		
		}
		//cardButtonList.get(0).setLocation(40, 40);
		this.revalidate();
	}
	
	
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//g.drawString("hello", 10, 50);
//		g.drawImage(MyAssetHandler.cardImageArray[0], 40, 40, null);
//		g.drawImage(MyAssetHandler.cardImageArray[1], 400, 40, null);
//		g.drawImage(MyAssetHandler.cardImageArray[2], 800, 40, null);

	}	
//	public void addToCapturedCard(String imagePath){
//		collectionPanel.insertIcon(new ImageIcon(imagePath));
//		
//	}
	public void incScore(int incBy){
		score = score + incBy;
		playerScore.setText(Integer.toString(score) + " ");
	}
		
}

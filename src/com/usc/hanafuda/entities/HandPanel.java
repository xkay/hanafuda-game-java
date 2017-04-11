package com.usc.hanafuda.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import com.usc.hanafuda.HClient;
import com.usc.hanafuda.handlers.MyAssetHandler;
import com.usc.hanafuda.screens.GameScreen;

public class HandPanel extends JPanel {
	HClient hClient;
	private GameScreen gameScreen;
	private  CollectionPanel collectionPanel;

	ArrayList<CardButton> cardButtonList = new ArrayList<CardButton>();
	private Card currentSelectedHandCard = null;

	private int score = 0;
	private JLabel playerScore;
	private String myName;

	boolean aCardIsUp = false;

	Lock lock = new ReentrantLock();

	public final int gap = 100;


	public HandPanel (HClient hClient, GameScreen gs) {
		this.hClient =  hClient;
		this.gameScreen = gs;
		this.myName = hClient.getUserName();

		// Set up panel
		this.setBackground (Color.yellow);
		this.setPreferredSize (new Dimension (1150, 200));
		this.setMinimumSize (new Dimension (1150, 200));
		this.setMaximumSize (new Dimension (1150, 200));
		this.setLayout (null);
		this.setBorder (BorderFactory.createLineBorder (Color.darkGray, 3));

		// Set up player's name which is displayed next to player's hand
		JLabel playerName = new JLabel (myName);
		playerName.setBounds (860, 10, 200, 50);
		playerName.setFont (new Font ("Arial", Font.PLAIN, 30));
		this.add (playerName);

		// Set up player's score which is displayed next to player's hand
		playerScore = new JLabel ("Score: " + Integer.toString(score));
		playerScore.setBounds (860, 35, 250, 100);
		playerScore.setFont (new Font ("Arial", Font.PLAIN, 30));
		this.add (playerScore);

		// Set up collection panel and button
		JButton showCapturedBtn = new JButton ("Show Captured Cards");
		showCapturedBtn.setBounds (860,110,160,40);
		this.add (showCapturedBtn);
		collectionPanel = new CollectionPanel (gs);

		int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
	    int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
		final JScrollPane jsp = new JScrollPane (collectionPanel, v , h);

		showCapturedBtn.addActionListener (new ActionListener() {
			public void actionPerformed (ActionEvent ae) {
				JFrame popup = new JFrame ("Your Collection Cards");
				popup.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
				popup.setSize (500,300);
				popup.setLocation (700,400);
				popup.setVisible (true);
				popup.setResizable (true);
				popup.add (jsp);
			}
		});

		initialDeal();
		refreshDisplay();
	}


	public synchronized void enableAllCards() {
		//DEBUG
		//System.out.println ("Entering enableAllCards()");

		for (int i = 0; i < cardButtonList.size(); i++) {
			cardButtonList.get(i).setEnabled(true);

		}

		this.repaint();
		this.validate();

		//DEBUG
		//System.out.println ("Finished enabling all cards.");
	}

	public synchronized void disableAllCards() {
		for (int i = 0; i < cardButtonList.size(); i++) {
			cardButtonList.get(i).setEnabled(false);
		}

		this.repaint();
		this.validate();
	}

	public CollectionPanel returnCollectionPanel() {
		return collectionPanel;
	}

	public void resetSelectedHandCard() {
		currentSelectedHandCard = null;
	}

	public void initialDeal() {
		//DEBUG
		//System.out.println ("HandPanel: Entering initialDeal()");

		ArrayList<Card> hand = hClient.getHand();

		for (int i = 0 ; i < hand.size(); i++) {
			final CardButton cb = new CardButton();
			cb.setCardImage (hand.get (i));
			cb.addActionListener (new HandCardListener (cb));
			cardButtonList.add (cb);
		}

		if (!hClient.isHost()) {
			disableAllCards();
		}
	}

	public synchronized void refreshHand() {
		//DEBUG
		//System.out.println ("HandPanel: Entering refreshHand()");

		ArrayList<Card> hand = hClient.getHand();

		removeAllCardButtons();
		cardButtonList.clear();

		for (int i = 0 ; i < hand.size(); i++) {
			final CardButton cb = new CardButton();
			cb.setCardImage (hand.get (i));
			cb.addActionListener (new HandCardListener (cb));
			cardButtonList.add (cb);
		}

		refreshDisplay();
	}

	public synchronized Card returnCurrentSelectedHandCard() {
		return currentSelectedHandCard;
	}

	public void highlightMatchingCards (Card c) {
		//DEBUG
		//System.out.println ("HandPanel: highlightMatchingCards()");

		unhighlightAllCards();

		ArrayList<Card> matchingCards = hClient.getMatchingCards (c);
		ArrayList<CardButton> cbList = gameScreen.getFieldPanel().getCardButtonList();

		// Set glow and repaint cards in the field that match c
		for (int i = 0 ; i < matchingCards.size(); i++) {
			for (int j = 0; j < cbList.size(); j++) {
				if ((matchingCards.get(i)).isMatch((cbList.get(j)).returnCard())) {
					cbList.get(j).setGlow();
					cbList.get(j).repaint();
				}
			}
		}
	}

	public void unhighlightAllCards() {
		//DEBUG
		//System.out.println ("HandPanel: Entering unhighlightAllCards()");

		ArrayList<CardButton> cbList = gameScreen.getFieldPanel().getCardButtonList();

		for (int j = 0; j < cbList.size(); j++) {
			cbList.get(j).unsetGlow();
			cbList.get(j).repaint();
		}
	}

	public void setScore (int s) {
		//DEBUG
		//System.out.println ("HandPanel: Entering setScore()");

		score = s;
		playerScore.setText ("Score: " + Integer.toString (score));

	}

	public synchronized void removeAllCardButtons() {
		//DEBUG
		//System.out.println ("HandPanel: removeAllCardButtons()");

		for (int i = 0 ; i < cardButtonList.size(); i++) {
			this.remove (cardButtonList.get(i));
		}

		this.revalidate();
	}

	public synchronized void refreshDisplay() {
		//DEBUG
		//System.out.println ("HandPanel: Entering refreshDisplay()");

		for (int i = 0 ; i < cardButtonList.size(); i++) {
			this.add (cardButtonList.get(i));
			cardButtonList.get(i).setBounds (40+i*gap, cardButtonList.get(i).getNewX(), MyAssetHandler.WIDTH, MyAssetHandler.HEIGHT);
		}

		this.revalidate();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public void incScore (int incBy) {
		//System.out.println ("HandPanel: Entering incScore()");

		score = score + incBy;
		playerScore.setText ("Score: " + Integer.toString (score));
	}

	public class HandCardListener implements ActionListener {
		CardButton cb;

		public HandCardListener (CardButton button) {
			cb = button;
		}

		public void actionPerformed (ActionEvent ae) {
			for (int j = 0; j < cardButtonList.size(); j++) {
				cardButtonList.get(j).moveDown();
			}

			gameScreen.getFieldPanel().resetSelectedFieldCard();

			refreshDisplay();

			Card c = ((CardButton) ae.getSource()).returnCard();

			highlightMatchingCards (c);
			currentSelectedHandCard = c;

			cb.moveUp();
			refreshDisplay();

		}
	}
}

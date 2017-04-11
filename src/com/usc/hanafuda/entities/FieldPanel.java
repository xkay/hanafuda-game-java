package com.usc.hanafuda.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.usc.hanafuda.HClient;
import com.usc.hanafuda.handlers.MyAssetHandler;
import com.usc.hanafuda.screens.GameScreen;

public class FieldPanel extends JPanel {
	HClient hClient;
	private GameScreen gameScreen;
	
	ArrayList<CardButton> cardButtonList = new ArrayList<CardButton>();
	private Card selectedFieldCard = null;
	private CardButton deckButton;
	private ImageIcon deckImage = new ImageIcon ("deck.png");
	
	private int numDeckClicked = 0;
	boolean aCardIsUp = false;
		
	public final int gap = 100;
	
	public FieldPanel (HClient hClient, GameScreen gs) {
		this.gameScreen = gs;
		this.hClient =  hClient;
		
		// Set up panel
		this.setBackground (Color.GRAY);
		this.setPreferredSize (new Dimension (1150, 200));
		this.setMinimumSize (new Dimension (1150, 200));
		this.setMaximumSize (new Dimension (1150, 200));
		this.setLayout (null);
		this.setBorder (BorderFactory.createLineBorder(Color.darkGray, 3));

		// Initialize deck button
		deckButton = new CardButton (deckImage);
		deckButton.setBounds(40, 40, MyAssetHandler.WIDTH, MyAssetHandler.HEIGHT);
		deckButton.addActionListener (new DeckButtonListener (deckButton));
		
		initialDeal();
		refreshDisplay();
		
	}

	
	public Card returnSelectedFieldCard() {
		return selectedFieldCard;
		
	}
	
	
	public void resetSelectedFieldCard() {
		selectedFieldCard = null;
		
	}
	
	
	public synchronized ArrayList<CardButton> getCardButtonList() {
		return cardButtonList;
		
	}

	
	public synchronized void setDeckImage (Card c) {
		deckButton.setCardImage (c);	
		deckButton.repaint();
		deckButton.revalidate();
		
	}
	
	
	public synchronized void resetDeckImage() {
		deckButton.setIcon (deckImage);
		deckButton.repaint();
		deckButton.revalidate();
		
	}
	
	
	public void initialDeal() {
		ArrayList<Card> field = hClient.getField();
		
		// Set up deck button
		deckButton = new CardButton (deckImage);
		deckButton.setBounds (40, 40, MyAssetHandler.WIDTH, MyAssetHandler.HEIGHT);
		deckButton.addActionListener (new DeckButtonListener (deckButton));
		this.add (deckButton);
		
		// Create card buttons for all cards in the field
		for (int i = 0 ; i < field.size(); i++) {
			final CardButton cb = new CardButton();
			
			cb.setCardImage (field.get(i));		
			cb.addActionListener (new CardButtonListener (cb));
			
			cardButtonList.add(cb);
			
		}
	}
	
	
	public synchronized void removeAllCardButtons() {
		//DEBUG
		//System.out.println ("FieldPanel: Entering removeAllCardButtons()");
		
		for (int i = 0 ; i < cardButtonList.size(); i++) {
			this.remove (cardButtonList.get(i));
			
		}
		
		this.repaint();		
		this.revalidate();
		
	}
	
	
	public synchronized void refreshField() {
		//DEBUG
		//System.out.println("FieldPanel: Entering refreshField()");
		
		ArrayList<Card> field = hClient.getField();
		
		// Remove buttons from panel and clear button list
		removeAllCardButtons();
		cardButtonList.clear();
		
		// Add buttons using new field
		for (int i = 0 ; i < field.size(); i++) {
			final CardButton cb = new CardButton();
			
			cb.setCardImage (field.get(i));		
			cb.addActionListener (new CardButtonListener (cb));				
			cardButtonList.add(cb);
		
		}
		
		refreshDisplay();
		
	}
	
	
	public synchronized void refreshDisplay() {
		//System.out.println ("FieldPanel: Entering refreshDisplay()");
		
		for (int i = 0 ; i < cardButtonList.size(); i++) {
			this.add (cardButtonList.get(i));
			cardButtonList.get(i).setBounds (140+i*gap, cardButtonList.get(i).getNewX(), MyAssetHandler.WIDTH, MyAssetHandler.HEIGHT);
			
		}

		this.validate();
		
	}
	
	
	public synchronized void resetNumDeckClicked() {
		numDeckClicked = 0;
	}
	

	protected void paintComponent (Graphics g) {
		super.paintComponent (g);

	}
	
	
	public class CardButtonListener implements ActionListener {
		private CardButton cb;
		
		public CardButtonListener (CardButton button) {
			cb = button;
			
		}
		
		public void actionPerformed (ActionEvent ae) {
			Card c = ((CardButton) ae.getSource()).returnCard();
			
			// If the card button is clicked and it is highlighted, then the card is a valid selection
			if (cb.isGlowSet()) {
				selectedFieldCard = c;
				
				HandPanel hp = gameScreen.getHandPanel();
				
				// Process player's choice
				if (hp.returnCurrentSelectedHandCard() != null) {
					Card mySelectedHand = hp.returnCurrentSelectedHandCard();
					
					hClient.sendSelectedCard (mySelectedHand);
					hClient.processMatchAndRemoveCards (mySelectedHand, selectedFieldCard);
					//hClient.getCardFromDeck();
					
				}
				else {
					hClient.processMatchAndRemoveCards (hClient.getReceivedDeckCard(), selectedFieldCard);
					hClient.resetReceivedDeckCard();
					resetDeckImage();
					
				}
				
				resetSelectedFieldCard();
				hp.resetSelectedHandCard();
				hp.disableAllCards();
				
			}
			
			else {
				selectedFieldCard = null;
			}
		}
	}
	
	
	public class DeckButtonListener implements ActionListener {
		private CardButton cb;
		
		public DeckButtonListener (CardButton button) {
			cb = button;
		}
		
		public void actionPerformed (ActionEvent e) {
			//System.out.println ("Deck has been clicked.");
			numDeckClicked++;
			
			HandPanel hp = gameScreen.getHandPanel();
			
			if (numDeckClicked == 1) {
				if ((selectedFieldCard == null) && (hp.returnCurrentSelectedHandCard() != null)) {
					// There were no matches in the field; add selectedHandCard to field
					Card mySelectedHand = hp.returnCurrentSelectedHandCard();
					
					hClient.sendSelectedCard (mySelectedHand);
					hClient.addHandCardToField (mySelectedHand);
					hp.resetSelectedHandCard();
					hp.disableAllCards();
					
				}
				
				hClient.getCardFromDeck();
				
			}			
		}
	}


}

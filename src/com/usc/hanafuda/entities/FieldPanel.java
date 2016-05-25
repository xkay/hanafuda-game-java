package com.usc.hanafuda.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.usc.hanafuda.HClient;
import com.usc.hanafuda.handlers.MyAssetHandler;
import com.usc.hanafuda.screens.GameScreen;

public class FieldPanel extends JPanel implements Runnable{

	
		static boolean aCardIsUp = false;
		static ArrayList<CardButton> cardButtonList;
		public final int gap = 100;
		private int score = 0;
		private GameScreen gameScreen;
		static HClient hClient;
		
		private static CardButton deckButton;
		
		private static Card selectedFieldCard = null; // add by X
		private  static boolean refreshFlag = false;
		private static boolean removeAllCardButtons = false;
		Lock lock = new ReentrantLock();
		private boolean firstRefresh = false;
		
		public FieldPanel(HClient hClient, GameScreen gs){
			this.setBackground(Color.GRAY);
			this.gameScreen = gs;
			this.hClient =  hClient;
			this.setPreferredSize(new Dimension (1150, 200));
			this.setMinimumSize(new Dimension (1150, 200));
			this.setMaximumSize(new Dimension (1150, 200));
			this.setLayout(null);
			this.setBorder(BorderFactory.createLineBorder(Color.darkGray, 3));
			cardButtonList = new ArrayList<CardButton>();
			initialDeal();
			refreshDisplay();
			Thread t = new Thread (this); //added by x
			t.start();

			
		}
		
		public void run(){
			while(true){

				validate();
				repaint();
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
				validate();
				repaint();
				this.revalidate();
				this.repaint();

				
				lock.unlock();
				
			}
		}

		public static Card returnSelectedFieldCard(){
			return selectedFieldCard;
		}
		public static void resetSelectedFieldCard(){
			selectedFieldCard = null;
		}

		
		public static synchronized void setDeckImage(Card c){
			deckButton.setCardImage(c);	
//			deckButton.setIcon(deckImage);
			deckButton.repaint();
			deckButton.revalidate();
		}
		public static synchronized void resetDeckImage(){
			ImageIcon deckImage = new ImageIcon("deck.png");
			deckButton.setIcon(deckImage);
			deckButton.repaint();
			deckButton.revalidate();
		}
		public void initialDeal(){
			ArrayList<Card> field = hClient.getField();
			ImageIcon deckImage = new ImageIcon("deck.png");
			deckButton = new CardButton(deckImage);
			deckButton.setBounds(40, 40, MyAssetHandler.WIDTH, MyAssetHandler.HEIGHT);
			
			deckButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					hClient.setDeckButtonStatus(true);
				}
				
			});
//			deck.setEnabled(false);
			this.add(deckButton);
			
			for(int i = 0 ; i < field.size(); i++){
				final CardButton cb = new CardButton();
				
				cb.setCardImage(field.get(i));				
				cb.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent aa) {
						Card c = ((CardButton) aa.getSource()).returnCard();
						if(cb.isGlowSet())
							selectedFieldCard =c;
						else 
							selectedFieldCard = null;

//						if(!cb.isGlowSet()) {
//							cb.setGlow();
//							cb.repaint();
//						}
//						
//						else{
//							cb.unsetGlow();
//							cb.repaint();
//						}
//						refreshDisplay();
					}
				});
				
				cardButtonList.add(cb);
				
			}
		}
		
		public synchronized void removeAllCardButtons(){
			System.out.println("removing all field card buttons");
			for(int i = 0 ; i < cardButtonList.size(); i++){
				//System.out.println("card " +  40+i*60 );
				this.remove(cardButtonList.get(i));
				//cardButtonList.get(i).setLocation(40+i*60 ,40);	
			}
			this.revalidate();
		}
		
		public static synchronized void refreshField(){
			System.out.println("refreshing field");
			ImageIcon deckImage = new ImageIcon("deck.png");
			CardButton deck = new CardButton(deckImage);
			deck.setBounds(40, 40, MyAssetHandler.WIDTH, MyAssetHandler.HEIGHT);
			
			deck.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					hClient.setDeckButtonStatus(true);					
				}
				
			});
			ArrayList<Card> field = hClient.getField();
			removeAllCardButtons = true;
			while (removeAllCardButtons==true){
				System.out.println("waiting for field card buttons to be removed");
			}
			
			cardButtonList.clear();
			System.out.println("cleared field card Buttons");
			
			for(int i = 0 ; i < field.size(); i++){
				final CardButton cb = new CardButton();
				
				cb.setCardImage(field.get(i));				
				cb.addActionListener(new ActionListener() {					
					public void actionPerformed(ActionEvent aa) {
						Card c = ((CardButton) aa.getSource()).returnCard();
						if(cb.isGlowSet())selectedFieldCard =c;
						else selectedFieldCard = null;
					}
				});				
				cardButtonList.add(cb);
			
			}
			refreshFlag = true;
		}
		
		public static synchronized void refreshField(ArrayList<Card> Field ){
			System.out.println("refreshing field");
			ImageIcon deckImage = new ImageIcon("deck.png");
			CardButton deck = new CardButton(deckImage);
			deck.setBounds(40, 40, MyAssetHandler.WIDTH, MyAssetHandler.HEIGHT);
			
			deck.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					hClient.setDeckButtonStatus(true);					
				}
				
			});
			ArrayList<Card> field = Field;
			removeAllCardButtons = true;
			while (removeAllCardButtons==true){
				System.out.println("waiting for field card buttons to be removed");
			}
			
			cardButtonList.clear();
			System.out.println("cleared field card Buttons");
			
			for(int i = 0 ; i < field.size(); i++){
				final CardButton cb = new CardButton();
				
				cb.setCardImage(field.get(i));				
				cb.addActionListener(new ActionListener() {					
					public void actionPerformed(ActionEvent aa) {
						Card c = ((CardButton) aa.getSource()).returnCard();
						if(cb.isGlowSet())selectedFieldCard =c;
						else selectedFieldCard = null;
					}
				});				
				cardButtonList.add(cb);
			
			}
			refreshFlag = true;
		}
		
		
		public synchronized void refreshDisplay(){
			
			for(int i = 0 ; i < cardButtonList.size(); i++){
				//System.out.println("card " +  40+i*60 );
				this.add(cardButtonList.get(i));
				//cardButtonList.get(i).setLocation(40+i*60 ,40);
				cardButtonList.get(i).setBounds(140+i*gap, cardButtonList.get(i).getNewX(), MyAssetHandler.WIDTH, MyAssetHandler.HEIGHT );				
			}
			//cardButtonList.get(0).setLocation(40, 40);
			this.validate();
		}
		

		
		
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

		}	
			
	

}

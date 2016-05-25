package com.usc.hanafuda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import com.usc.hanafuda.entities.Card;
import com.usc.hanafuda.entities.Card.Yaku;
import com.usc.hanafuda.entities.FieldPanel;
import com.usc.hanafuda.entities.HandPanel;
import com.usc.hanafuda.entities.OpponentCollectionPanel;
import com.usc.hanafuda.entities.OpponentPanel;
import com.usc.hanafuda.handlers.MyAssetHandler;

//TODO: How do we notify the GUI of when to change things?
		// For example, when the server sends a card
		// Maybe use enum to represent state
		// Change the state depending on what we want it to do


//TODO: Store GUI inside client 

public class HClient extends Thread {
	//private PrintWriter pw;
	//private BufferedReader br;
	private ObjectOutputStream os;
	private ObjectInputStream is;
	private ArrayList<Card> Hand = new ArrayList<Card>();
	private ArrayList<Card> Field = new ArrayList<Card>();
	private ArrayList<Card> Collection = new ArrayList<Card>();
	private ArrayList<Card> OpponentCollection = new ArrayList<Card>();
	private boolean Host = false;
	private boolean MyTurn = false;
	private int score=0;
	private int AnotherScore=0;
	private Card anotherSelectedCard;
	private Card receivedDeckCard;
	private Socket s;
	private String userName;
	private Scanner scan;
	private boolean hostSet = false;
	private boolean deckButtonClicked = false; // added by X
	private String hostName, clientName;
	private int turn =0;
	
	
	public HClient (String hostname, int port, String userName) {	
		
		try {
			//DEBUG
			this.scan = new Scanner (System.in);
			
			this.userName = userName;
			Socket s = new Socket (hostname, port);
			//this.pw = new PrintWriter (s.getOutputStream());
			this.os = new ObjectOutputStream (s.getOutputStream());
			//this.br = new BufferedReader (new InputStreamReader(s.getInputStream()));
			this.is = new ObjectInputStream (s.getInputStream());
			this.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	} // End of constructor
	
	public boolean getMyTurn(){
		return MyTurn;
	}
	
	public void setDeckButtonStatus(boolean b){ // added by X
		deckButtonClicked = b;
	}


	public String getUserName() {
		return userName;
		
	}
	
	
	public ArrayList<Card> getHand(){
		return Hand;
		
	}
	public ArrayList<Card> getCollection(){ // newly added
		return Collection;		
	}
	public ArrayList<Card> getOpponentCollection(){ // newly added
		return OpponentCollection;		
	}
	
	
	public void sendScore() {
		sendMessage ("Signal:SendScore");
		
		// Add an h to the front of Host's score to differentiate between them
		if (Host) {
			sendMessage ("h"+Integer.toString(score));
		}
		else {
			sendMessage (Integer.toString(score));
		}
	}
	
	
	public void sendFinalScore() {
		sendMessage ("Signal:SendFinalScore");
		
		// Add an h to the front of Host's score to differentiate between them
		if (Host) {
			sendMessage ("h"+Integer.toString(score));
		}
		else {
			sendMessage (Integer.toString(score));
		}
	}
	
	
	public void updateScore() {
		int tempScore = 0;

		for (int i = 0; i <Collection.size(); i++) {
			tempScore += Collection.get(i).getValue();
		}
		
		score = tempScore;
		
	}
	
	
	public void sendMessage (String msg) {
		try {
			os.writeObject (msg);
			os.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void sendCardToServer (Card cd) {
		try {
			// Inform server that client will be sending card
			sendMessage ("Signal:SendCard");
			
			// Make sure card and message are not sent at the same time
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Send the card
			os.reset();
			os.writeObject (cd);
			os.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}		
	
	// This method checks for matches with the given card and all cards from the field 
	public ArrayList<Card> getMatchingCards (Card currentCard) { 
		ArrayList<Card> potentialMatch = new ArrayList<Card>();
		
		if (currentCard!=null) {
			// Iterate through field
			for (int i = 0; i < Field.size(); i++) {
				// Check if card from the field is a match
				
				// DEBUG
				//System.out.println("Match "+currentCard.getName()+" with "+Field.get(i).getName());
				
				if (currentCard.isMatch (Field.get(i))) {
					potentialMatch.add (Field.get(i));
				}
			}
		}
		
		return potentialMatch;
	}
	
	
	// This method is called when a player wants to match a card from the hand with one from the field
	// This can also be called for when you want to match a card drawn from the deck with one from the field
	// They will collect the card from the field; discard any cards whose value is 0
	public synchronized void processMatchAndRemoveCards (Card cardFromHand, Card cardFromField) {
		System.out.println ("In process match and remove cards");
		// Add card to collection if value > 0 or it is a gaji
		if (cardFromHand.getValue() > 0 || cardFromHand.isGaji()) {
			if (cardFromHand.isGaji()) {
				cardFromHand.setGajiMonth (cardFromField.getMonth());
				
			}
			
			Collection.add(cardFromHand);

			//TODO: Update Collection Panel

		}
		
		// Add card to collection if value > 0
		if (cardFromField.getValue() > 0) {		
			Collection.add (cardFromField);
			
		}
		
		//DEBUG
//		for (int i = 0; i < Collection.size(); i++) {			
//			System.out.println ("Process: " + Collection.get(i).getName());
//			
//		}
		
		// Remove matched card from hand
		for (int i = 0; i < Hand.size(); i++) {			
			if (Hand.get(i).equals(cardFromHand)) {
				Hand.remove (Hand.get(i));
				
			}
		}
		
		// Remove matched card from field
		for (int i = 0; i < Field.size(); i++) {			
			if (Field.get(i).equals(cardFromField)) {
				Field.remove (Field.get(i));
				
			}
		}
		
		HandPanel.refreshHand(); // added by X refresh after field and hand and collection updated
		FieldPanel.refreshField();
		(HandPanel.returnCollectionPanel()).updateCollection();
		// Send new card and field to server
			
		updateScore();	
		sendScore();		
		sendField();	
		sendCollection();		
		
	}
	
	
	// Sends the card selected from hand to server
	public void sendSelectedCard (Card cd) {		
		sendMessage ("Signal:SendSelectedCard");
		
		sendCardToServer (cd);
				
	}
	
	
	// Call on the end of the player's turn
	public void endTurn(){
		MyTurn = false;
		
		sendMessage("Signal:EndTurn");
		
	}
	
	
	// Call when you want to add the card drawn from the deck to the field
	// This is done when there is no match
	public void addDrawnCardToField (Card cd) {
		Field.add(cd);
		FieldPanel.refreshField();
		sendField();
		sendMessage ("Signal:UpdateFieldFinished");
		
	}
	
	
	// Call when you want to add a card from the hand to the field
	// This happens when there is no match in the player's hand
	public void addHandCardToField (Card cd) {
		Field.add(cd);
		
		for (int i = 0; i < Hand.size(); i++){
			if (Hand.get(i).equals(cd))
				Hand.remove(cd);
			
		}
		
		sendField();
		FieldPanel.refreshField(); // added to refresh GUI
		HandPanel.refreshHand();
		sendMessage ("Signal:UpdateFieldFinished");
		
	}
	
	
	// Requests a card from the deck from the server
	public void getCardFromDeck() {		
		sendMessage ("Signal:GetCardFromDeck");
		
	}
	
	
	public ArrayList<Card> getField() {	
		return Field;
		
	}
	
	
	public synchronized void sendField () {
		sendMessage ("Signal:UpdateField");
		
		for (int i = 0; i < Field.size(); i++) {
			//DEBUG
			System.out.println ("Send field card " + i);
			
			sendMessage ("Signal:SendField");
			sendCardToServer (Field.get (i));
			
		}
		
		
	}
	
	
	public synchronized void sendCollection () {
		sendMessage ("Signal:UpdateCollection");
		
		for (int i=0; i < Collection.size(); i++) {
			//DEBUG
			System.out.println ("Sending card " + i + " of collection");
			
			sendMessage ("Signal:SendCollection");
			sendCardToServer (Collection.get (i));
			
		}
		
		sendMessage ("Signal:UpdateCollectionFinished");

	}
	
	
	public void waitForResponse() {
		try {
			this.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public int calculateFinalScore () {
		int finalScore = 0;
		
		// Check for gaji in collection
		for (Card cc : Collection) {
			if (cc.isGaji()) {
				// Check if field has any cards from gaji month
				for (Card fc : Field) {
					//TODO: May want to show cards being added to hand
					if (fc.getMonth() == cc.getGajiMonth()) {
						Collection.add(fc);
					}
				}
			}
		}
		
		// Count value of all cards in collection
		for (Card cd : Collection) {
			finalScore += cd.getValue();
		}
		
		// Check for yakus
		int I = 0;
		int Ro = 0;
		int Ha = 0;
		int Ni = 0;
		int Ho = 0;
		int He = 0;
		int To = 0;
		int Chi = 0;
		
		for (Card cd : Collection) {
			if (cd.getYaku1() != null && cd.getYaku1() == Yaku.I) {
				I++;
			}
			if (cd.getYaku2() != null && cd.getYaku2() == Yaku.I) {
				I++;
			}
			if (cd.getYaku1() != null && cd.getYaku1() == Yaku.Ro) {
				Ro++;
			}
			if (cd.getYaku2() != null && cd.getYaku2() == Yaku.Ro) {
				Ro++;
			}
			if (cd.getYaku1() != null && cd.getYaku1() == Yaku.Ha) {
				Ha++;
			}
			if (cd.getYaku2() != null && cd.getYaku2() == Yaku.Ha) {
				Ha++;
			}
			if (cd.getYaku1() != null && cd.getYaku1() == Yaku.Ni) {
				Ni++;
			}
			if (cd.getYaku2() != null && cd.getYaku2() == Yaku.Ni) {
				Ni++;
			}
			if (cd.getYaku1() != null && cd.getYaku1() == Yaku.Ho) {
				Ho++;
			}
			if (cd.getYaku2() != null && cd.getYaku2() == Yaku.Ho) {
				Ho++;
			}
			if (cd.getYaku1() != null && cd.getYaku1() == Yaku.To) {
				To++;
			}
			if (cd.getYaku2() != null && cd.getYaku2() == Yaku.To) {
				To++;
			}
			if (cd.getYaku1() != null && cd.getYaku1() == Yaku.Chi) {
				Chi++;
			}
			if (cd.getYaku2() != null && cd.getYaku2() == Yaku.Chi) {
				Chi++;
			}
		}
		
		if (I == 3) {
			finalScore += 50;
		}
		if (Ro == 3) {
			finalScore += 50;
		}
		if (Ha == 3) {
			finalScore += 50;
		}
		if (Ni == 3) {
			finalScore += 50;
		}
		if (Ho == 3) {
			finalScore += 50;
		}
		if (He == 3) {
			finalScore += 50;
		}
		if (To == 3) {
			finalScore += 50;
		}
		if (Chi == 3) {
			finalScore += 50;
		}
		
		return finalScore;
	}
	
	
	public void run() {
		try {	
			while (true) {
				//DEBUG
				//System.out.println(1);
				
				String line = (String) is.readObject();
				
				//DEBUG
				//System.out.println(2);
				System.out.println ("Client: Received message from Server: "+ line);
				
				// Host has the "start game" button while others have "prepared" button
				
				if (line.equals ("Signal:Host")) {
					Host = true;
					
				}
				if(!Host && !hostSet){
					this.userName = userName + "(Client)";
					System.out.println("I am Client");
					hostSet = true;
				}
				else if(Host & !hostSet){
					this.userName = userName + "(Host)";
					//HServer.hostExist = true;
					System.out.println("I am Host");
					hostSet = true;
				}
				
				// Receive hand
				if (line.equals ("Signal:SendHand")) {
					// Read one more line message to confirm the receiving card process
					String nextMessage = (String) is.readObject();
						
					if (nextMessage.equals ("Signal:SendCard")) {
						try {
							Card received = (Card) is.readObject();
							
							// Add to hand
							Hand.add(received);
							
							//DEBUG
							System.out.println ("I am the host: " + Host);
							System.out.println ("Hand Card received: " + received.getName());
							System.out.println ("I have <" + Hand.size() + "> cards in Hand.");
						} catch (ClassNotFoundException e) {
							System.out.println(e.getMessage());
							e.printStackTrace();
						}
					}
						
				} // End of receive hand block
				
				
				// Receive field
				if (line.equals ("Signal:SendField")) {
					// Read one more line message to confirm the receiving card process
					String nextMessage = (String) is.readObject();
					
					if (nextMessage.equals ("Signal:SendCard")) {
						try {
							Card received = (Card) is.readObject();
							
							// Add to field
							Field.add(received);
						
							//DEBUG
							System.out.println ("Field Card received: " + received.getName());
							System.out.println ("I have <" + Field.size() + "> cards in Field");
						
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				} // End of receive field block
				

				// Opponent's collection must be updated				
				if (line.equals ("Signal:SendOpponentCollection")) {
					String nextMessage = (String) is.readObject();
					
					if (nextMessage.equals ("Signal:SendCard")) {		
						try {
							Card received = (Card) is.readObject();
							
							OpponentCollection.add(received);
							
							//DEBUG
							System.out.println ("Collection Card received: " + received.getName());
							
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
				
				
				// Field is about to be updated; prepare by clearing
				if (line.equals ("Signal:UpdateField")) {
					Field.clear();
					
				}
				
				
				// Opponent collection is about to be updated; prepare by clearing
				if (line.equals ("Signal:UpdateOpponentCollection")) {
					OpponentCollection.clear();
					
				}
				
				
				// Receive signal that it is this client's turn
				if (line.equals ("Signal:Turn")) {
						MyTurn = true;
						
						//DEBUG
						System.out.println ("My turn");
						System.out.println ("My Hand cards now:");
						for (int i = 0; i < Hand.size(); i++) {
							System.out.println ("<" + i + ">" + Hand.get(i).getName());		
						}
						System.out.println();
						System.out.println ("Field cards now: ");
						for (int i = 0; i < Field.size(); i++) {
							System.out.println ("<" + i + ">" + Field.get(i).getName());		
						}
						if(this.turn>=1){
							FieldPanel.refreshField(Field);
						}
						this.turn++;

						//refresh field and hand for current player

						
						System.out.println("Select a hand card to play");
						//int choice=scan.nextInt();
						//Card playing=Hand.get(choice);
						
						//initialize opponnet panle score collection
						if(OpponentCollection.size()!=0){
							OpponentPanel.returnOpponentCollectionPanel().updateCollection();
						}
						System.out.println("Score of the Other player: "+ AnotherScore);
						if(AnotherScore!=0){
							OpponentPanel.setScore(AnotherScore);
						}
						
						
						
						while (HandPanel.returnCurrentSelectedHandCard() == null) {
							//System.out.println("waiting for a hand card to be selected");
						}
						
						Card playing = HandPanel.returnCurrentSelectedHandCard();


						ArrayList<Card> temp = getMatchingCards (playing);
						System.out.println ("Player chose: "+ playing.getName());

						/*
						if (temp.size() == 0) { // If there is no match, need put the card onto the field
							System.out.println ("Because there are no matches, the card will be placed in the field");
							addHandCardToField(playing);
							this.waitForResponse();
	
						}
						*/
						HandPanel.highlightMatchingCards(playing);
						while (HandPanel.returnNumMatchingCards() == -1) { // changes by X. Wait for Gui to return num mathicng cards
							System.out.println("waiting to receive number of matching cards:" + HandPanel.returnNumMatchingCards() );
						}
						
						System.out.println("Number of Matching Cards:" +HandPanel.returnNumMatchingCards());
						
						if (HandPanel.returnNumMatchingCards() == 0) { // No match; need put the card onto the field
							System.out.println ("Going to refresh GUI");
							addHandCardToField (playing); // changed this function a little bit to resend field to GUI - Xiaohan
							this.waitForResponse();
							
						}
						
						else { // There is a match! It needs to be processed
							/*
							System.out.println ("There are possible matches for this card: ");
							for (int i = 0; i < temp.size(); i++){
								System.out.println ("<" + i + ">" + temp.get(i).getName());
							
							}
							
							System.out.println ("Select a card from field to match: ");
							int selectMatchedCard = scan.nextInt();
							*/
							
							Card selectedMatchedCard = null;
							while (FieldPanel.returnSelectedFieldCard() == null) {
								System.out.println("waiting for matched field card to be selected");
							}
							
							selectedMatchedCard = FieldPanel.returnSelectedFieldCard();
							
							FieldPanel.resetSelectedFieldCard(); // reset after receive
							System.out.println ("Selected matched card: " + selectedMatchedCard.getName());
							//this.processMatchAndRemoveCards(playing, temp.get(selectMatchedCard));
							this.processMatchAndRemoveCards(playing, selectedMatchedCard); // function changed a little 
													
							this.waitForResponse();	
						}
						
						// Get card from deck
						HandPanel.setScore(score);
						HandPanel.resetNumMatchingCards();// reset mathcing cards after use
						getCardFromDeck();
						
						line = (String) is.readObject();
						
						if (line.equals ("Signal:SendCardFromDeck")) {
							String nextMessage = (String) is.readObject();
							
							if (nextMessage.equals ("Signal:SendCard")) {
								try {
									Card received = (Card) is.readObject();	
									// wait for deck button to be clicked
									while(!deckButtonClicked){	
										System.out.println("waiting for deck to be clicked");
									}									
									//TODO: Notify GUI
									
									this.receivedDeckCard=received;
									
									
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								}
							}
						}
						
						System.out.println("I drew " + receivedDeckCard.getName() + " from deck");
						this.waitForResponse();
						
						
						ArrayList<Card> temp2 = getMatchingCards(receivedDeckCard);
						
						if (temp2.size()==0) { // No match to drawn card							
							FieldPanel.setDeckImage(receivedDeckCard);
							
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							FieldPanel.resetDeckImage();
							
							this.addDrawnCardToField(receivedDeckCard); // modified by x to refresh GUI
							
							this.waitForResponse();							
							
						}
						
						else {
//							System.out.println ("There are possible matches for this card:");
//							for (int i=0; i < temp2.size(); i++) {
//								System.out.println ("<" + i + ">" + temp2.get(i).getName());
//								
//							FieldPanel.setDeckImage(receivedDeckCard.getImage());// set deck image
							FieldPanel.setDeckImage(receivedDeckCard);// set deck image
							
							HandPanel.highlightMatchingCards(receivedDeckCard); 
							Card selectedMatchedCard = null;
							while (FieldPanel.returnSelectedFieldCard() == null) {
								System.out.println("waiting for matched field card to be selected");
							}
							
							FieldPanel.resetDeckImage();
							selectedMatchedCard = FieldPanel.returnSelectedFieldCard();
							
							FieldPanel.resetSelectedFieldCard(); // reset after receive to prevent errors
//							
//							System.out.println ("Select a card from field to match");
//							
//							int selectMatchedCard = scan.nextInt();
							this.processMatchAndRemoveCards (receivedDeckCard, selectedMatchedCard);
							
							this.waitForResponse();
							
						}
						
						System.out.println("My turn is over!!!!!!!!!!");
						System.out.println();
						System.out.println("My Hand cards now:");
						for (int k=0; k< Hand.size(); k++) {
							System.out.println ("<" + k + ">" + Hand.get(k).getName());		
						}
						System.out.println();
						System.out.println ("Field cards now:");
						for (int k = 0;k < Field.size(); k++) {
							System.out.println("<"+k+">"+Field.get(k).getName());		
						}
						System.out.println();
						System.out.println ("Collection cards now:");
						for (int k = 0; k < Collection.size(); k++) {
							System.out.println ("<" + k + ">" + Collection.get(k).getName());		
						}
						
						System.out.println();
						System.out.println ("My Score now: " + score);
						HandPanel.setScore(score);
						
						HandPanel.resetNumMatchingCards(); // added by X
						deckButtonClicked =false;
						OpponentPanel.refreshOpponentHand();
						
						//update opponent GUI
						endTurn();

						System.out.println("My turn is ended");	
						
						
				}
				
				
				// Receive card from deck
				if (line.equals ("Signal:SendCardFromDeck")) {
					String nextMessage = (String) is.readObject();
					
					if (nextMessage.equals ("Signal:SendCard")) {
						try {
							Card received = (Card) is.readObject();				
							
							//TODO: Notify GUI
							
							this.receivedDeckCard = received;
							
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
				
				
				// Receive opponent's score
				if (line.equals ("Signal:ScoreOfAnother")) {
						String nextMessage = (String) is.readObject();
						
						AnotherScore = Integer.valueOf(nextMessage);
						
						//DEBUG
						System.out.println ("Opponent Score is: " + AnotherScore);
					
				}
				
				
				// Receive opponent's selected card
				if(line.equals("Signal:ReceiveSelectedCard")){
					String nextMessage = (String) is.readObject();
					
					if (nextMessage.equals ("Signal:SendCard")) {		
						try {
							Card received = (Card) is.readObject();
							
							this.anotherSelectedCard = received;
							
							//DEBUG
							System.out.println ("Opponent is currently playing card :" + anotherSelectedCard.getName());
							
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
					
				}
				
				
				if (line.equals ("Signal:GameEnded")) {  
					//TODO: print to GUI that the game ended and show final score
					calculateFinalScore();
					sendFinalScore();
				}
				

			
			} // End of while(true) block
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	} // End of run() block
	
	public static void main (String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.print("what is your username: ");
		String playerName = scan.nextLine();
		HClient h = new HClient("localhost",6789, playerName);
		MyAssetHandler.load();
		MyGame g = new MyGame(h);
	}
}

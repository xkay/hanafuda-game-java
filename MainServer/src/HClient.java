import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.smartcardio.Card;

//TODO: How do we notify the GUI of when to change things?
		// For example, when the server sends a card
		// Maybe use enum to represent state
		// Change the state depending on what we want it to do


//TODO: Store client inside of GUI

public class HClient extends Thread {
	private PrintWriter pw;
	private BufferedReader br;
	private ObjectOutputStream os;
	private ObjectInputStream is;
	private ArrayList<Card> Hand = new ArrayList<Card>();
	private ArrayList<Card> Field = new ArrayList<Card>();
	private ArrayList<Card> Collection = new ArrayList<Card>();
	private boolean Host = false;
	private boolean MyTurn = false;
	
	public HClient (String hostname, int port) {	
		try {
			Socket s = new Socket (hostname, port);
			this.pw = new PrintWriter (s.getOutputStream());
			this.os = new ObjectOutputStream (s.getOutputStream());
			this.br = new BufferedReader (new InputStreamReader(s.getInputStream()));
			this.is = new ObjectInputStream (s.getInputStream());
			this.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // End of constructor
	
	public void sendMessage (String msg) {
		pw.println (msg);
		pw.flush();
	}
	
	public void sendCardToServer (Card cd) {
		try {
			// Inform server that client will be sending card
			pw.println ("Signal:SendCard");
			pw.flush();
			
			// Make sure card and message are not sent at the same time
			try {
				Thread.sleep(100);
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
		
		// Iterate through field
		for (int i = 0; i < Field.size(); i++) {
			// Check if card from the field is a match
			if (currentCard.isMatch (Field.get(i))) {
				potentialMatch.add (Field.get(i));
			}
		}
		
		return potentialMatch;
	}
	
	//TODO: Need method to process turn when there is no match
	
	// This method is called when a player wants to match a card from the hand with one from the field
	// They will collect the card from the field; discard any cards whose value is 0
	public void processMatchAndRemoveCards (Card cardFromHand, Card cardFromField) {
		// Add card to collection if value > 0
		if (cardFromHand.getValue() > 0) {		
			Collection.add(cardFromHand);
			//TODO: Update score 
		}
		
		// Add card to collection if value > 0
		if (cardFromField.getValue() > 0) {		
			Collection.add (cardFromField);
		}
		
		// Remove matched card from hand
		for(int i=0; i < Hand.size(); i++) {			
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
		// Send new card and field to server
		sendField();
		sendCollection();
	}
	
	public void endTurn(){
		
		
		sendMessage("Signal:EndTurn");
		
		MyTurn=false;
		
		
	}
	
	public void addDrawnCardToField(Card cd){
		
	
		Field.add(cd);
		
		sendField();
		
		
		
		
	}
	
	
	
	public void addHandCardToField(Card cd) {//for no match
		Field.add(cd);
		
		Hand.remove(cd);
		
		sendField();
		
		
	}
	
	public void getCardFromDeck(){
		
		
		sendMessage("Signal:GetCardFromDeck");
		
		
		

		
	}
	
	public void sendField () {
		sendMessage("Signal:UpdateField");
		
		for (int i = 0; i < Field.size(); i++) {
			sendMessage ("Signal:SendField");
			sendCardToServer (Field.get (i));
		}
		
		sendMessage ("Signal:UpdateFieldFinished");
		
		// What happens in the server when the first and last messages are sent?
	}
	
	public void sendCollection () {
		sendMessage ("Signal:UpdateCollection");
		
		for (int i =0; i < Collection.size(); i++) {
			sendMessage ("Signal:SendCollection");
			sendCardToServer (Collection.get (i));
		}
		
		sendMessage ("Signal:UpdateCollectionFinished");

		// What happens in the server when the first and last messages are sent?
	}
	
	
	public void run() {
		try {	
			while (true) {
				String line = br.readLine();
				System.out.println ("Client: Received message from Server: "+ line);
				
				// Host has the "start game" button while others have "prepared" button
				// NOTE: What does prepared do?
				
				if (line.equals ("Signal:Host")) {
					Host = true;
				}
				
				// Receive hand
				if (line.equals ("Signal:SendHand")) {
					// Read one more line message to confirm the receiving card process
					String nextMessage=br.readLine();
						
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
							e.printStackTrace();
						}
					}
						
				} // End of receive hand block
				
				// Receive field
				if (line.equals ("Signal:SendField")) {
					// Read one more line message to confirm the receiving card process
					String nextMessage = br.readLine();
					
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
				
				//TODO: Collections that are sent should be from other players
				if (line.equals ("Signal:SendCollection")) {
					// Read one more line message to confirm the receiving card process
						String nextMessage = br.readLine();
						
						if (nextMessage.equals ("Signal:SendCard")) {		
							try {
								Card received = (Card) is.readObject();
	
								//TODO: Add received card to the collection of the correct opponent
								
								//DEBUG
								System.out.println ("Collection Card received: " + received.getName());
								//System.out.println ("I have <" + Collection.size() + "> cards in Collection");
								
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
				}
					
				// Update field
				if (line.equals ("Signal:UpdateField")) {
					Field.clear();
				}
				
				// Update collection of opponent
				if (line.equals ("Signal:UpdateCollection")) {
					//TODO: Clear collection of correct opponent
				}
				
				// Determine the player's ability to act
				if (line.equals ("Signal:Turn")) {
						MyTurn = true;
						
						//DEBUG
						System.out.println ();
						System.out.println ("It is now my turn.");
						System.out.println ();
						for (Card c : Hand) {
							System.out.println ("Matching cards for " + c.getName() + ": ");
							for (Card match : getMatchingCards(c)) {
								match.printName();
							}
							System.out.println();
						}
						
						//DEBUG
						System.out.println();
						
				}
				
				// Receive card from deck
				if (line.equals ("Signal:SendCardFromDeck")) {
					String nextMessage = br.readLine();
					
					if (nextMessage.equals ("Signal:SendCard")) {
						try {
							Card received = (Card) is.readObject();
							//TODO: notify gui
							
							
							
							
							
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
				
				/*
				// Takes in matched cards from field. You to user in GUI and and the user decide which card should be added to collection
				if (line.equals ("Signal:SendMatchingCardsFromField")) {  
					try {
						Card received =  (Card) is.readObject();
						// TODO show these cards to user a matching card.Then add that card to collection
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
		
				}
				*/
				
				if (line.equals ("Signal:GameEnded")) {  
					//TODO: print to GUI that the game ended and show final score
				}
				
				if (line.equals ("Signal:YourTurn")) {  
					//TODO: Enable player's access in GUI
					// player chooses a card and system checks if there are any matching cards in the field
					//1. if no match, then put the card in the field
					//2. if there is one match, add both of these cards to collection
					//3. if there is more than one match, let the player choose which card, along with the card chosen, should be added to collection
						// if there is a match at all, then player should draw another card from a deck and system compares that card with the cards on field
						//repeat steps 1,2,3 once
				}
				
			} // End of while(true) block
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // End of run() block
	
	public static void main (String[] args) {
		new HClient("localhost",6789);
	}
}
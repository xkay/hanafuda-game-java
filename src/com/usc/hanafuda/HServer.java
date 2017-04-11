package com.usc.hanafuda;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

import com.usc.hanafuda.entities.Card;
import com.usc.hanafuda.entities.Deck;

public class HServer {
	public  Vector<ServerThread> vServerThread = new Vector<ServerThread>();
	private Deck deck;
	public ArrayList<Card> Field;
	public ArrayList<Card> Collection = new ArrayList<Card>();
	public ServerThread currentPlayer;
	public String hostName = "";
	public String clientName = "";
	
	public Card currentPlayedCard;
	
	public int hostScore = 0;
	public int guestScore = 0;
	private int finalHostScore = 0;
	private int finalGuestScore = 0;
	private boolean receivedFinalHostScore = false;
	private boolean receivedFinalGuestScore = false;
	public boolean close = false;
	
	public HServer (int port) {
		try {
			ServerSocket ss = new ServerSocket (port);

			while (true) {
				//DEBUG
				if (vServerThread.size() == 2 && !close) {
					onStartOfGame();
					close = true;
				}
				
				Socket s = ss.accept();
				ServerThread st = new ServerThread (s, this);
				vServerThread.add (st);
				st.start();	
				
				if (vServerThread.size() == 1) {
					//The first one connected is default to be the host
					sendMessage ("Signal:Host", st);
					//System.out.println("host connected");
				}
				
				//DEBUG
				//System.out.println ("Client has connected to the server.");
				
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	
	// Send a message to a specific client
	public synchronized void  sendMessage (String msg, ServerThread current) {
		//DEBUG
		//System.out.println ("Server: Sending message to client: " + msg);
		
		current.send (msg);
		
	}
	
	
	// Send a card to a specific client 
	public synchronized void sendCard (Card cd, ServerThread current) {
		//DEBUG
		//System.out.println ("Server: Sending card to client: " + cd.getName());
		
		current.sendCard (cd);
		
	}
	
	
	// Remove disconnected client
	public void removeDisconnection (ServerThread disconnected) {
		vServerThread.remove (disconnected);
		
	}

	
	// This method is called when server receives message to start the game
	// NOTE: The player will send the message "Signal:StartGame"
	public void onStartOfGame () {
		// Initialize Deck object
		deck = new Deck();
				
		// Initialize field
		int numInitialFieldCard = 0;
		int numInitialHandCard = 0;
		
		if (vServerThread.size() == 2) {
			numInitialFieldCard = 8;
			numInitialHandCard = 8;
		}
		
		Field = new ArrayList (numInitialFieldCard);
		
		for (int i = 0; i < numInitialFieldCard; i++) {
			Field.add (deck.drawCard());
			// Deck should now be 40
		}
		
		// Send correct amount of cards to each client
		for(int i = 0; i < vServerThread.size(); i++) { // For each client
			ServerThread current = vServerThread.get (i);
			
			for (int j = 0; j < numInitialHandCard; j++) {
				// Signal clients that the next card will be for the hand
				sendMessage ("Signal:SendHand", current);
			
				// Make sure the messages are not sent to clients at the same time as the cards 
				try {
					Thread.sleep (10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
				sendCard (deck.drawCard(), current);
			}
		} // Deck should be 24
		
		// Send field to each client
		for (int i = 0; i < vServerThread.size(); i++) { // For each client
			ServerThread current = vServerThread.get (i);
			
			for (int j = 0; j < numInitialFieldCard; j++) {
				// Signal clients that the next card will be for the field
				sendMessage ("Signal:SendField", current);
				
				// Make sure the messages are not sent to clients at the same time as the cards 
				try {
					Thread.sleep (10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				sendCard (Field.get(j), current);
			}
		}
		
		//DEBUG
		//System.out.println ("Amount of cards in deck: " + deck.numCardsLeft());
		
		// Notify first player of his/her turn; as default, the host gets the first turn
		ServerThread host = vServerThread.get (0);
		sendMessage ("Signal:Turn", host);
		currentPlayer = host;
		
	} // End of OnStartOfGame() block
	
	
	// This method will send the updated field to the opponent of the current player
	public synchronized void updateField (ServerThread oppo) {
		sendMessage("123",oppo); // Junk message to allow Signal:UpdateField to be sent
		
		sendMessage ("Signal:UpdateField", oppo);

		for(int j=0; j < Field.size(); j++) {
			// Signal clients that the next few cards will be the field
			sendMessage ("Signal:SendField", oppo);
			
			// Make sure the messages are not sent to clients at the same time as the cards
			//TODO: The sleep was commented out earlier, why?
			try {
				Thread.sleep (10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			sendCard (Field.get (j), oppo);
		}			
		
		sendMessage ("Signal:UpdateFieldEnded", oppo);
		
	} // End of updateField() block
	
	
	// This method sends the updated collection to the opponent of the current player
	public synchronized void updateOpponentCollection (ServerThread oppo) {			
		sendMessage ("Signal:UpdateOpponentCollection", oppo);
	
		for(int j=0; j < Collection.size(); j++) {
			// Signal clients that the next few cards will be the opponent's collection
			sendMessage ("Signal:SendOpponentCollection", oppo);
			
			// Make sure the messages are not sent to clients at the same time as the cards 
			try {
				Thread.sleep (10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			sendCard (Collection.get (j), oppo);
		}
		
		sendMessage ("Signal:SendOpponentCollectionEnded", oppo);
		
	} // End of updateOpponenetCollection() block
	
	
	// This method will send card from the deck to the current client
	public synchronized void sendCardFromDeck () {
		// Draw card from deck
		Card drawnCard = deck.drawCard();
		
		// Inform the player that a card will be sent
		sendMessage ("Signal:SendCardFromDeck", currentPlayer);
		
		sendCard (drawnCard, currentPlayer);
		
	}
	
	
	// This method sets the changes the current player
	public void nextPlayer() {
		if(currentPlayer.equals(vServerThread.get(0))){
			currentPlayer=vServerThread.get(1);
			
		}
		
		else if(currentPlayer.equals(vServerThread.get(1))){
			currentPlayer=vServerThread.get(0);
			
		}		
		
	}
	
	
	// This method returns true if the game is over
	public boolean GameIsOver() {		
		if(deck.numCardsLeft()==8) {
			//DEBUG
			//System.out.println ("Game is over");
			
			return true;
		}
		
		return false;	
		
	}
	
	
	public void notifyEndOfGame() {
		for (ServerThread s : vServerThread) {
			sendMessage ("Signal:GameEnded", s);
		}
		
	}
	
	
	public boolean hasReceivedFinalHostScore() {
		return receivedFinalHostScore;
		
	}
	
	
	public boolean hasReceivedFinalGuestScore() {
		return receivedFinalGuestScore;
		
	}
	
	
	public void setFinalHostScore (int s) {
		finalHostScore = s;
		receivedFinalHostScore = true;
		
	}
	
	
	public void setFinalGuestScore (int s) {
		finalGuestScore = s;
		receivedFinalGuestScore = true;
		
	}
	
	
	public void saveScoresInDatabase() {		
		new Database (hostName, clientName, finalHostScore, finalGuestScore);
		
	}
	
	
	public void setHostName (String s) {
		hostName = s;
		
	}
	
	
	public void setClientName (String s) {
		clientName = s;
		
	}

	
	public static void main(String [] args){
		new HServer(6789);
	}	
}

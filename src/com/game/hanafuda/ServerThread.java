package com.usc.hanafuda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.usc.hanafuda.entities.Card;

public class ServerThread extends Thread {
	private Socket s;
	private HServer hs;
	//private PrintWriter pw;
	private ObjectOutputStream os;
	private ObjectInputStream is;
	private int PendingScore;
	private ServerThread PendingTarget;

	public ServerThread (Socket s, HServer hs) {
		this.s = s;
		this.hs = hs;
		try {
			//this.pw=new PrintWriter(s.getOutputStream());
			this.os = new ObjectOutputStream(s.getOutputStream());
			this.is = new ObjectInputStream(s.getInputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	// This method sends a card to the client
	public void sendCard (Card cd){
		try {
			// Send as object instead of using printwriter
			os.reset();
			os.writeObject ("Signal:SendCard");
			os.flush();

			try {
				this.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			os.reset();
			os.writeObject(cd);
			os.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// This method sends a String to the client
	public void send (String message) {
		try {
			os.reset();
			os.writeObject (message);
			os.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run () {
		try {
			while(true){
				// Receive String from client
				String line = (String) is.readObject();

				// Nothing more to read, close the socket
				if (line==null) {
					s.close();
					hs.removeDisconnection(this);
					break;
				}

				if (line.equals ("Signal:StartGame")) {
					hs.onStartOfGame();
				}

				// Client is sending Field to server
				if (line.equals ("Signal:SendField")) {
					// Read one more line message to confirm the receiving card process
					String nextMessage = (String) is.readObject();
					if (nextMessage.equals ("Signal:SendCard")) {
						try {
							Card received = (Card) is.readObject();
							// Add received card to field
							hs.Field.add(received);
							received.printName();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}

				// Client is sending his/her collection
				if (line.equals ("Signal:SendCollection")) {
					// Read one more line message to confirm the receiving card process
					String nextMessage = (String) is.readObject();
					if (nextMessage.equals ("Signal:SendCard")) {
						try {
							Card received = (Card) is.readObject();

							// Add received card to collection
							hs.Collection.add (received);
							received.printName();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}

				// Client wants to update the Field; clear the field to prepare
				if (line.equals ("Signal:UpdateField")) {
					hs.Field.clear();
				}
				// Client wants to update the Collection; clear the collection to prepare
				if (line.equals ("Signal:UpdateCollection")) {
					hs.Collection.clear();
				}
				// The update of the field has finished
				if (line.equals ("Signal:UpdateFieldFinished")) {
					ServerThread oppo = null;

					for (int i=0; i < hs.vServerThread.size(); i++) {
						if (!hs.vServerThread.get(i).equals(this)) {
							// Set opponent thread to the thread which is not this one
							oppo = hs.vServerThread.get(i);
						}
					}
					// Send opponent the updated field
					hs.updateField (oppo);
				}
				if (line.equals ("Signal:UpdateCollectionFinished")) {
					// Find opponent
					ServerThread oppo = null;
					for (int i = 0; i < hs.vServerThread.size(); i++) {
						if (!hs.vServerThread.get(i).equals(this)) {
							oppo=hs.vServerThread.get(i);
						}
					}

					// Update opponent's Field //TODO: Why update the field here?
					hs.updateField(oppo);

					try {
						this.sleep(400);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Update opponent's collection
					hs.updateOpponentCollection(oppo);

					try {
						this.sleep(400);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Signal opponent of incoming score //TODO: Where does PendingTarget get set?
					hs.sendMessage ("Signal:ScoreOfAnother", PendingTarget);

					try {
						this.sleep(400);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// Send opponent this client's score
					hs.sendMessage (Integer.toString(PendingScore), PendingTarget);
				}
				// Send top card from deck
				if (line.equals ("Signal:GetCardFromDeck")) {
					hs.sendCardFromDeck();
				}

				// Signal received when player has finished his/her turn
				if (line.equals ("Signal:EndTurn")) {
					// If the game is not over, signal the next player of his/her turn
					if (!hs.GameIsOver()) {
						// Set next player to current player
						hs.nextPlayer();
						hs.sendMessage ("Signal:Turn", hs.currentPlayer);
					}

					// If the game is over
					else {
						hs.notifyEndOfGame();
					}
				}

				if (line.equals ("Signal:SendName")) {
					String nextMessage = (String) is.readObject();

					// If the score belongs to the host
					if (nextMessage.charAt(0) == 'h') {
						String name = nextMessage.substring(1, nextMessage.length());
						hs.setHostName (name);

					}

					// If the score is not the host's
					else {
						hs.setClientName (nextMessage);
					}
				}

				if (line.equals ("Signal:SendScore")) {
					String nextMessage = (String) is.readObject();

					// If the score belongs to the host
					if (nextMessage.charAt(0) == 'h') {
						int score = Integer.valueOf (nextMessage.substring(1, nextMessage.length()));
						hs.hostScore = score;
						PendingScore = score;

						// Set pending target to opponent (not host)
						ServerThread another = hs.vServerThread.get(1);

						PendingTarget = another;

						hs.sendMessage("Signal:ScoreOfAnother", another);
						hs.sendMessage(Integer.toString(score), another);
					}

					// If the score is not the host's
					else {
						int score = Integer.valueOf(nextMessage);
						hs.guestScore = score;
						PendingScore = score;

						// Set pending target to opponent (host)
						ServerThread another = hs.vServerThread.get(0);
						PendingTarget = another;

						hs.sendMessage("Signal:ScoreOfAnother", another);
						hs.sendMessage(Integer.toString(score), another);
					}
				}

				// Client sends server the card that was selected from hand
				if (line.equals ("Signal:SendSelectedCard")) {
					try {
						String nextMessage = (String) is.readObject();

						if (nextMessage.equals ("Signal:SendCard")) {
							Card received = (Card) is.readObject();
							hs.currentPlayedCard = received;
							ServerThread another = null;

							// Find the server thread of the other player
							for (int i = 0; i < hs.vServerThread.size(); i++) {
								if(!hs.vServerThread.get(i).equals(hs.currentPlayer)){
									another = hs.vServerThread.get(i);
								}
							}

							// Send selected card to the other player
							hs.sendMessage ("Signal:ReceiveSelectedCard", another);
							hs.sendCard (hs.currentPlayedCard, another);
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}

				if (line.equals ("Signal:SendFinalScore")) {
					String nextMessage = (String) is.readObject();
					// If the score belongs to the host
					if (nextMessage.charAt(0) == 'h') {
						int score = Integer.valueOf (nextMessage.substring(1, nextMessage.length()));
						hs.setFinalHostScore(score);
						if (hs.hasReceivedFinalGuestScore() && hs.hasReceivedFinalHostScore()) {
							hs.saveScoresInDatabase();
						}
						PendingScore = score;
						// Set pending target to opponent (not host)
						ServerThread another = hs.vServerThread.get(1);
						PendingTarget = another;

						hs.sendMessage("Signal:FinalScoreOfAnother", another);
						hs.sendMessage(Integer.toString(score), another);
					}

					// If the score is not the host's
					else {
						int score = Integer.valueOf(nextMessage);
						hs.setFinalGuestScore(score);
						if (hs.hasReceivedFinalGuestScore() && hs.hasReceivedFinalHostScore()) {
							hs.saveScoresInDatabase();
						}
						PendingScore = score;
						// Set pending target to opponent (host)
						ServerThread another = hs.vServerThread.get(0);
						PendingTarget = another;

						hs.sendMessage("Signal:FinalScoreOfAnother", another);
						hs.sendMessage(Integer.toString(score), another);

					}
				}
			} // End of while loop
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	} // End of run()
}

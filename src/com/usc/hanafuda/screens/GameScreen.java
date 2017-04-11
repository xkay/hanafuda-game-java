package com.usc.hanafuda.screens;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.usc.hanafuda.HClient;
import com.usc.hanafuda.MyGame;
import com.usc.hanafuda.entities.Deck;
import com.usc.hanafuda.entities.FieldPanel;
import com.usc.hanafuda.entities.HandPanel;
import com.usc.hanafuda.entities.OpponentPanel;
import com.usc.hanafuda.handlers.MyAssetHandler;

public class GameScreen extends JPanel {
	private JTextField textField;
	private JTextArea textArea;
	private JButton sendMessage;
	private HandPanel handPanel;
	private OpponentPanel opponentPanelNorth;
	private FieldPanel fieldPanel;
	private MyGame myGame;
	private HClient client;
	
	
	public GameScreen (MyGame mg, HClient hc) {
		this.myGame = mg;
		this.client = hc;
		this.setLayout (new BorderLayout());
		
		// Create panel for game play
		JPanel deckPanel = new JPanel();
		deckPanel.setLayout (new BorderLayout());
		deckPanel.setOpaque (false);
		add (deckPanel, BorderLayout.CENTER);

		// Create panel for player's hand
		handPanel = new HandPanel (myGame.getHClient(), this);
		deckPanel.add (handPanel, BorderLayout.SOUTH);
	
		// Create panel for field cards
		fieldPanel = new FieldPanel (myGame.getHClient(), this);
		deckPanel.add (fieldPanel, BorderLayout.CENTER);
		
		// Create panel to show opponent's hand
		opponentPanelNorth = new OpponentPanel (myGame.getHClient(), this);
		deckPanel.add (opponentPanelNorth, BorderLayout.NORTH);
		
		// Create panel for the chat box
		JPanel textPanel = new JPanel();
		textPanel.setLayout (new BorderLayout());
		textPanel.setBorder (BorderFactory.createLineBorder (Color.darkGray, 1));
		add (textPanel, BorderLayout.EAST);
		textPanel.setPreferredSize (new Dimension (250, 1000));

		// Create panel for the bottom of the chat box, where a player can enter text
		JPanel chatBottomPanel = new JPanel (new FlowLayout());
		
		textField = new JTextField (15);
		textField.addKeyListener (new ChatKeyListener (this));
		
		sendMessage = new JButton ("Send");
		sendMessage.addActionListener (new ChatActionListener (this));
		
		chatBottomPanel.add (textField);
		chatBottomPanel.add (sendMessage);
		
		textPanel.add (chatBottomPanel, BorderLayout.SOUTH);
		
		// Create area for the text to appear in
		textArea = new JTextArea ("", 7, 20);
		textArea.setEditable (false);
		textArea.setLineWrap (true);
		textPanel.add (textArea, BorderLayout.CENTER);

	}
	
	
	public MyGame getMyGame() {
		return myGame;
		
	}
	
	
	public HClient getClient() {
		return client;
		
	}

	
	public FieldPanel getFieldPanel () {
		return fieldPanel;
		
	}
	
	
	public HandPanel getHandPanel () {
		return handPanel;
		
	}
	
	
	public OpponentPanel getOpponentPanel() {
		return opponentPanelNorth;
		
	}
	
	
	public void enterNewMessage (String newMessage) {
		textArea.append (newMessage + "\n");
		
	}
	
	
	public void sendFinalScoreMessage (String message) {
		getMyGame().getChatClient().sendMessage (message);
		textArea.append (message + "\n");
	}
	
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	
	}
	
	
	// ActionListener for send button; sends entered text to server
	public class ChatActionListener implements ActionListener {
		private GameScreen myGameScreen;
		
		public ChatActionListener (GameScreen gameScreen) {
			myGameScreen = gameScreen;
		}
		
		public void actionPerformed (ActionEvent ae) {
			if(textField.getText().length() < 1) {
				//do nothing, no message
			}
			else{
				String newMessage = "<" + myGame.getName() + ">: " + textField.getText();
				myGameScreen.getMyGame().getChatClient().sendMessage(newMessage);
				textArea.append(newMessage + "\n"); 
				textField.setText("");
			}
		}
	}
	
	
	// KeyListener for when enter key is clicked; sends entered text to server
	public class ChatKeyListener implements KeyListener {
		private GameScreen myGameScreen;
		
		public ChatKeyListener (GameScreen gameScreen) {
			myGameScreen = gameScreen;
		}

		public void keyPressed (KeyEvent ke) {
			if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
				if (textField.getText().length() < 1) {
					//do nothing, no message
				}
				else {
					String newMessage = "<" + myGame.getName() + ">: " + textField.getText();
					myGameScreen.getMyGame().getChatClient().sendMessage(newMessage);
					textArea.append(newMessage + "\n"); 
					textField.setText("");
				}
			}
		}

		public void keyReleased(KeyEvent arg0) {			
		}

		public void keyTyped(KeyEvent e) {
			
		}
	}

}

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

import com.usc.hanafuda.MyGame;
import com.usc.hanafuda.entities.Deck;
import com.usc.hanafuda.entities.FieldPanel;
import com.usc.hanafuda.entities.HandPanel;
import com.usc.hanafuda.entities.OpponentPanel;
import com.usc.hanafuda.handlers.MyAssetHandler;

public class GameScreen extends JPanel {
	//
	//private JLabel t = new JLabel("another player");
	private JButton backButton;
	private JTextField textField;
	private JTextArea textArea;
	private JButton sendMessage;
	private HandPanel handPanel;
	private OpponentPanel opponentPanelNorth;
	//public static Deck deck;
	private FieldPanel fieldPanel;
	private MyGame myGame;
	
	
	public GameScreen(MyGame mg) {
		//deck = new Deck();

		this.myGame = mg;
		this.setLayout(new BorderLayout());
		JPanel deckPanel = new JPanel();
		deckPanel.setLayout(new BorderLayout());
		deckPanel.setOpaque(false);
		add(deckPanel, BorderLayout.CENTER);

		//deckPanel.add(t, BorderLayout.WEST);

//		BufferedImage image = null;
//		try {
//			image = ImageIO.read(new File("Image1.png"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		//JLabel imageLabel = new JLabel(new ImageIcon(image));

		//deckPanel.add(imageLabel, BorderLayout.SOUTH);
		handPanel = new HandPanel( myGame.getHClient(), this);
		deckPanel.add(handPanel, BorderLayout.SOUTH);

		
		
		fieldPanel = new FieldPanel(myGame.getHClient(), this);
		deckPanel.add(fieldPanel, BorderLayout.CENTER);
		
		//deckPanel.add(fieldPanel, BorderLayout.CENTER);
		opponentPanelNorth = new OpponentPanel(myGame.getHClient(), this);
		deckPanel.add(opponentPanelNorth, BorderLayout.NORTH);
		
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BorderLayout());
		textPanel.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
		add(textPanel, BorderLayout.EAST);
		textPanel.setPreferredSize(new Dimension(250, 1000));

		backButton = new JButton("Back");
		textPanel.add(backButton, BorderLayout.NORTH);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aa) {
				myGame.setPanel(new MenuScreen(myGame));

			}
		});
		
		JPanel chatBottomPanel = new JPanel(new FlowLayout());
		
		textField = new JTextField(15);
		//textPanel.add(textField, BorderLayout.SOUTH);
		
		sendMessage = new JButton("Send");
		//textPanel.add(sendMessage, BorderLayout.SOUTH);
		sendMessage.addActionListener(new ChatActionListener(this));
		textField.addKeyListener(new ChatKeyListener(this));
		
		chatBottomPanel.add(textField);
		chatBottomPanel.add(sendMessage);
		
		textPanel.add(chatBottomPanel, BorderLayout.SOUTH);
		textArea = new JTextArea("", 7, 20);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textPanel.add(textArea, BorderLayout.CENTER);

		
		
		
		
		
		
		
//		// temp code:
//		JPanel tf = new JPanel();
//		tf.setOpaque(false);
//		JLabel ml = new JLabel("another player");
//		tf.add(ml);
//		deckPanel.add(tf, BorderLayout.NORTH);
//
//		JPanel te = new JPanel();
//		te.setOpaque(false);
//		JLabel mle = new JLabel("another player");
//		te.add(mle);
//		deckPanel.add(mle, BorderLayout.EAST);
//
//		JPanel o = new JPanel();
//		o.setOpaque(false);
//		o.setLayout(new GridBagLayout());
//		JLabel m = new JLabel("main deck");
//		o.add(m);
//		deckPanel.add(o, BorderLayout.CENTER);

	}
	
	public MyGame getMyGame() {
		return myGame;
	}

	public FieldPanel getFieldPanel () {
		return fieldPanel;
	}
	
	public void enterNewMessage (String newMessage) {
		textArea.append(newMessage + "\n");
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		//g.setColor(new Color(246, 244, 186));
		//g.fillRect(0, 0, 1400, 1000);

		// temp:
		//g.setColor(Color.black);
		//g.drawRect(100, 100, 1000, 700);
		//g.drawImage(MyAssetHandler.deckImage, 200, 300, null);

	
	}
	
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

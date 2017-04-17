package com.usc.hanafuda.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import com.usc.hanafuda.HClient;
import com.usc.hanafuda.screens.GameScreen;

public class OpponentPanel extends JPanel {
	private HClient hClient;
	private GameScreen gameScreen;
	private JPanel cardPanel;
	private OpponentCollectionPanel opponentCollectionPanel;

	private JButton showCapturedCard;
	private JLabel numCards, scoreLabel;
	private int cardLeft = 8;
	private int score = 0;
	private BufferedImage cardFaceDown;

	public OpponentPanel (HClient hc, GameScreen gs) {
		this.hClient = hc;
		this.gameScreen = gs;

		this.setLayout (new BoxLayout(this, BoxLayout.X_AXIS));

		this.add (Box.createHorizontalGlue());
		showCapturedCard = new JButton ("Show Captured Cards");
		showCapturedCard.setMaximumSize (new Dimension (160,40));
		opponentCollectionPanel = new OpponentCollectionPanel (gs);

		int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
	    int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
		final JScrollPane jsp = new JScrollPane (opponentCollectionPanel, v , h);
		this.add (showCapturedCard);

		showCapturedCard.addActionListener (new ActionListener() {
			public void actionPerformed (ActionEvent ae) {
				JFrame popup = new JFrame ("Opponent's Collection Cards");
				popup.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
				popup.setSize (500,300);
				popup.setLocation (100, 100);
				popup.setVisible (true);
				popup.setResizable (true);
				popup.add (jsp);
			}
		});

		// Create panel to show opponent's hand
		cardPanel = new JPanel();
		cardPanel.setBackground (Color.LIGHT_GRAY);
		add (cardPanel);
		this.add (Box.createHorizontalGlue());

		// Create panel that contains score and number of cards left
		JPanel eastPanel = new JPanel();
		eastPanel.setBackground (Color.LIGHT_GRAY);
		eastPanel.setLayout (new BoxLayout(eastPanel, BoxLayout.Y_AXIS));

		// Create label for score
		scoreLabel = new JLabel ("Score: " + Integer.toString(score) + " ");
		scoreLabel.setFont (new Font ("Arial", Font.PLAIN, 30));
		eastPanel.add (scoreLabel);

		// Create label for cards left
		numCards = new JLabel ("Cards left: " + cardLeft + " ");
		numCards.setFont (new Font ("Arial", Font.PLAIN, 30));
		eastPanel.add (numCards);

		this.add (eastPanel);
		this.setBorder (BorderFactory.createLineBorder (Color.darkGray, 3));
		this.setBackground (Color.LIGHT_GRAY);
		this.setPreferredSize (new Dimension (1150, 200));
		this.setMinimumSize (new Dimension (1150, 200));
		this.setMaximumSize (new Dimension (1150, 200));

		try {
			cardFaceDown = ImageIO.read (new File ("deck.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		refreshOpponentHand();
	}


	public void setScore (int score) {
		scoreLabel.setText ("Score: " + Integer.toString (score));
	}

	public OpponentCollectionPanel returnOpponentCollectionPanel() {
		return opponentCollectionPanel;
	}

	public void incScore (int incBy) {
		score = score + incBy;
		scoreLabel.setText ("Score: " + Integer.toString (score));
	}

	public void discardCard() {
		cardLeft--;
		numCards.setText ("Cards left: " + cardLeft + " ");
		cardPanel.removeAll();
		refreshOpponentHand();
	}

	public void refreshOpponentHand() {
		cardPanel.removeAll();

		for (int i = 0; i < cardLeft; i++) {
			cardPanel.add (new JLabel (new ImageIcon (cardFaceDown)));
		}

		numCards.setText ("Cards left: " + cardLeft + " ");
	}
}

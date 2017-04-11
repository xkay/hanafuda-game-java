package com.usc.hanafuda;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.usc.hanafuda.handlers.MyAssetHandler;
import com.usc.hanafuda.screens.GameScreen;
import com.usc.hanafuda.screens.MenuScreen;

public class MyGame extends JFrame {
	public static final boolean DEBUG = true;
	static JPanel currentPanel;
	static HClient client;
	static String playerName;
	private ChatClient chatClient;
	private GameScreen gameScreen;

	public MyGame (HClient h) {
		super ("Hanafuda");

		setSize (1400, 1000);
		setLocation (150, 30);
		setMinimumSize (new Dimension (1350, 650));
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

		// Initialize variables
		this.client = h;
		this.playerName = h.getUserName();
		chatClient = new ChatClient ("localhost", 7777, this);

		// Create MenuScreen and set as current panel
		currentPanel = new MenuScreen (this, h);
		add(currentPanel);

		h.setMyGame (this);

		setVisible(true);
	}

	public synchronized GameScreen getGameScreen() {
		return gameScreen;
	}

	public void setGameScreen (GameScreen gs) {
		gameScreen = gs;
	}

	public ChatClient getChatClient() {
		return chatClient;
	}

	public String getName() {
		return playerName;
	}

	public HClient getHClient(){
		return client;
	}

	public void setPanel (JPanel panel) {
		this.remove (currentPanel);
		currentPanel = panel;
		this.add (currentPanel);
		this.repaint();
		this.revalidate();
	}

	public JPanel getCurrentPanel(){
		return currentPanel;
	}

}

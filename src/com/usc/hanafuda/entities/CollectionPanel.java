package com.usc.hanafuda.entities;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import com.usc.hanafuda.handlers.MyAssetHandler;
import com.usc.hanafuda.screens.GameScreen;

public class CollectionPanel extends JPanel {
	private GameScreen gameScreen;
	

	public CollectionPanel (GameScreen gs) {
		gameScreen = gs;
		this.setLayout (new FlowLayout());

	}
	
	
	public void updateCollection() {
		//DEBUG
		//System.out.println ("CollectionPanel: updateCollection()");
		
		ArrayList<Card> collection = gameScreen.getClient().getCollection();
		
		removeAll();
		
		for (int i = 0 ; i < collection.size(); i++) {
			int id = collection.get(i).getId();
			Image buffered = MyAssetHandler.cardImageArray[id].getScaledInstance(85,140,Image.SCALE_SMOOTH);
			JLabel iconLabel1 = new JLabel (new ImageIcon (buffered));
			this.add (iconLabel1);
			
			this.validate();
			this.repaint();
			
		}
	}
	
}

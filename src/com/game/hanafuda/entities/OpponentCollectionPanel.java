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

public class OpponentCollectionPanel extends JPanel {
	private GameScreen gameScreen;

	Lock lock = new ReentrantLock();
	private  boolean refreshFlag = false;
	private boolean removeAllPictures = false;


	public OpponentCollectionPanel (GameScreen gs) {
		this.gameScreen = gs;
		this.setLayout(new FlowLayout());
	}

	public void run(){
		while(true){
			this.validate();
			this.revalidate();
			this.repaint();

			lock.lock();
			if(removeAllPictures == true){
				this.removeAll();
				this.revalidate();
				removeAllPictures = false;
			}
			lock.unlock();
		}
	}

	public  void updateCollection() {
		//DEBUG
		System.out.println("OpponentCollectionPanel: Entering updateCollection()");

		ArrayList<Card> collection = gameScreen.getClient().getOpponentCollection();

		this.removeAll();
		this.revalidate();

		for (int i = 0 ; i < collection.size(); i++) {
			int id = collection.get(i).getId();
			Image buffered = MyAssetHandler.cardImageArray[id].getScaledInstance (85, 140, Image.SCALE_SMOOTH);
			JLabel iconLabel1 = new JLabel (new ImageIcon (buffered));
			this.add (iconLabel1);

			this.validate();
			this.repaint();
		}
	}
}

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

public class CollectionPanel extends JPanel implements Runnable{
	
	Lock lock = new ReentrantLock();
	private  static boolean refreshFlag = false;
	private static boolean removeAllPictures = false;


	CollectionPanel(){
//		this.setEditable(false);
		this.setLayout(new FlowLayout());
//		JLabel iconLabel2 = new JLabel( new ImageIcon ( "deck.png" ));
//		this.add(iconLabel1);
//		this.add(iconLabel2);

		Thread t = new Thread (this); // added by X
		t.start();
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
			
//			if(refreshFlag ==true){
//				refreshDisplay();
//				refreshFlag=false;
//			}
			lock.unlock();

		
			
		}
	}
	
	public void updateCollection(){
		System.out.println("refreshing collection");
		ArrayList<Card> collection = HandPanel.hClient.getCollection();
		
		removeAllPictures = true;
		while (removeAllPictures==true){
			System.out.println("waiting for your collection to be erased");
		}

		System.out.println("Collection successfully erased");
		
		for(int i = 0 ; i < collection.size(); i++){
			int id = collection.get(i).getId();
			Image buffered = MyAssetHandler.cardImageArray[id].getScaledInstance(85,140,Image.SCALE_SMOOTH);
			JLabel iconLabel1 = new JLabel(new ImageIcon(buffered));
//			JLabel imageLabel = new JLabel(ic);
//			imageLabel.setBounds(40+i*5, 40, MyAssetHandler.WIDTH, MyAssetHandler.HEIGHT );
			this.add(iconLabel1);
			System.out.println("reading captured image to collection:");
			String n  = collection.get(i).getName();
			System.out.println("Adding " + n + " to Collection Panel");
			this.validate();
			this.repaint();
		}

		
	}
	
}

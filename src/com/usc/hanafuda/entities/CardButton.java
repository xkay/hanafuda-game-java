package com.usc.hanafuda.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.usc.hanafuda.handlers.MyAssetHandler;

public class CardButton extends JButton{
	private int x =40;
	private boolean setGlow = false;
	private boolean myTurn = true;
	private Card card;
	private boolean cardUp;
	
	public CardButton(ImageIcon cardIconArray){
		this.setIcon(cardIconArray);
		this.cardUp = false;
	}

	public CardButton() {
		// TODO Auto-generated constructor stub
	}

	public int getNewX() {
		return x;
	}
	
	public Card returnCard(){
		return card;
	}

	public boolean isCardUp() {
		return cardUp;
	}
	public void moveUp(){
		x = 20;
	}
	
	public void moveDown(){
		x = 40;
	}

	public void setCardImage(Card card){
		ImageIcon icon = MyAssetHandler.getIcon(card.getId());
		this.setIcon(icon);	
		this.card = card;
		
		
	}
	
	
	
	public void setGlow(){
		setGlow = true;
	}
	
	public void unsetGlow() {
		setGlow = false;
	}
	
	public void isMyTurn() {
		myTurn = true;
	}
	public void isNotMyTurn() {
		myTurn = false;
	}
	public boolean isGlowSet() {
		return setGlow;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		if(setGlow == true) { 
			g2.setStroke(new BasicStroke(5));
			g2.setFont(new Font("Arial", Font.BOLD, 20)); 
			g2.setColor(Color.green);
			g2.drawRect(5, 5, 75, 130);
		}
		if(myTurn == false) {
			g.setColor(Color.red);
			g.setFont(new Font("Arial", Font.BOLD, 18)); 
			g.drawString("Wait", 10, 10);
			g.drawString("For", 10, 20);
			g.drawString("Opponent", 10, 30);
		}
		
	}

	
}

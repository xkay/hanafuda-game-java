package com.usc.hanafuda.entities;


import java.util.ArrayList;
import java.util.Collections;


import java.util.ArrayList;
import java.util.Collections;

import com.usc.hanafuda.handlers.MyAssetHandler;

public class Deck {
	private ArrayList<Card> cardDeck;
	
	public Deck () {
		cardDeck = new ArrayList<Card> ();
		initializeCards();
		initializeCardImages();
		shuffleDeck();
	}
	
	private void shuffleDeck() {
		Collections.shuffle (cardDeck);
	}
	
	public int numCardsLeft() {
		return cardDeck.size();
	}
	
	public Card drawCard() {
		Card c = cardDeck.get(0);
		cardDeck.remove(0);
		return c;
	}
	
	//DEBUG
	public void printDeck () {
		for (Card c : cardDeck) {
			c.printName();
		}
	}

	
	private void initializeCards () {
		cardDeck.add (new Card (0, "January0_20pts_Ro", Card.Month.January, Card.Yaku.Ro, null, false, null, 20));
		cardDeck.add (new Card (1, "January1_10pts_Ha", Card.Month.January, Card.Yaku.Ha, null, false, null, 10));
		cardDeck.add (new Card (2, "January2_0pts", Card.Month.January, null, null, false, null, 0));
		cardDeck.add (new Card (3, "January3_0pts", Card.Month.January, null, null, false, null, 0));
		cardDeck.add (new Card (5, "February0_10pts_Ha", Card.Month.February, Card.Yaku.Ha, null, false, null, 10));
		cardDeck.add (new Card (4, "February1_5pts_Ro", Card.Month.February, Card.Yaku.Ro, null, false, null, 5));
		cardDeck.add (new Card (6, "February2_0pts", Card.Month.February, null, null, false, null, 0));
		cardDeck.add (new Card (7, "February3_0pts", Card.Month.February, null, null, false, null, 0));
		cardDeck.add (new Card (8, "March0_20pts_I", Card.Month.March, Card.Yaku.I, Card.Yaku.Ro, false, null, 20));
		cardDeck.add (new Card (9, "March1_10pts_Ha", Card.Month.March, Card.Yaku.Ha, null, false, null, 10));
		cardDeck.add (new Card (10, "March2_0pts", Card.Month.March, null, null, false, null, 0));
		cardDeck.add (new Card (11, "March3_0pts", Card.Month.March, null, null, false, null, 0));
		cardDeck.add (new Card (13, "April0_10pts_He", Card.Month.April, Card.Yaku.He, null, false, null, 10));
		cardDeck.add (new Card (12, "April1_5pts_To", Card.Month.April, Card.Yaku.To, null, false, null, 5));
		cardDeck.add (new Card (14, "April2_0pts", Card.Month.April, null, null, false, null, 0));
		cardDeck.add (new Card (15, "April3_0pts", Card.Month.April, null, null, false, null, 0));
		cardDeck.add (new Card (17, "May0_10pts_He", Card.Month.May, Card.Yaku.He, null, false, null, 10));
		cardDeck.add (new Card (16, "May1_5pts_To", Card.Month.May, Card.Yaku.To, null, false, null, 5));
		cardDeck.add (new Card (18, "May2_0pts", Card.Month.May, null, null, false, null, 0));
		cardDeck.add (new Card (19, "May3_0pts", Card.Month.May, null, null, false, null, 0));
		cardDeck.add (new Card (21, "June0_10pts_Ni", Card.Month.June, Card.Yaku.Ni, null, false, null, 10));
		cardDeck.add (new Card (20, "June1_5pts_Ho", Card.Month.June, Card.Yaku.Ho, null, false, null, 5));
		cardDeck.add (new Card (22, "June2_0pts", Card.Month.June, null, null, false, null, 0));
		cardDeck.add (new Card (23, "June3_0pts", Card.Month.June, null, null, false, null, 0));
		cardDeck.add (new Card (25, "July0_10pts_He", Card.Month.July, Card.Yaku.He, null, false, null, 10));
		cardDeck.add (new Card (24, "July1_5pts_To", Card.Month.July, Card.Yaku.To, Card.Yaku.Chi, false, null, 5));
		cardDeck.add (new Card (26, "July2_0pts", Card.Month.July, null, null, false, null, 0));
		cardDeck.add (new Card (27, "July3_0pts", Card.Month.July, null, null, false, null, 0));
		cardDeck.add (new Card (28, "August0_20pts_I", Card.Month.August, Card.Yaku.I, null, false, null, 20));
		cardDeck.add (new Card (29, "August1_5pts_Chi", Card.Month.August, Card.Yaku.Chi, null, false, null, 5));
		cardDeck.add (new Card (30, "August2_0pts", Card.Month.August, null, null, false, null, 0));
		cardDeck.add (new Card (31, "August3_0pts", Card.Month.August, null, null, false, null, 0));
		cardDeck.add (new Card (33, "September0_10pts_Ni", Card.Month.September, Card.Yaku.Ni, null, false, null, 10));
		cardDeck.add (new Card (32, "September1_5pts_I", Card.Month.September, Card.Yaku.I, Card.Yaku.Ho, false, null, 5));
		cardDeck.add (new Card (34, "September2_0pts", Card.Month.September, null, null, false, null, 0));
		cardDeck.add (new Card (35, "September3_0pts", Card.Month.September, null, null, false, null, 0));
		cardDeck.add (new Card (37, "October0_10pts_Ni", Card.Month.October, Card.Yaku.Ni, null, false, null, 10));
		cardDeck.add (new Card (36, "October1_5pts_Ho", Card.Month.October, Card.Yaku.Ho, Card.Yaku.Chi, false, null, 5));
		cardDeck.add (new Card (38, "October2_0pts", Card.Month.October, null, null, false, null, 0));
		cardDeck.add (new Card (39, "October3_0pts", Card.Month.October, null, null, false, null, 0));
		cardDeck.add (new Card (42, "November0_10pts", Card.Month.November, null, null, false, null, 10));
		cardDeck.add (new Card (40, "November1_5pts", Card.Month.November, null, null, false, null, 5));
		cardDeck.add (new Card (41, "November2_5pts", Card.Month.November, null, null, false, null, 5));
		cardDeck.add (new Card (43, "November3_Gaji", Card.Month.November, null, null, true, null, 0));
		cardDeck.add (new Card (44, "December0_20pts", Card.Month.December, null, null, false, null, 20));
		cardDeck.add (new Card (45, "December1_10pts", Card.Month.December, null, null, false, null, 10));
		cardDeck.add (new Card (46, "December2_0pts", Card.Month.December, null, null, false, null, 0));
		cardDeck.add (new Card (47, "December3_0pts", Card.Month.December, null, null, false, null, 0));
	}
	private void initializeCardImages(){ // added by me to initialize images
		for(int i=0;i<cardDeck.size();i++){
			(cardDeck.get(i)).setImage(MyAssetHandler.getIcon(i));
		}
		
	}
	
}
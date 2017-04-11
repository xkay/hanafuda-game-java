package com.usc.hanafuda.handlers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class MyAssetHandler {
	public final static int SIZE = 48;
	public final static int ROW = 12;
	public final static int COL = 4;
	public final static int WIDTH = 85;
	public final static int HEIGHT = 140;


	public static BufferedImage[] cardImageArray = new BufferedImage[SIZE];
	public static ImageIcon[] cardIconArray = new ImageIcon[SIZE];

	public static BufferedImage deckImage;


	public static void load () {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("all.png"));

			//DEBUG
			//System.out.println("load + cardImage.jpg");

			deckImage = ImageIO.read(new File("deck.png"));

			for(int i = 0; i < ROW; i++){
				for(int j = 0; j < COL ; j++){
				//cardImageArray[i] = new BufferedImage(500, 500, image.getType());
					//System.out.println(i + "," + j);
				cardImageArray[i*COL+j] = image.getSubimage(j * WIDTH, i * HEIGHT, WIDTH, HEIGHT);
				cardIconArray[i*COL+j] = new ImageIcon(cardImageArray[i*COL+j]);

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ImageIcon getIcon(int i) {
		//DEBUG
		//System.out.println("image at " + i);
		return cardIconArray[i];
	}

}

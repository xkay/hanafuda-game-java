package com.usc.hanafuda.entities;



import java.io.Serializable;

import javax.swing.ImageIcon;

public class Card implements Serializable {
	enum Month {
		January, February, March, April, May, June,
		July, August, September, October, November, December
	}

	public enum Yaku {
		I, Ro, Ha, Ni, Ho, He, To, Chi
	}

	private String name;
	private Month month;
	private Yaku yaku1, yaku2;
	private int value;
	private boolean gaji;
	private Month gajiMonth;
	private ImageIcon image;
    private int id;
	public Card (int id, String n, Month m, Yaku y1, Yaku y2, boolean g, ImageIcon i, int v) {
        this.id = id;
		this.name = n;
		this.month = m;
		this.yaku1 = y1;
		this.yaku2 = y2;
		this.gaji = g;
		this.image = i;
		this.value = v;
	}

	//DEBUG
	public void printName() {
		System.out.println(name);
	}

	public void setImage(ImageIcon i){
		this.image =i;
	}
	public ImageIcon getImage() {
		return image;
	}

	public int getValue() {
		return value;
	}

    public int getId() {
		return id;
	}

	public Month getMonth() {
		return month;
	}

	public Yaku getYaku1() {
		return yaku1;
	}

	public Yaku getYaku2() {
		return yaku2;
	}

	public String getName() {
		return name;
	}

	public boolean isGaji() {
		return gaji;
	}

	public void setGajiMonth (Month m) {
		gajiMonth = m;
	}

	public Month getGajiMonth () {
		return gajiMonth;
	}

	public boolean isMatch(Card cd) {
		if (this.month == cd.getMonth() || this.gaji == true) {
				return true;
		}
		else {
			return false;
		}
	}
}

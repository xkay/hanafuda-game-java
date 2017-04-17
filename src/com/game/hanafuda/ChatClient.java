package com.usc.hanafuda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class ChatClient extends Thread {

	private PrintWriter pw;
	private BufferedReader br;
	//private Scanner scan;
	private MyGame myGame;

	public ChatClient (String hostname, int port, MyGame g){
		try {
			Socket s=new Socket(hostname, port);
			this.pw=new PrintWriter(s.getOutputStream());
			this.br=new BufferedReader(new InputStreamReader(s.getInputStream()));
			this.myGame = g;
			this.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void sendMessage (String msg) {
		pw.println(msg);
		pw.flush();
	}


	public void run() {
		try {
			while(true){
			String line = br.readLine();
			myGame.getGameScreen().enterNewMessage(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

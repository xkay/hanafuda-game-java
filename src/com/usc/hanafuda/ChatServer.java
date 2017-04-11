package com.usc.hanafuda;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;


public class ChatServer {

	private Vector<ChatThread> ctVector=new Vector<ChatThread>();


	public ChatServer(int port){
		try {
			ServerSocket ss=new ServerSocket(port);

			while(true){
				Socket s=ss.accept();
				ChatThread ct=new ChatThread(s,this);
				ctVector.add(ct);
				ct.start();
			}

		}

		catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void sendMessage(String message, ChatThread ct){
		for(ChatThread c: ctVector) {
			if(!c.equals(ct)){
				c.send(message);
			}
		}

	}

	public void removeClient(ChatThread ct){
		ctVector.remove(ct);
	}

	public static void main(String[] args){
		new ChatServer(7777);
	}

}

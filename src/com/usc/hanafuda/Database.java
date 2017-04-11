package com.usc.hanafuda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	public Database (String player1, String player2, int score1, int score2) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection myConnection = DriverManager.getConnection("jdbc:mysql://localhost/hanafuda_schema", "root", "SoftwareDeveloper12");
			
			String query = "INSERT INTO userscorelist (name, score)" + "\r\n" + "VALUES (?, ?)";
			
			PreparedStatement myPreparedStatement = myConnection.prepareStatement(query);
				
			myPreparedStatement.setString (1, player1);
			myPreparedStatement.setString (2, Integer.toString(score1));
			myPreparedStatement.execute();
			
			myPreparedStatement.setString (1, player2);
			myPreparedStatement.setString (2, Integer.toString(score2));
			myPreparedStatement.execute();
			
			String selectQuery = "SELECT * FROM userscorelist;";
			
			Statement myStatement = myConnection.createStatement();
			ResultSet result = myStatement.executeQuery (selectQuery);
			
			while (result.next()) {
				//System.out.println (result.getString(1) + " " + result.getString(2) + " " + result.getString(3));
			}
		} catch (ClassNotFoundException e) {
			System.out.println (e.getMessage());
		} catch (SQLException e) {
			System.out.println (e.getMessage());
		}
	}
	
	public static void main (String [] args) {
		new Database ("P1", "P2", 25, 50);
	}
}

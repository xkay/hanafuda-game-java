public class Player extends HClient {
	public Player (String hostname, int port) {
		super (hostname, port);
	}
	
	//This method sends a message to the server to signal the start of the game
	//Will be called most likely by a button listener
	//Uses HClient.sendMessage()
	public void notifyServerStart () {
		System.out.println ("Sending notification to server to start game");
		//sendMessage ("Signal:StartGame");
	}
}

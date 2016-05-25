import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class ServerThread extends Thread{
	private Socket s;
	private HServer hs;
	private PrintWriter pw;
	private ObjectOutputStream os;
	private ObjectInputStream is;
	
	public ServerThread(Socket s, HServer hs){
		this.s=s;
		this.hs=hs;
		try {
			
			this.pw=new PrintWriter(s.getOutputStream());
			this.os=new ObjectOutputStream(s.getOutputStream());
			this.is=new ObjectInputStream(s.getInputStream());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void sendCard(Card cd){
		try {
			
			pw.println("Signal:SendCard");
			pw.flush();
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			os.reset();
			os.writeObject(cd);
			os.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void send(String message){
		pw.println(message);
		pw.flush();
		
	}
	
	public void run (){
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));
			while(true){
				String line=br.readLine();
				//nothing more to read, close the socket
				if(line==null){
					s.close();
					hs.removeDisconnection(this);
					break;
				}
				//
				if(line.equals("Signal:StartGame")){
					
					hs.onStartOfGame();
					
				}
				
				
				if(line.equals("Signal:SendField")){
					
					//read one more line message to confirm the receiving card process
					
						String nextMessage=br.readLine();
						
						if(nextMessage.equals("Signal:SendCard")){
						
						try {
							
							Card received = (Card) is.readObject();
							//add to field
							hs.Field.add(received);
							//for test
							System.out.println("Field Card received");
							received.printName();
							System.out.println("I have <"+hs.Field.size()+"> cards in Field");
							
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
				
				if(line.equals("Signal:SendCollection")){
					
					//read one more line message to confirm the receiving card process
					
						String nextMessage=br.readLine();
						
						if(nextMessage.equals("Signal:SendCard")){
						
						try {
							
							Card received = (Card) is.readObject();
							//add to field
							hs.Collection.add(received);
							//for test
							System.out.println("Field Card received");
							received.printName();
							System.out.println("I have <"+hs.Collection.size()+"> cards in Collection");
							
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
				
				
				if(line.equals("Signal:UpdateField")){
					
					hs.Field.clear();
					
				}
				
				if(line.equals("Signal:UpdateCollection")){
					
					hs.Collection.clear();
					
				}
				
				if(line.equals("Signal:UpdateFieldFinished")){
					
					hs.updateField();
					
				}
				
				if(line.equals("Signal:GetCardFromDeck")){
					
					hs.sendCardFromDeck();
					
				}
				
				if(line.equals("Signal:EndTurn")){
					
					
					
					
				}
				
				
				System.out.println("received message: "+line);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

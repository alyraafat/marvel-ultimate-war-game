package network;
import java.net.*;
import java.io.*;

public class Client {
	private Socket socket = null;
	private DataInputStream input = null;
	private DataOutputStream out = null;
	public Client(String address,int port) {
		try{
			socket = new Socket(address,port);
			System.out.println("Connected");
			input = new DataInputStream(System.in);
			out = new DataOutputStream(socket.getOutputStream());
		}
		catch(UnknownHostException u) {
			System.out.println(u);
		} catch (IOException e) {
			System.out.println(e);
		}
		String line = "";
		while(!line.equals("Over")) {
			try {
				line = input.readLine();
				out.writeUTF(line);
			}
			catch(IOException e) {
				System.out.println(e);
			}	
		}
		try {
			input.close();
			out.close();
			socket.close();
		}catch(IOException e) {
			System.out.println(e);
		}	
	}
	public static void main(String args[]) {
		
	}
}
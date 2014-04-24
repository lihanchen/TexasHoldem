package Network;
import java.io.*;
import java.net.*;

import GUI.GUI;
import java.util.*;

public class ClientMessageHandler {
	
	ObjectOutputStream oos=null;
	ObjectInputStream ois=null;
	Socket socket=null;
	//Object receivedObj=null;
	Deque<Object> receivedObjs = new ArrayDeque<Object>();
	int ChildIndex=-1;
	ReceivingThread receivingThread;
	
	
	public static class NameTakenException extends Exception {
		public NameTakenException(String msg) {
			super(msg);
		}
	}
	
	public static class LobbyFullException extends Exception {
		public LobbyFullException(String msg) {
			super(msg);
		}
	}
	
	/* 
	 * Constructor 
	 * Used to create ClientMessageHandler
	 * Implement socket 
	 * Connect to a specific port listened by HostMessageHandler
	 * Get a pair of streams in socket
	 **/
	public ClientMessageHandler(InetAddress IP, int port, String playerName)
			throws IOException, ClassNotFoundException, NameTakenException, LobbyFullException{

		socket = new Socket();
		socket.connect(new InetSocketAddress(IP, port), 1500);
		
		// send player name to host, await boolean reply to see if name
		// is ok
		DataInputStream dis=new DataInputStream(socket.getInputStream());
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		dos.writeUTF(playerName);
		dos.flush();
		boolean lobbyHasSpace = dis.readBoolean();
		boolean playerNameOk = dis.readBoolean();
		if (!lobbyHasSpace) {
			socket.close();
			throw new LobbyFullException("lobby full");
		}
		if (!playerNameOk) {
			socket.close();
			throw new NameTakenException("Name "+playerName+" is being used by another client.");
		}
				
		// get object i/o streams of socket
		oos=new ObjectOutputStream(socket.getOutputStream());
		ois=new ObjectInputStream(socket.getInputStream());
		
		receivingThread = new ReceivingThread();
		receivingThread.start();
	}	
	
	
	public Object getReceivedObject() {
		if (receivedObjs.isEmpty())
			return null;
		else{
			return receivedObjs.removeFirst();
		}
	}
	
	
	/*
	 * Call this function will
	 * send UserAction object 
	 * through oos stream to host message handler
	 * */
	public synchronized void send(Object ob) throws IOException{
		oos.writeObject(ob);
		oos.flush();
	}
	
	/*
	 * Call this function will make
	 * current socket close
	 * */
	public void close(){
		receivingThread.enable = false;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Inner class thread
	 * continuously receive game state through socket stream
	 * show game state when there is one
	 * run when client message handler is created
	 * */
	class ReceivingThread extends Thread{
		public boolean enable = true;
		public void run(){
			while(enable){
				try{
					Object receivedObj=ois.readObject();
					receivedObjs.addLast(receivedObj);
					System.out.println("Receive a game state from host\n\t: "+receivedObj);
				}catch(IOException | ClassNotFoundException e){
					System.out.println("Lost connection to host!");
					e.printStackTrace();
					if (enable) {
						GUI.hostConnectionError_flag = true;
					}
					break;
				}
			}
		}
	}

}	

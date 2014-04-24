package Network;
import java.io.*;
import java.net.*;
import java.util.*;


public class HostMessageHandler {
	
	private class ClientConnection {
		private ObjectOutputStream oos;
		private ObjectInputStream ois;
		private ReceivingThread rt;
		private ClientConnection(ObjectOutputStream oos, ObjectInputStream ois,
				ReceivingThread rt) {
			this.oos = oos;
			this.ois = ois;
			this.rt = rt;
		}
		private void close() {
			rt.enable = false;
			try {
				ois.close();
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String lastJoinedPlayer;
	
	private Map<String, ClientConnection> clientConnections;
	
	private Host.Host host;
	private ServerSocket server=null;
	private int port;
	protected String allowedPlayer=null;
	protected volatile Timer nowTimer;
	private Listening listeningThread = null;
	private volatile boolean blocking;
	
	private boolean inLobbyMode;
	
	/*
	 * Constructor 
	 * Used to create HostMessageHandler
	 * Implement socket server
	 * listen on specific port
	 * create arrays to store multiple streams.
	 * */
	public HostMessageHandler(int port, Host.Host host, Thread hostThread){
		this.port=port;
		this.host=host;
		allowedPlayer=null;
		blocking=false;
		inLobbyMode = true;
		
		try{
			server=new ServerSocket(port);
			System.out.println("Host is Listening on port ["+port+"] Waiting for client to connect...");
		}catch(IOException e){
			System.out.println("Cannot listen on port");
			e.printStackTrace();
			System.exit(0);
		}
		
		clientConnections = new HashMap<String, ClientConnection>();
		listeningThread = new Listening();
		listeningThread.start();
	}
	
	
	// stop all threads
	public void close() {
		if (listeningThread != null) {
			listeningThread.enable = false;
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (ClientConnection cc : clientConnections.values()) {
			cc.close();
		}
		clientConnections.clear();
	}
	
	
	/* DEAD CONNECTIONS AUTOMATICALLY REMOVED WHEN SOCKET CLOSES
	// remove dead connections, returns true if a dead connection was found
	public boolean removeDeadConnections() {
		boolean clientDisconnected = false;
		Iterator<Map.Entry<String, ClientConnection>> iter = clientConnections.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, ClientConnection> entry = iter.next();
			if (!entry.getValue().rt.isAlive()) {
				iter.remove();
				clientDisconnected = true;
			}
		}
		return clientDisconnected;
	}
	*/
	
	public int getNumConnectedPlayers() {
		int ret;
		synchronized (clientConnections) {
			ret = clientConnections.size();
		}
		return ret;
	}
	
	// check if a player's connection is still alive
	public boolean isConnected(String playerName) {
		boolean ret;
		synchronized (clientConnections) {
			ret = clientConnections.containsKey(playerName);
		}
		return ret;
	}
	

	
	public void gameStart(){
		blocking=true;
		inLobbyMode = false;
	}
	
	public void gameEnd(){
		blocking=false;
		inLobbyMode = true;
	}
	
	/*
	 * Inner class
	 * when new client is connected to server.
	 * return socket and create new streams in arrays
	 * start receiving thread
	 * */
	public class Listening extends Thread{
		
		boolean enable = true;
		
		public void run(){
			Socket socket;
			while(enable){
				try{
					
					socket=server.accept();
					
					// check if the lobby is already full
					boolean lobbyHasSpace = true;
					synchronized (host.players){ 
						if (host.playerCount >= host.players.length) {
							lobbyHasSpace = false;
						}
					}
					
					// wait for client to send player name, reply whether or not
					// the name is ok
					DataInputStream dis = new DataInputStream(socket.getInputStream());
					DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
					String playerName = dis.readUTF();
					boolean playerNameOk = !clientConnections.containsKey(playerName);
					dos.writeBoolean(lobbyHasSpace);
					dos.writeBoolean(playerNameOk);
					dos.flush();
					
					if (!playerNameOk || !lobbyHasSpace) {
						socket.close();
						continue;
					}
					ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					ReceivingThread rt = new ReceivingThread(playerName, ois);
					
					lastJoinedPlayer = playerName;
					
					clientConnections.put(playerName, new ClientConnection(oos, ois, rt));
					
					rt.start();
					
					System.out.println(socket.getInetAddress().getHostAddress()+" is connected to the port ["
							+port+"] as client "+playerName);
					
					
					// update host players list
					synchronized (host.players) {
						// find first empty spot in host.players
						int i;
						for (i=0; i<host.players.length; i++) {
							if (host.players[i]==null)
								break;
						}
						host.players[i] = playerName;
						host.playerCount++;
						sendAll(host.players.clone());
					}

					
				}catch(NullPointerException e){
					System.out.println("Cannot listen on port listening()");
					e.printStackTrace();
					break;
				}catch(Exception e){
				}
			}
		}
	}
	
	/*
	 * Inner class 
	 * receiving thread
	 * continuously receive UserAction through socket stream
	 * show action when there is one
	 * */
	class ReceivingThread extends Thread{
		String playerName;
		ObjectInputStream myois;
		boolean enable;
		public ReceivingThread(String playerName, ObjectInputStream ois) {
			this.playerName = playerName;
			this.myois = ois;
			enable = true;
		}
		public void run(){
			
			while(enable){
				try{
					Object ac;
					ac=myois.readObject();
					if (blocking==true){
						if (playerName!=allowedPlayer) continue;
						nowTimer.cancel();
						System.out.println("TIMER!!! cancel timer "+allowedPlayer+System.currentTimeMillis()/1000);
						allowedPlayer=null;
					}
					host.objReceived=ac;
					if (host.isWaiting) {
						synchronized(host){
							host.isWaiting = false;
							host.objSender = playerName;
							host.notify();
						}
					}
					System.out.println("Host receives an object from Client "+playerName);
				}catch(IOException | ClassNotFoundException e){
					System.out.println("Session end for client "+playerName);
					
					synchronized (clientConnections) {
						clientConnections.remove(playerName);
					}
					
					if (inLobbyMode) {
						// remove player's name from host playerlist
						synchronized (host.players) { 
							for (int i=0; i<host.players.length; i++) {
								if (host.players[i]!=null && host.players[i].equals(playerName)) {
									host.players[i] = null;
									host.playerCount--;
								}
							}
							sendAll(host.players.clone());
						}
					}
					break;
				}
			}
		}
	}
	
	
	class autoResponse extends TimerTask{
		public void run() {
			nowTimer.cancel();
			UserAction ac=new UserAction(UserAction.Action.FOLD,0);
			host.objReceived=ac;
			System.out.println("TIMER!!! auto fold for "+allowedPlayer+System.currentTimeMillis()/1000);
			if (host.isWaiting) {
				synchronized(host){
					host.isWaiting = false;
					host.objSender = allowedPlayer;
					allowedPlayer=null;
					host.notify();
				}
			}	
		}
		
	}
	
	
	
	// call this function will send game state to specific client,
	// which are arguments
	public synchronized void send(String playerName, Object ob){
		if (blocking==true && ob instanceof GameState.Gamestate){
			int turn=((GameState.Gamestate)ob).whoseTurn;
			if (turn>=0){ 
				allowedPlayer=host.players[turn];
				if (playerName==allowedPlayer){
					nowTimer=new Timer();
					System.out.println("TIMER!!!start timer for "+allowedPlayer+System.currentTimeMillis()/1000);
					nowTimer.schedule(new autoResponse(), 25000);
				}
			}
		}
		ObjectOutputStream oos= clientConnections.get(playerName).oos;
		try{
			oos.writeObject(ob);
			oos.flush();
		}catch(IOException e){
			System.out.println("Cannot send object: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	// call this function will send game state to all clients
	public synchronized void sendAll(Object ob){
		for (String playerName : clientConnections.keySet())
			send(playerName, ob);
	}
	
}

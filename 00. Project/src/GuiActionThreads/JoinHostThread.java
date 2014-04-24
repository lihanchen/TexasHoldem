package GuiActionThreads;

import java.io.IOException;
import java.net.InetAddress;

import GUI.GUI;
import Network.ClientMessageHandler;
import Network.ClientMessageHandler.LobbyFullException;
import Network.ClientMessageHandler.NameTakenException;

public class JoinHostThread extends Thread {
	
	String hostIp;
	String playerName;
	
	public JoinHostThread(String hostIp, String playerName) {
		this.hostIp = hostIp;
		this.playerName = playerName;
	}
	
	@Override
	public void run() {
		
		GUI.joinMode.joinHostSuccess_flag = false;
		GUI.joinMode.joinHostLobbyFull_flag = false;
		GUI.joinMode.joinHostNameTaken_flag = false;
		GUI.joinMode.joinHostError_flag = false;
		
		try {
			// connect to host, send playername
			GUI.cmh = new ClientMessageHandler(
					InetAddress.getByName(hostIp), 4321, playerName);
			GUI.joinMode.joinHostSuccess_flag = true;
		} catch (LobbyFullException e) {
			GUI.cmh = null;
			GUI.joinMode.joinHostLobbyFull_flag = true;
		} catch (NameTakenException e) {
			GUI.cmh = null;
			GUI.joinMode.joinHostNameTaken_flag = true;
		} catch (IOException | ClassNotFoundException e) {
			// failed to join
			GUI.cmh = null;
			GUI.joinMode.joinHostError_flag = true;
		}	
	}
}

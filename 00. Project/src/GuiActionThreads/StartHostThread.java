package GuiActionThreads;

import java.io.*;
import java.net.*;

import GUI.GUI;
import Host.Host;
import Network.ClientMessageHandler;
import Network.ClientMessageHandler.LobbyFullException;
import Network.ClientMessageHandler.NameTakenException;

public class StartHostThread extends Thread {
	
	private String playerName;
	
	public StartHostThread(String playerName) {
		this.playerName = playerName;
	}
	
	
	@Override
	public void run() {
		
		GUI.startMode.startHostSuccess_flag = false;
		GUI.startMode.startHostError_flag = false;
		
		// start host process
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		String className = Host.class.getCanonicalName();
		
		ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);
		Process p;
		try {
			p = builder.start();
		} catch (IOException e2) {
			//e2.printStackTrace();
			GUI.startMode.startHostError_flag = true;
			return;
		}
		
		
		// wait some time to allow host process to start
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			//e1.printStackTrace();
			GUI.startMode.startHostError_flag = true;
			return;
		}
		
		
		// check if host process is still running
		try {
			p.exitValue();
			GUI.startMode.startHostError_flag = true;
			return;
		} catch (IllegalThreadStateException e) {
		}
		
					
		// connect to the newly-created host process
		// if fail, destroy host process
		InetAddress myIP = null;
		try {
			myIP = InetAddress.getLocalHost();
			GUI.cmh = new ClientMessageHandler(myIP, 4321, playerName);
			GUI.startMode.startHostSuccess_flag = true;
		} catch (IOException | ClassNotFoundException | NameTakenException | LobbyFullException e) {
			//e.printStackTrace();
			p.destroy();
			GUI.startMode.startHostError_flag = true;
			return;
		}
		
		// TEST: read output from invoked host process
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		try {
			while ( (line = br.readLine()) != null) {
			   System.out.println("\tINVOKED_PROC: "+line);	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

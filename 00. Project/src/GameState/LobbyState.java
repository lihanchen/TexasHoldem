package GameState;

import java.io.Serializable;

public class LobbyState implements Serializable{
	public int numOfPlayer;
	public String hostName;
	
	public LobbyState(int numOfPlayer){
		this.numOfPlayer=numOfPlayer;
		hostName="";
	}
	
	public LobbyState(String hostNickName){
		numOfPlayer=1;
		hostName=hostNickName;
	}
}

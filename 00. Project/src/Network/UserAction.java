package Network;

import java.io.Serializable;

public class UserAction implements Serializable{

	public enum Action{FOLD, CHECK, CALL, BET, RAISE, ALL_IN};
	public Action action;
	public int raiseAmount;

	//private String msg;
	public UserAction(Action action, int raiseAmount){
		this.action = action;
		this.raiseAmount = raiseAmount;
	}
	
	
	public String toString(){
		StringBuffer s=new StringBuffer();
		s.append("User Action: send [ "+action.name()+" raise "+raiseAmount+" ] !");
		return s.toString();
	}
}


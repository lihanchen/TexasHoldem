package Host.GameSystem;

import java.io.Serializable;

import Network.UserAction;


public class Player implements Serializable {

	public Card hand[];
	public int totalChip;
	public int betAmount;
	
	public UserAction latestAction;
	
	public boolean hasFolded, hasLeft;
	public int count;

	//Constructor
	public Player() {
		totalChip = GameSystem.INIT_CHIP;
		hasLeft = false;
		betAmount = 0;
	}
	
	// copy constructor
	public Player(Player player) {
		
		hand = new Card[2];
		for (int i=0; i<2; i++) {
			hand[i] = new Card(player.hand[i].getKind(), player.hand[i].getNumber());
		}
		totalChip = player.totalChip;
		betAmount = player.betAmount;
		
		if (player.latestAction==null)
			latestAction = null;
		else
			latestAction = new UserAction(player.latestAction.action, player.latestAction.raiseAmount);
		
		hasFolded = player.hasFolded;
		hasLeft = player.hasLeft;
		count = player.count;
	}
	
	//Checks if the player went all in.
	public boolean isAllIn() {
		if(totalChip == 0)	return true;
		return false;
	}	 
	
	//the player bets the betAmount.
	//**parameter betAmount IS Cumulative** 
	//If the bet is higher than players totalChip, return false;
	//If successfully bet, return true;
	public boolean bet(int betAmount) {
		
		betAmount -= this.betAmount;
		
		if(betAmount > totalChip){
			this.betAmount += totalChip;
			totalChip = 0;
			latestAction = new UserAction(UserAction.Action.ALL_IN, this.betAmount);
			return false;
		}
		
		this.betAmount += betAmount;
		totalChip -= betAmount;
		
		return true;
	}
	
	public void dealHands(Card hand[]){
		this.hand = hand;
		hasFolded = false;
	}
	public Card[] player_hand(Card hand[]){
		this.hand = hand;
		return hand;
		
	}
	//setter
	public void fold(){ hasFolded = true; }
	
	public String toString()
	{
		String str = new String();
		
		if(hand == null) str += "No hand is dealt yet\n";
		else str += ("Hand       : "+hand[0] + "    " + hand[1] + "\n");
		
		str += "Total Chip : " + totalChip;
		str += "\nBet Amount : " + betAmount + "\n";
		
		if(hasFolded)	str += "Has Folded\n";
		if(hasLeft)		str += "Has Left\n\n";
		
		return str;
	}
}
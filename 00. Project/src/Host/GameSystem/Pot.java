package Host.GameSystem;

import java.io.Serializable;

import Host.*;

public class Pot implements Serializable  {

	public Pot splitPot;
	public int totalPot;
	public boolean playerInvolved[];
	
	public boolean winner[];
	public int winnerRank;
	
	//special case
	public int winnerByFold;

	//Constructor
	public Pot() {
		totalPot = 0;
		splitPot = null;
		winnerByFold = -1;
		
		playerInvolved = new boolean[GameSystem.MAXPLAYER];
		for(int i=0; i< GameSystem.MAXPLAYER; i++)	playerInvolved[i] = true;
	}
	public Pot(int leftover) {
		totalPot = leftover;
		splitPot = null;
		winnerByFold = -1;
		
		playerInvolved = new boolean[GameSystem.MAXPLAYER];
		for(int i=0; i< GameSystem.MAXPLAYER; i++)	playerInvolved[i] = true;
	}
	public Pot(int leftover, Player[] player) {
		totalPot = leftover;
		splitPot = null;
		winnerByFold = -1;
		
		playerInvolved = new boolean[GameSystem.MAXPLAYER];
		for(int i=0; i< GameSystem.MAXPLAYER; i++)
			if(player[i] != null)	playerInvolved[i] = true;
			else					playerInvolved[i] = false;
	}
	
	
	//copy constructor
	public Pot(Pot pot) {
		totalPot = pot.totalPot;
		winnerByFold = pot.winnerByFold;
		winnerRank = pot.winnerRank;
		
		playerInvolved = new boolean[pot.playerInvolved.length];
		for (int i=0; i<GameSystem.MAXPLAYER; i++)
			playerInvolved[i] = pot.playerInvolved[i];
		
		if(pot.winner!=null){
			winner = new boolean[GameSystem.MAXPLAYER];
			for(int i=0; i<GameSystem.MAXPLAYER; i++)
				winner[i] = pot.winner[i];
		}
		
		if (pot.splitPot!=null)
			splitPot = new Pot(pot.splitPot);
		else
			splitPot = null;
	}
	
	public void fold(int playerIndex){
		if(splitPot != null)	splitPot.fold(playerIndex);
		
		playerInvolved[playerIndex] = false;
	}

	//gathersPots at the end of each round
	//parameter player[] is the array of players. 
	public void gatherPots(Player player[], int highestBet) {
		
		if(splitPot != null)	splitPot.gatherPots(player, highestBet);
		
		else {
			//check for someone who are not involved in the current pot
			for(int i=0; i<player.length; i++) {
				if(player[i] != null){
					if(player[i].betAmount == 0 && highestBet > 0) {
						playerInvolved[i] = false;
					}
					if(player[i].hasFolded)
						playerInvolved[i] = false;
				}
				else{
					playerInvolved[i] = false;
				}
			}
				
			if(winnerByFold == -1){
				//check if someone went all in, and find the lowest all in
				int lowestBet = GameSystem.INIT_CHIP*8 + 1; 
				for(int i=0; i<player.length; i++)
				{
					if(player[i] != null && !player[i].hasFolded){
						if(player[i].betAmount > 0 && highestBet > player[i].betAmount){
							if(lowestBet > player[i].betAmount) lowestBet = player[i].betAmount;
						}
					}
				}
				//if someone went all in.....
				if(lowestBet != (GameSystem.INIT_CHIP*8 + 1))
				{
					//subtract the all in amount and add it to this totalPot; Then, splitPot the rest;
					for(int i=0; i<player.length; i++)
					{
						if(player[i] != null){
							if(player[i].betAmount > lowestBet){
								totalPot += lowestBet;
								player[i].betAmount -= lowestBet;
							}
							else {
								totalPot += player[i].betAmount;
								player[i].betAmount = 0;
							}
						}
					}
					//splitPot the rest;
					splitPot = new Pot(0, player);
					splitPot.gatherPots(player, highestBet-lowestBet);
					
					return;
				}
			}
				
			//if it gets to this point its normal pot (no all ins)
			//save the total pot
			for(int i=0; i<player.length; i++)
			{
				if(player[i] != null){
					totalPot += player[i].betAmount;
					player[i].betAmount = 0;
				}
			}
		}
	}

	public void potToWinner(GameSystem game)
	{
		if(splitPot != null)	splitPot.potToWinner(game);
		
		Card hands[][] = new Card[GameSystem.MAXPLAYER][2];
		
		for(int i=0; i<GameSystem.MAXPLAYER; i++){
			if(playerInvolved[i])
				hands[i] = game.player[i].hand;
			else
				hands[i] = null;
		}
		
		winner = (new Rank()).findWinner(this, game.flops, hands);
		int winnerCount = 0;
		
		totalPot += game.leftover;
		
		for(int i=0; i<GameSystem.MAXPLAYER; i++){
			if(winner[i]) winnerCount++;
		}
		for(int i=0; i<GameSystem.MAXPLAYER; i++)
		{
			if(winner[i]){
				game.player[i].totalChip += (int) (totalPot/winnerCount);
			}
		}
		game.leftover = totalPot%winnerCount;
	}
	
	public Pot getCurrentPot() {
		if(splitPot == null)	return this;
		else					return splitPot.getCurrentPot();
	}
	public int countPots(int n){
		if(splitPot == null)	return n;
		else					return splitPot.countPots(n+1);
	}
	public void printPot()
	{
		if(splitPot != null)	splitPot.printPot();
		
		System.out.println("totalPot : " + totalPot);
		System.out.print("Splitted to : Player ");
		for(int i=0; i<GameSystem.MAXPLAYER; i++){
			if(playerInvolved[i])	System.out.print(i + " ");
		}
		System.out.println("\n");
	}
}

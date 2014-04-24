package Host.Test;

import java.util.*;

import GameState.*;
import Host.GameSystem.GameSystem;
import Host.GameSystem.Player;
import Host.GameSystem.Pot;
import Network.*;

import java.util.Random;



//Host will do the followings:
//1. create host network
//2. wait for it to start
//3. start the game
//   - has all the flows here
//   - uses methods in GameSystem
public class HostTestInText {
	
	public GameSystem game;
	public Player player;
	public boolean last_standing=false;
	
	public int playerCount;
	public int playerLeftCount;
	public String players[];
		
	public HostTestInText(){
		playerCount = 4;
	}
					
	/************** Game Flow **************/
	//each game
	//1. players, table created
	//
	//each hand
	//1. new deck, new card
	//2. rank at the end
	//3. small/big blind
	//each round
	//1. pot
	//
	//each turn
	//1. turn change
	//2. player action
	public void startGame(){
		Random generator = new Random();
		//each game
		game = new GameSystem(playerCount);
		
		//TEST TEST TEST// V_01
//		System.out.println("before the game start");
//		for(int i=0; i<GameSystem.MAXPLAYER; i++)
//			if(game.player[i] != null)	System.out.println("Player "+i+":\n" + game.player[i]);
//			else						System.out.println("Player "+i+" Does not exist.");
		//TEST TEST TEST// V_01
		
		//each hand
		while(game.playerCount() > 1){
			game.newHand();
			int player_count=1;
			System.out.println("player count = " + game.playerCount());
			//TEST TEST TEST// V_01
//			System.out.println("\nwhen the hand is dealt");
//			for(int i=0; i<GameSystem.MAXPLAYER; i++)
//				if(game.player[i] != null)	System.out.println("Player "+i+":\n" + game.player[i]);
//				else						System.out.println("Player "+i+" Does not exist.");
//			System.out.println("Flops : ");
//			for(int i=0; i<5; i++)	System.out.println(game.flops[i]);
			//TEST TEST TEST// V_01
			
			//each round
			for(int i=0; i<4; i++){
				game.flopState = i;
				game.highestBetter = game.nextTurn();
				
				//TEST TEST TEST// V_01
//				System.out.println("Flop state : " + game.flopState);
				//TEST TEST TEST// V_01

				//each turn
				do{					
					//TEST V_02!!//
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nFlops :");
					
					if(game.flopState == 0)	System.out.println("-");
					else if(game.flopState == 1)	for(int k=0; k<3; k++)	System.out.println(game.flops[k]);
					else if(game.flopState == 2)	for(int k=0; k<4; k++)	System.out.println(game.flops[k]);
					else if(game.flopState == 3)	for(int k=0; k<5; k++)	System.out.println(game.flops[k]);
					
					System.out.println();
					int j=0;
					for(int k=0; k<GameSystem.MAXPLAYER; k++){
						if(game.player[k] != null && game.whoseTurn!=-1 ){
							if(game.dealer == k)	System.out.println("***Dealer***");
							if(game.whoseTurn == k)	System.out.println("---Your Turn---");
							System.out.println("Player "+k+":\n" + game.player[k]);
							j=k;
						}
					}
					
					if(game.whoseTurn==-1||player_count+1 == (game.playerCount())){
						//game.whoseTurn = k;
						game.whoseTurn=j;
						break;
					}
					System.out.println("It's player " + game.whoseTurn +"'s turn!");
					System.out.print("Fold=0 Call=1 Bet=2\n:");
				
					//Scanner s = new Scanner(System.in);
					//int input = s.nextInt();
					//random input generation
					int input = generator.nextInt(3);
				
					System.out.println(input);
					if(game.player[game.whoseTurn].totalChip<game.highestBet&&game.player[game.whoseTurn].totalChip==0){
						input=1;
					}
			
				
					if(input == 0){
						if(player_count+1 == (game.playerCount())){
							System.out.println("player count = "+player_count);
							System.out.println("player count 2  = "+game.playerCount());
							last_standing = true;
							//if every1 folds the hand ends
							//
						}
						if(last_standing==false){
							game.player[game.whoseTurn].fold();
							player_count++;
						}
					}
					else if(input == 1)	game.player[game.whoseTurn].bet(game.highestBet);
					else if(input == 2){
						System.out.print("How much you want to bet? :");
						int whileloop=0;
						
						while(whileloop==0){
							input = generator.nextInt(1000);
							if(game.player[game.whoseTurn].totalChip<game.highestBet||game.player[game.whoseTurn].totalChip<=20){
								
								
									input=game.player[game.whoseTurn].totalChip;
									whileloop=2;
								
							}
							else if(input>=20&&input>=game.highestBet&&input<=1000&&input<game.player[game.whoseTurn].totalChip){
								whileloop=1;
							}
							else{
								
							}
						}
						System.out.println(input);
						game.player[game.whoseTurn].bet(input);
						if(whileloop == 1){
							game.highestBetter = game.whoseTurn;
							game.highestBet = input;
						}
					}
					else{
						
					}
					//TEST V_02!!//
					
					//TEST TEST TEST// V_01
//					System.out.println("It's player " + game.whoseTurn +"'s turn!");
//					System.out.print("Fold=0 Call=1 Bet=2\n:");
//					Scanner s = new Scanner(System.in);
//					int input = s.nextInt();
//					if(input == 0)		game.player[game.whoseTurn].fold();
//					else if(input == 1)	game.player[game.whoseTurn].bet(game.highestBet);
//					else {
//						System.out.print("How much you want to bet? :");
//						input = s.nextInt();
//						game.player[game.whoseTurn].bet(input);
//						highestBetter = game.whoseTurn;
//						game.highestBet = input;
//					}
//					
//					for(int k=0; k<GameSystem.MAXPLAYER; k++)
//						if(game.player[k] != null)	System.out.println("Player "+k+":\n" + game.player[k]);
//						else						System.out.println("Player "+k+" Does not exist.");
					//TEST TEST TEST// V_01

				}while(game.nextTurn() != game.highestBetter && game.whoseTurn != -1
						&& game.potTotal.getCurrentPot().winnerByFold == -1);
				
				game.updateRound();
				
				//testing
				game.potTotal.printPot();
				
				/*special case handling*/
				//if everyone folds
				if(game.potTotal.getCurrentPot().winnerByFold != -1)
					break;
				
				//if everyone went all in in current pot
				Pot currentPot = game.potTotal.getCurrentPot();
				int notAllIn = 0;
				for(int j=0; j<GameSystem.MAXPLAYER; j++)
					if(currentPot.playerInvolved[i] && !game.player[i].isAllIn())
						notAllIn++;
				
				if(notAllIn <= 1){
					game.showdown = true;
					break;
				}
			}
			game.updateHand();
		}
		//celebrate the winner
		//losers will just become a spectator without any notification
		//celebrateWinner();
	}
		
	
	//main method - a process created by Poker.java or GUI
	public static void main(String args[]){

		HostTestInText host = new HostTestInText();		

		host.startGame();	
	}	
}
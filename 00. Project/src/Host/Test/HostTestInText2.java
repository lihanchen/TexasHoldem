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
public class HostTestInText2 {
	
	public GameSystem game;
	
	public int playerCount;
		
	public HostTestInText2(){
		playerCount = 8;
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
	//each game
	game = new GameSystem(playerCount);
	//game.player[0].totalChip = 5;
	
	//TEST TEST TEST// V_01
//		System.out.println("before the game start");
//		for(int i=0; i<GameSystem.MAXPLAYER; i++)
//			if(game.player[i] != null)	System.out.println("Player "+i+":\n" + game.player[i]);
//			else						System.out.println("Player "+i+" Does not exist.");
	//TEST TEST TEST// V_01
	
	//each hand
	while(game.playerCount() > 1){
		game.newHand();

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
			game.newRound();
			
			//TEST TEST TEST// V_01
//				System.out.println("Flop state : " + game.flopState);
			//TEST TEST TEST// V_01

			//each turn
			do{					
				
				sendGameState();
				updateAction();
				

			}while(game.nextTurn() != game.highestBetter && game.whoseTurn != -1
					&& game.potTotal.getCurrentPot().winnerByFold == -1);
			
			game.updateRound();
			
			//testing
			game.potTotal.printPot();
			
			/*special case handling*/
			Pot currentPot = game.potTotal.getCurrentPot();
			currentPot.printPot();
			System.out.println("WinnerByFold : " + currentPot.winnerByFold);
			//if everyone folds
			if(currentPot.winnerByFold != -1)
				break;
			
			//if everyone went all in in current pot
			int notAllIn = 0;
			for(int j=0; j<GameSystem.MAXPLAYER; j++)
				if(currentPot.playerInvolved[j] && !game.player[j].isAllIn())
					notAllIn++;
			
			System.out.println("Not All In (Showdown)  : " + notAllIn);
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
	System.out.println("----- THE END ------");
	//TODO what to do when the game ends?

}


public void sendGameState()
{
	//TEST V_02!!//
	System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nFlops :");
	
	if(game.flopState == 0)	System.out.println("-");
	else if(game.flopState == 1)	for(int k=0; k<3; k++)	System.out.println(game.flops[k]);
	else if(game.flopState == 2)	for(int k=0; k<4; k++)	System.out.println(game.flops[k]);
	else if(game.flopState == 3)	for(int k=0; k<5; k++)	System.out.println(game.flops[k]);
	
	System.out.println();
	for(int k=0; k<GameSystem.MAXPLAYER; k++){
		if(game.player[k] != null){
			if(game.dealer == k)	System.out.println("***Dealer***");
			if(game.whoseTurn == k)	System.out.println("---Your Turn---");
			System.out.println("Player "+k+":\n" + game.player[k]);
		}
	}

}

public void updateAction()
{
//First, receive Action
	System.out.println("It's player " + game.whoseTurn +"'s turn!");
	System.out.print("Fold=0 Call=1 Bet=2\n:");

	//random input generation
	Random generator = new Random();
	//int input;
	int raise = 0;
	
	//Scanner s = new Scanner(System.in);
	//int input = s.nextInt();
	//int input = generator.nextInt(3);
	int input;
	if(game.player[game.whoseTurn].totalChip<game.highestBet)
		input = generator.nextInt(2);
	else
		input = generator.nextInt(3);

	System.out.println(input);

	if(input == 2){
//input for raise
		System.out.print("How much you want to bet? :");
		int whileloop=0;
		
		while(whileloop==0){
			raise = generator.nextInt(2000);
			if(game.player[game.whoseTurn].totalChip < 20 || game.player[game.whoseTurn].totalChip<=game.highestBet){
				raise = game.player[game.whoseTurn].totalChip;
				whileloop=1;
			}
			else if(raise>=20 && raise>game.highestBet && raise<=game.player[game.whoseTurn].totalChip && raise>game.player[game.whoseTurn].betAmount){
				whileloop=1;
			}
			System.out.println("re-type bet amount");
		}
		//s = new Scanner(System.in);
		//raise = s.nextInt();

		System.out.println(raise);

	}

//Now, update Action
	if(input == 0){
		game.player[game.whoseTurn].fold();
		game.potTotal.fold(game.whoseTurn);
		
		//Special 
		Pot currentPot = game.potTotal.getCurrentPot();
		int numPlaying = 0;
		for(int i=0; i<GameSystem.MAXPLAYER; i++){
			if(currentPot.playerInvolved[i]){
				numPlaying++;
				currentPot.winnerByFold = i;
			}
		}
		if(numPlaying > 1){
			currentPot.winnerByFold = -1;
		}
	}
	else if(input == 1)	game.player[game.whoseTurn].bet(game.highestBet);
	else if(input == 2){
		game.player[game.whoseTurn].bet(raise);
		game.highestBetter = game.whoseTurn;
		game.highestBet = raise;
	}
	//TEST V_02!!//
	
	//TEST TEST TEST// V_01
//		System.out.println("It's player " + game.whoseTurn +"'s turn!");
//		System.out.print("Fold=0 Call=1 Bet=2\n:");
//		Scanner s = new Scanner(System.in);
//		int input = s.nextInt();
//		if(input == 0)		game.player[game.whoseTurn].fold();
//		else if(input == 1)	game.player[game.whoseTurn].bet(game.highestBet);
//		else {
//			System.out.print("How much you want to bet? :");
//			input = s.nextInt();
//			game.player[game.whoseTurn].bet(input);
//			highestBetter = game.whoseTurn;
//			game.highestBet = input;
//		}
//		
//		for(int k=0; k<GameSystem.MAXPLAYER; k++)
//			if(game.player[k] != null)	System.out.println("Player "+k+":\n" + game.player[k]);
//			else						System.out.println("Player "+k+" Does not exist.");
	//TEST TEST TEST// V_01


}

//main method - a process created by Poker.java or GUI
	public static void main(String args[]){

		HostTestInText2 host = new HostTestInText2();		
		int i=0;
		while(i<100){
			host.startGame();	
			i++;
		}
	}	
}
package Host.Test;

import Host.GameSystem.Card;
import Host.GameSystem.GameSystem;
import Host.GameSystem.Player;
import Host.GameSystem.Pot;

public class test {

	public static void main(String[] arg)
	{
		test t = new test();
		
		t.testPot();
	}
	
	public void testPot(){
		//initialization
		Player player[] = new Player[8];
		Card cards[] = new Card[2];
		Pot pot = new Pot();

		//Test 1 ( Every player has equal amount of chip and All-in case )
		test1(player,cards,pot);
		//Test 2 ( One on One all-in case with one player has more chip than other player ). 
		test2(player,cards,pot);
		//Test 3 ( each player with different amount pot goes all-in )
		test3(player,cards,pot);
		//Test 4 ( only 4 players bet ) 
		test4(player,cards,pot);
		
	}
	public void test1(Player player[], Card cards[], Pot pot){
		int tempbet=100;
		for(int i=0; i<GameSystem.MAXPLAYER; i++){
			player[i] = new Player();
			player[i].hand = cards;
			player[i].totalChip = tempbet;
		}
		for(int i=0; i<GameSystem.MAXPLAYER; i++){
			player[i].bet(player[i].totalChip);
		}
		for(int i=0; i<GameSystem.MAXPLAYER; i++)
		{
			System.out.println("Player "+ i+1 + " : " + player[i].totalChip + "chips and " + player[i].betAmount + "bet");
		
		}
		pot = new Pot();
		pot.gatherPots(player, 100);
		pot.printPot();
		System.out.println("Winner takes all");
	}
	public void test2(Player player[], Card cards[], Pot pot){
		int tempbet=100;
		for(int i=0; i<GameSystem.MAXPLAYER; i++){
			player[i] = new Player();
			player[i].hand = cards;
			player[i].totalChip = tempbet;
			tempbet+=50;
		}
		player[0].bet(player[0].totalChip);
		System.out.println(player[0].betAmount);
		if(player[7].bet(player[0].betAmount)==false){
			player[7].bet(player[7].totalChip);
		}
		for(int i=0; i<GameSystem.MAXPLAYER; i++)
		{
			System.out.println("Player "+ i + " : " + player[i].totalChip + "chips and " + player[i].betAmount + "bet");
		
		}
		pot = new Pot();
		pot.gatherPots(player, 100);
		pot.printPot();
	}
	public void test3(Player player[], Card cards[], Pot pot){
		int tempbet=100;
		for(int i=0; i<GameSystem.MAXPLAYER; i++){
			player[i] = new Player();
			player[i].hand = cards;
			player[i].totalChip = tempbet;
			tempbet+=50;
		}
		for(int i=0; i<GameSystem.MAXPLAYER; i++){
			player[i].bet(player[i].totalChip);
		}
		for(int i=0; i<GameSystem.MAXPLAYER; i++)
		{
			System.out.println("Player "+ i + " : " + player[i].totalChip + "chips and " + player[i].betAmount + "bet");
		
		}
		pot = new Pot();
		pot.gatherPots(player, 450);
		pot.printPot();	
			
	}
	public void test4(Player player[], Card cards[], Pot pot){
		int tempbet=100;
		for(int i=0; i<GameSystem.MAXPLAYER; i++){
			player[i] = new Player();
			player[i].hand = cards;
			player[i].totalChip = tempbet;
			tempbet+=50;
		}
		for(int i=1; i<GameSystem.MAXPLAYER; i+=2){
			player[i].bet(player[i].totalChip);
		}
		for(int i=0; i<GameSystem.MAXPLAYER; i++)
		{
			System.out.println("Player "+ i + " : " + player[i].totalChip + "chips and " + player[i].betAmount + "bet");
		
		}
		pot = new Pot();
		pot.gatherPots(player, 450);
		pot.printPot();	
	}
}


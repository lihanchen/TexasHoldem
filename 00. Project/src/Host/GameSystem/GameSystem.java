package Host.GameSystem;

import GameState.Gamestate;


public class GameSystem{

	public Player player[];

	//**Instance variables that hold throughout the entire game;
	public int whoseTurn;		//player index
	public int dealer;			//player index
	public int bigBlinder;		//player index
	public int smallBlinder;	//player index

	//**Instance variables that reset every hand;
	public Card flops[];
	public int flopState;
	
	public Pot potTotal;
	public int highestBet;
	public int highestBetter;
	
	private Deck deck;
	
	public int blind;
	public int leftover;
	
	public boolean showdown;
	
	public GameSystem() {
		
		player = new Player[MAXPLAYER];
		for(int i=0; i<MAXPLAYER; i++){
			player[i] = null;
		}
		
		blind = INIT_BLIND;
		dealer = -1;
		leftover = 0;
	}
		
	//constructor
	//What to initialize:
	//1. players
	//2. table
	public GameSystem(int numPlayer){
		
		player = new Player[MAXPLAYER];
		for(int i=0; i<numPlayer; i++){
			player[i] = new Player();
		}
		for(int i=numPlayer; i<MAXPLAYER; i++){
			player[i] = null;
		}
		
		blind = INIT_BLIND;
		dealer = -1;
		leftover = 0;
	}
	
	public GameSystem(String[] players){
		
		player = new Player[MAXPLAYER];
		for(int i=0; i<MAXPLAYER; i++){
			if (players[i] != null)
				player[i] = new Player();
			else
				player[i] = null;
		}
		
		blind = INIT_BLIND;
		dealer = -1;
		leftover = 0;
	}
	
	//Hand
	//1. new deck
	//2. assign cards
	public void newHand(){
		
		deck = new Deck();
		deck.shuffle();
		
		for(int i=0; i<MAXPLAYER; i++)
			if(player[i] != null){
				player[i].dealHands(deck.drawHands());
				player[i].latestAction = null;
			}
		
		flops = deck.drawFlops();
		
		//update dealer
		if(dealer == -1)	dealer = INIT_DEALER;
		else				dealer = nextPlayer(dealer);
		
		potTotal = new Pot(leftover, player);
		leftover = 0;

		whoseTurn = dealer;
		highestBet = blind;
		//initBlinds();
		
		flopState = 0;
	}
	public void newRound(){
		
		if(nextPlayer(whoseTurn) != -1 )
			highestBetter = nextTurn();
		else
			highestBetter = whoseTurn;

	}
	
	public void updateRound(){
		
		for(int i=0; i<MAXPLAYER; i++)
			if(player[i] != null && 
				!player[i].hasFolded &&	!player[i].isAllIn() )	player[i].latestAction = null;
		
		potTotal.gatherPots(player, highestBet);
		
		highestBet = 0;
		whoseTurn = dealer;
		
	}
	public void updateHand(){

		//find and give pot to winner
		potTotal.potToWinner(this);				
		flopState = 4;
		
	}
	
	public void nullLosers() {
		for(int i=0; i<MAXPLAYER; i++){
			if(player[i]!=null && player[i].totalChip == 0)
				player[i]=null;
		}
	}
	
	
	public void initBlinds(){
		//initial small/big blinds
		//updates
		if(playerCount() > 2){
			player[nextTurn()].bet(blind/2);
			player[nextTurn()].bet(blind);
		}
		else {
			player[whoseTurn].bet(blind/2);
			player[nextTurn()].bet(blind);
		}
	}
	
	public int nextTurn(){
		whoseTurn = nextPlayer(whoseTurn);
		return whoseTurn;
	}
	public int nextPlayer(int currentPlayer){
		int nextP = currentPlayer+1;
		if(nextP == MAXPLAYER)	nextP = 0;
		
		while(true){
			if(player[nextP] != null && !player[nextP].hasFolded && !player[nextP].isAllIn())	break;
	
			nextP++;
			if(nextP == MAXPLAYER)	nextP = 0;

			if(nextP == currentPlayer) return -1;
		}
		
		return nextP;
	}

	public int playerCount(){
		
		int count = 0;
		for(int i=0; i<MAXPLAYER; i++)
			if(player[i]!=null)	count++;
		
		return count;
	}
	
	public Gamestate getGamestate(){
		Gamestate gamestate = new Gamestate();
		
		gamestate.player = new Player[GameSystem.MAXPLAYER];
		for(int i=0; i< GameSystem.MAXPLAYER; i++)
			if(player[i] == null)	gamestate.player[i] = null;
			else					gamestate.player[i] = new Player(player[i]);
		
		gamestate.whoseTurn = whoseTurn;
		gamestate.dealer = dealer;
		gamestate.bigBlinder = bigBlinder;
		gamestate.smallBlinder = smallBlinder;
		
		gamestate.flops = new Card[5];
		for(int i=0; i<5; i++)
			gamestate.flops[i] = new Card(flops[i].getKind(), flops[i].getNumber());

		gamestate.flopState = flopState;
		
		gamestate.potTotal = new Pot(potTotal);
		gamestate.highestBet = highestBet;
		
		gamestate.blind = blind;
		gamestate.leftover = leftover;
		
		gamestate.showdown = showdown;
		
		return gamestate;
	}

	public static int INIT_CHIP = 1000;
	public static int MAXPLAYER = 8;
	public static int INIT_BLIND = 20;
	public static int INIT_DEALER = 0;
}

package GameState;

import java.io.Serializable;

import Host.GameSystem.*;

//sends Gamestate from server to host while in the game
//*** One gamestate holds through the entire game ***//
public class Gamestate implements Serializable {
	
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
	
	public int blind;
	public int leftover;
	
	public boolean showdown;
}

package GUI;


import java.awt.Font;
import java.io.IOException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.state.StateBasedGame;

import GameState.Gamestate;
import Host.GameSystem.Player;
import Network.UserAction;
import Poker.Music;

public class OngoingMode extends TableMode {
	
	private final int[][] mainCardOffsets = {{172, 35}, {254, 35}};
	private final int[] mainCheckButtonOffset = {10, 35};
	private final int[] mainFoldButtonOffset = {10, 90};
	private final int[] mainRaiseButtonOffset = {340, 90};
	private final int[] mainAllInButtonOffset = {440, 54};
	private final int[] mainRaiseTextFieldOffset = {340, 35};
	private final int[] mainChipAmountOffset = {37, -30};
	private final int[] mainDealerChipOffset = {7, -30};
	private final int[] mainTotalAmountOffset = {10, 15};		// added (center-left align)
	
	private final int[][] playerCardOffsets = {{7, 32}, {89, 32}};
	private final int[] playerChipAmountOffset = {72, 145};
	private final int[] playerDealerChipOffset = {42, 145};
	protected final int playerNameOffset[] = {8, 15};			// added, overloads. (center-left align)
	private final int[] playerTotalAmountOffset = {162, 15};		// added (center-right align)
	
	private final int[] timerPanelPosition = {750, 478};
	private final int[] timerPanelSize = {85, 55};
	private final int[] yourTurnTextOffset = {43, 10};	// center
	private final int[] timeTextOffset = {43, 33};		// center
	
	
	private final Color winnerLabelColor = new Color(212, 65, 238, 242);
	private final Color thinkingLabelColor = new Color(128, 128, 128, 242);
	private final Color foldLabelColor = new Color(206, 0, 0, 242);
	private final Color raiseLabelColor = new Color(92, 184, 17, 242);
	private final Color checkLabelColor = new Color(30, 98, 208, 242);
	private final Color allInLabelColor = new Color(156, 51, 237, 242);
	
	private final String[] winnerLabelStrings = {"High Card", "One Pair", "Two Pair", "Three of a Kind",
					"Straight", "Flush", "Full House", "Four of a Kind", "Straight Flush", "Royal Flush"};
	
	
	
	private PopupMessageOneButton popupRaiseInvalid;
	
	private PopupMessageTwoButtons popupAllInConfirm;
	private final String allInConfirmString = "Are you sure you want to go all in?";
	
	private PopupMessageOneButton popupLostGame;
	private final String lostGameString = "You lost. Press OK to return to main menu.";
	private final String winGameString = "You won!!! Press OK to return to main menu.";
	
	private Cards cards;
	private ChipAmounts chipAmounts;
	private DealerChip dealerChip;

	private TrueTypeFont infoFontBig;
	private TrueTypeFont buttonFont;
	private TrueTypeFont allInButtonFont;
	private TrueTypeFont totalAmountFont;		// added
	
	private TrueTypeFont timerFont;
	private TrueTypeFont yourTurnFont;
	
	private final double timePerTurn = 20.99;//36000.0;//30.99;
	private long turnStartTimeNano;
	
	private final int minimumRaise = 20;
	
	
	Button foldButton;
	Button checkButton;
	Button raiseButton;
	Button allInButton;
	RaiseTextField raiseTextField;

	//private String checkButtonString;	// changes between check or call
	//private String raiseButtonString;	// changes between bet and raise

	private boolean checkOrCall;	// true=check
	private boolean betOrRaise;		// true = bet;
	
	private String[] playerNamesLocal;
	
	
	private Gamestate gameState;
	private int lastFlopState;
	
	private Gamestate postHandGameState;
	private int lastPotIndex;
	private String[] winnerLabels;
	private int[] potLeftovers;


	
	@Override
	public void init(GameContainer container, final StateBasedGame game)throws SlickException {
		
		super.init(container, game);
		
		// load UI fonts
		infoFontBig = new TrueTypeFont(new java.awt.Font("Segoe UI Semibold", Font.PLAIN, 26), true);
		buttonFont = new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 24), true);
		allInButtonFont = new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 16), true);		
		totalAmountFont = new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 16), true);
		timerFont = new TrueTypeFont(new java.awt.Font("Segoe UI Semibold", Font.PLAIN, 32), true);
		yourTurnFont = new TrueTypeFont(new java.awt.Font("Segoe UI", Font.ITALIC, 14), true);	
		
		// initialize cards
		int[][][] playerCardPositions = new int[8][2][2];
		playerCardPositions[0][0][0] = mainPanelPosition[0]+mainCardOffsets[0][0];
		playerCardPositions[0][0][1] = mainPanelPosition[1]+mainCardOffsets[0][1];
		playerCardPositions[0][1][0] = mainPanelPosition[0]+mainCardOffsets[1][0];
		playerCardPositions[0][1][1] = mainPanelPosition[1]+mainCardOffsets[1][1];
		for (int i=1; i<8; ++i) {
			playerCardPositions[i][0][0] = playerPanelPositions[i][0]+playerCardOffsets[0][0];
			playerCardPositions[i][0][1] = playerPanelPositions[i][1]+playerCardOffsets[0][1];
			playerCardPositions[i][1][0] = playerPanelPositions[i][0]+playerCardOffsets[1][0];
			playerCardPositions[i][1][1] = playerPanelPositions[i][1]+playerCardOffsets[1][1];
		}
		cards = new Cards(playerCardPositions);
		
		
		// initialize chip amounts
		int[][] playerAmountPositions = new int[8][2];
		playerAmountPositions[0][0] = mainPanelPosition[0]+mainChipAmountOffset[0];
		playerAmountPositions[0][1] = mainPanelPosition[1]+mainChipAmountOffset[1];
		for (int i=1; i<8; ++i) {
			playerAmountPositions[i][0] = playerPanelPositions[i][0]+playerChipAmountOffset[0];
			playerAmountPositions[i][1] = playerPanelPositions[i][1]+playerChipAmountOffset[1];
		}
		chipAmounts = new ChipAmounts(infoFont, infoFontBig, 0, playerAmountPositions);
		
		// initialize dealer chip
		int[][] dealerChipPositions = new int[8][2];
		dealerChipPositions[0][0] = mainPanelPosition[0]+mainDealerChipOffset[0];
		dealerChipPositions[0][1] = mainPanelPosition[1]+mainDealerChipOffset[1];
		for (int i=1; i<8; ++i) {
			dealerChipPositions[i][0] = playerPanelPositions[i][0]+playerDealerChipOffset[0];
			dealerChipPositions[i][1] = playerPanelPositions[i][1]+playerDealerChipOffset[1];
		}
		dealerChip = new DealerChip(dealerChipPositions, 0);
		
		
		
		leaveButton = new Button(container, GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_leave.png",
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_leave_down.png",
				leaveButtonPosition[0], leaveButtonPosition[1],
				new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent arg0) {	// leave action
						// disable buttons
						setButtonsEnable(false);
						popupLeaveConfirm.setVisible(leaveButton);
					}
				});
		
		
		popupLeaveConfirm = new PopupMessageTwoButtons(container, leaveConfirmString,
				new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent arg0) {		// ok action
						// disconnect from host, return to main screen
						GUI.cmh.close();
						GUI.cmh = null;
						game.enterState(1);
					}
				}, new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent arg0) {		// cancel action
						// re-enable buttons
						setButtonsEnable(true);
					}
				});
		
		
		
		
		popupLostGame = new PopupMessageOneButton(container, "",
				new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent arg0) {	// ok action
						// disconnect from host, return to main menu
						game.enterState(1);
					}
				});

		
		popupRaiseInvalid = new PopupMessageOneButton(container, "",	// raise error msg will be set manually
				new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent arg0) {	// ok action
						// close popup, re-enable gui, erase raise amount
						setButtonsEnable(true);
					}
				});
		
		
		popupAllInConfirm = new PopupMessageTwoButtons(container, allInConfirmString,
				new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent arg0) {	// ok action
						//if (GUI.cmh!=null) {
							try {
								Player player = gameState.player[GUI.playerIndexInHost];
								GUI.cmh.send(new UserAction(UserAction.Action.ALL_IN, 
										player.totalChip + player.betAmount));
							} catch (IOException e) {
								System.out.println("failed to send raise (all in)!");
							}
						//}
						// do not re-enable buttons
					}
				},
				new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent arg0) {	// cancel action
						// close popup, enable buttons
						setButtonsEnable(true);
					}
				});
		
		// load buttons and textfield
		
		raiseTextField = new RaiseTextField(container,
				mainPanelPosition[0] + mainRaiseTextFieldOffset[0],
				mainPanelPosition[1] + mainRaiseTextFieldOffset[1]);
				
		checkButton = new Button(container, GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_blue.png",
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_blue_down.png",
				mainPanelPosition[0] + mainCheckButtonOffset[0], mainPanelPosition[1] + mainCheckButtonOffset[1],
				new ComponentListener() {
					@Override
					public void componentActivated(AbstractComponent source) {						
						try {
							//if (GUI.cmh!=null) {
								GUI.cmh.send(new UserAction(
										checkOrCall ? UserAction.Action.CHECK : UserAction.Action.CALL,
										gameState.highestBet));
								setButtonsEnable(false);
							//}
						} catch (IOException e) {
							System.out.println("Failed to send user action");
						}
					}
		});
		
		foldButton = new Button(container, GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_red.png",
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_red_down.png",
				mainPanelPosition[0] + mainFoldButtonOffset[0], mainPanelPosition[1] + mainFoldButtonOffset[1],
				new ComponentListener() {
					@Override
					public void componentActivated(AbstractComponent source) {						
						try {
							//if (GUI.cmh!=null) {
								GUI.cmh.send(new UserAction(UserAction.Action.FOLD, 0));
								setButtonsEnable(false);
							//}
						} catch (IOException e) {
							System.out.println("Failed to send user action");
						}
					}
		});
				
		raiseButton = new Button(container, GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_green.png",
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_green_down.png",
				mainPanelPosition[0] + mainRaiseButtonOffset[0], mainPanelPosition[1] + mainRaiseButtonOffset[1],
				new ComponentListener() {
					@Override
					public void componentActivated(AbstractComponent source) {						
						try {
							//if (GUI.cmh!=null) {
								
								String raiseAmtString = raiseTextField.getText();
								int raiseAmount = 0;
								if (!raiseAmtString.isEmpty()) {
									raiseAmount = Integer.parseInt(raiseAmtString);
									
									Player player = gameState.player[GUI.playerIndexInHost];
									int playerTotalPlusBet = player.totalChip + player.betAmount;
									int raiseToAtLeast = gameState.highestBet + minimumRaise;
									
									if (raiseAmount < raiseToAtLeast) {
										// must raise to above the highest bet
										setButtonsEnable(false);
										popupRaiseInvalid.setMessageString(betOrRaise
												? "Must bet at least $"+raiseToAtLeast
												: "Must raise to at least $"+raiseToAtLeast);
										popupRaiseInvalid.setVisible(raiseButton);
									} else if (raiseAmount >= playerTotalPlusBet) {
										// assume this means all in, show all in popup 
										setButtonsEnable(false);
										popupAllInConfirm.setVisible(raiseButton);
									} else {
										GUI.cmh.send(new UserAction(
												betOrRaise ? UserAction.Action.BET : UserAction.Action.RAISE,
												raiseAmount));
										setButtonsEnable(false);
									}
									
								} else {
									// no amount entered
									setButtonsEnable(false);
									popupRaiseInvalid.setMessageString("No amount entered!");
									popupRaiseInvalid.setVisible(raiseButton);
								}
								
							//}
						} catch (IOException e) {
							System.out.println("Failed to send user action");
						}
					}
		});
		
		allInButton = new Button(container, GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_purple.png",
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_purple_down.png",
				mainPanelPosition[0] + mainAllInButtonOffset[0], mainPanelPosition[1] + mainAllInButtonOffset[1],
				new ComponentListener() {
					@Override
					public void componentActivated(AbstractComponent source) {
						// show all in popup
						setButtonsEnable(false);
						popupAllInConfirm.setVisible(allInButton);
					}
		});
		
		playerNamesLocal = new String[8];
		for (int i=0; i<8; ++i) {
			playerNamesLocal[i] = null;
		}
		
		winnerLabels = new String[8];
		potLeftovers = new int[8];
	}
	
	protected void setPlayerNamesLocal(String[] names) {
		this.playerNamesLocal = names;
	}
	
	
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
		super.update(container, game, delta);
		
		// update all cards
		cards.update(delta);
		
		// update all chip amounts
		chipAmounts.update(delta);
		
		// update dealer chip
		dealerChip.update(delta);
		
		
		/*
		// temporary method for transitioning between modes
		if (container.getInput().isKeyPressed(Input.KEY_1))
			game.enterState(1);
		else if (container.getInput().isKeyPressed(Input.KEY_2))
			game.enterState(2);
		else if (container.getInput().isKeyPressed(Input.KEY_3))
			game.enterState(3);
		*/
		
		
		// on-enter-mode actions
		if (GUI.currentMode != 4) {
			
			lastFlopState = 4;
			lastPotIndex = -1;
			for (int i=0; i<8; i++)
				winnerLabels[i] = null;
			
			gameState = null;
			postHandGameState = null;
			
			checkOrCall = true;
			betOrRaise = true;
			
			
			// reset cards
			cards.resetCards();
			
			// clear all chip amounts
			for (int i=0; i<8; i++) {
				chipAmounts.setPlayerAmount(i, 0);
				chipAmounts.setPotAmount(i, 0);
			}
			
			// disable buttons
			setButtonsEnable(false);
			
			GUI.currentMode = 4;
		}
		
		
		// check if we're still connected to host
		if (GUI.hostConnectionError_flag) {
			GUI.hostConnectionError_flag = false;
			setButtonsEnable(false);
			popupHostConnectionLost.setVisible(null);
		}
		
		
		// check if it's our turn and we're out of time
		if (gameState!=null && gameState.whoseTurn==GUI.playerIndexInHost
				&& getRemainingTimeThisTurn()==0.0) {
			try {
				GUI.cmh.send(new UserAction(UserAction.Action.FOLD, 0));
			} catch (IOException e) {
				System.out.println("Failed to send automatic fold!");
				e.printStackTrace();
			}
			setButtonsEnable(false);
		}
		
		
		// check for received gamestates from host

		Object receivedObject;
		if (GUI.cmh!=null && (receivedObject=GUI.cmh.getReceivedObject())!=null) {
			
			if (receivedObject instanceof Gamestate) {
			
				Gamestate receivedGameState = (Gamestate)receivedObject;
				
				
				if (receivedGameState.flopState==4) {
					postHandGameState = receivedGameState;
					setButtonsEnable(false);
					lastFlopState = 4;	// need to do this to ensure proper card actions for next hand
					return;
				}
				
				
				gameState = receivedGameState;
				
				
				
				
				/*			
				// DEBUG: print game state
				System.out.println("---------------------------------------------------------------------");
				System.out.println("highest bet: "+gameState.highestBet);
				
				System.out.println("Flops :");
				if(gameState.flopState == 0)	System.out.println("-");
				else if(gameState.flopState == 1)	for(int k=0; k<3; k++)	System.out.println(gameState.flops[k]);
				else if(gameState.flopState == 2)	for(int k=0; k<4; k++)	System.out.println(gameState.flops[k]);
				else if(gameState.flopState == 3)	for(int k=0; k<5; k++)	System.out.println(gameState.flops[k]);
				else if(gameState.flopState == 4)	System.out.print("FLOPSTATE_4: round over.");
				System.out.println();
				for(int k=0; k<8; k++){
					if(gameState.player[k] != null){
						if(gameState.dealer == k)	System.out.println("***Dealer***");
						if(gameState.whoseTurn == k)	System.out.println("---Your Turn---");
						System.out.println("Player "+k+":\n" + gameState.player[k]);
					}
				}
				System.out.println("It's player " + gameState.whoseTurn +"'s turn!");
				// DONE printing game state
				*/
				
				
				
				// sync local player list with gamestate (nulls players who have lost)
				int numPlayersRemaining = 0;
				for (int i=0; i<8; i++) {
					if (gameState.player[i]==null) {
						playerNamesLocal[hostToLocalIndex(i)] = null;
					} else {
						numPlayersRemaining++;
					}
				}
				
				
				
				// check if we've lost the game.  if so, show the lostGame popup
				// when the first gamestate of the next hand is received.
				if (gameState.player[GUI.playerIndexInHost]==null) {
					popupLostGame.setMessageString(lostGameString);
					popupLostGame.setVisible(null);
					// disable cmh now so no further gameStates are received
					GUI.cmh.close();
					GUI.cmh = null;
					return;
				}
				// check if we've won the game.  if so, show lostGame popup
				else if (numPlayersRemaining == 1) {
					popupLostGame.setMessageString(winGameString);
					popupLostGame.setVisible(null);
					// disable cmh now so no further gameStates are received
					GUI.cmh.close();
					GUI.cmh = null;
					return;
				}
				
								
				
				
				// update call/check and bet/raise flags
				int currentBet = gameState.player[GUI.playerIndexInHost].betAmount;
				checkOrCall = (gameState.highestBet==currentBet);
				// update bet/raise label
				if (gameState.highestBet==0 || 
						(gameState.flopState==0 && gameState.bigBlinder==GUI.playerIndexInHost
						&& gameState.highestBet==gameState.blind)) {
					betOrRaise = true;
					
				} else {
					betOrRaise = false;
				}
				

				// enable/disable buttons based on if it's our turn
				if (!popupLeaveConfirm.isVisible())
					setButtonsEnable(true);

				
				// record start time of turn if this gamestate starts a turn
				if (gameState.whoseTurn >= 0)
					turnStartTimeNano = System.nanoTime();
			
				
				// update dealer chip and chip amounts (depends on whoseTurn) ---------------------------------------------
				
				
				if (gameState.whoseTurn != -2) {
					
					// if we're not collecting bets, sync them with the gamstate
					
					System.out.println("###GUI ACTION: sync bets");
					
					// update bets without animation if this gamestate comes
					// before/after a player's turn
					boolean changed = false;
					for (int i=0; i<8; i++) {
						if (gameState.player[i] != null) {
							int localIndex = hostToLocalIndex(i);
							int amount = gameState.player[i].betAmount;
							if (chipAmounts.setPlayerAmount(localIndex, amount))
								changed = true;
						}
					}
					if (changed)
						Music.oneChipSound();
					
						
					// update pot amounts (should be redundant)
					Host.GameSystem.Pot pot = gameState.potTotal;
					for (int i=0; i<8; i++) {
						if (pot!=null) {
							if (chipAmounts.getPotAmount(i) != pot.totalPot) {
								System.out.println("pot "+i+" inconsistent with gamestate!");
								chipAmounts.setPotAmount(i, pot.totalPot);
							}
							pot = pot.splitPot;
						} else {
							if (chipAmounts.getPotAmount(i) != 0) {
								System.out.println("pot "+i+" inconsistent with gamestate!");
								chipAmounts.setPotAmount(i, 0);
							}
						}
					}

				} else {									// COLLECT BETS -----
					
					System.out.println("###GUI ACTION: collect bets");
					
					// collect bets (if any)	
					
					for (int i=0; i<8; i++) {
						
						if (gameState.player[i] != null) {
							
							int localIndex = hostToLocalIndex(i);
							int amount = chipAmounts.getPlayerAmount(localIndex);
							if (amount > 0) {
								chipAmounts.addSendToQueue(
										amount,
										true, localIndex,
										false, 0,	// send to main pot
										0.0, false);
							}
						}
					}
					
					
					// update split pots, taking difference from main pot
					Host.GameSystem.Pot pot = gameState.potTotal.splitPot;
					boolean first = true;
					for (int i=1; i<8; i++) {
						if (pot==null)
							break;
						System.out.println("Collect bets: pot "+i+" has "+pot.totalPot);
						int amount = pot.totalPot;
						int oldAmount = chipAmounts.getPotAmount(i);
						if (oldAmount!=amount) {
							chipAmounts.addSendToQueue(
									amount-oldAmount,
									false, 0,	// take from main pot
									false, i,	// send to whichever sidepot
									first ? 200.0 : 0.0, first);
							first = false;
						}
						pot = pot.splitPot;
					}
					
					
				}
				
				
				
				
				// update cards and dealerchip ---------------------------------------------------------------
				
								
				
				// fold player's cards if needed
				for (int i=0; i<8; i++) {
					if (gameState.player[i]!=null
							&& gameState.player[i].hasFolded) {
						cards.fold(hostToLocalIndex(i));
					}
				}
				
				
				// if showdown occurred, reveal cards of everyone who hasn't folded
				if (lastFlopState!=gameState.flopState && gameState.showdown) {
					// reveal everyone's cards who haven't folded
					for (int i=0; i<8; i++) {
						if (gameState.player[i]!=null && !gameState.player[i].hasFolded) {
							cards.showPlayerCards(hostToLocalIndex(i));
						}
					}
				}
				
				
				// deal cards / reveal flop cards depending on flopstate
				switch (gameState.flopState) {
				
				case 0:
					
					// when a new hand starts...
					if (lastFlopState != 0) {

						// update faces of all centercards and player cards
						for (int i=0; i<5; i++) {
							cards.centerCards[i].setFaceImage(gameState.flops[i]);
						}
						for (int i=0; i<8; i++) {
							if (gameState.player[i] !=null) {
								int localIndex = hostToLocalIndex(i);
								Host.GameSystem.Card[] hand = gameState.player[i].hand;
								cards.playerCards[localIndex][0].setFaceImage(hand[0]);
								cards.playerCards[localIndex][1].setFaceImage(hand[1]);
							}
						}
						
						
						// update dealer chip position
						System.out.println("###GUI ACTION: move dealer chip");
						
						dealerChip.moveTo(hostToLocalIndex(gameState.dealer));	
						
						
						// reset and deal cards, show main player's cards					
						cards.collectCards(500.0);	// allow time for dealerchip to move
						cards.dealCards(hostToLocalIndex(gameState.dealer),
								0.0, playerNamesLocal);
						cards.showMainPlayerCards();
					}
					break;
					
				case 1:
					
					if (lastFlopState==0) {
						// flip over flop cards
						cards.dealFlop(0.0);
					} 
					break;
					
				case 2:
					if (lastFlopState == 1) {
						// flip over turn card						
						cards.dealTurn(0.0);
						
					} else if (lastFlopState == 0){
						// flip over flop and turn cards
						cards.dealFlop(0.0);
						cards.dealTurn(0.0);							
					}
					break;
					
				case 3:
					if (lastFlopState==2) {
						cards.dealRiver(0.0);
						
					} else if (lastFlopState==1) {
						// flip over turn and river cards
						cards.dealTurn(0.0);
						cards.dealRiver(0.0);
					} else if (lastFlopState == 0) {
						// flip over flop, turn, and river cards
						cards.dealFlop(0.0);
						cards.dealTurn(0.0);
						cards.dealRiver(0.0);
					}
					break;
					
				default:						
					break;
				}
				
				lastFlopState = gameState.flopState;
				
				
				System.out.println("\n\n\n\n");
			
			} else if (receivedObject instanceof Integer) { // ******************************************************************
				
				//  POST HAND: reveal cards, distribute winnings for each pot-------------
				
				int potIndex = ((Integer)receivedObject).intValue();
				System.out.println("received int "+potIndex+" from host!");
				
				
				if (potIndex >= 0) {
					
					// find the pot in question
					Host.GameSystem.Pot pot = postHandGameState.potTotal;
					for (int i=0; i<potIndex; i++)
						pot = pot.splitPot;
					
					
					if (lastPotIndex != potIndex) {
						
						// clear winner labels
						for (int i=0; i<8; i++)
							winnerLabels[i] = null;
						
						// show cards of the players involved in this pot
						// unless winner by fold occurred
						if (postHandGameState.potTotal.winnerByFold == -1)
						for (int i=0; i<8; i++) {
							if (pot.playerInvolved[i])
								cards.showPlayerCards(hostToLocalIndex(i));
							else
								cards.hidePlayerCards(hostToLocalIndex(i));
						}
						
					} else {
						
						// set winner labels, unless winner by fold occurred						
						if (postHandGameState.potTotal.winnerByFold==-1) {
							for (int i=0; i<8; i++) {
								if (pot.winner[i]) {
									winnerLabels[hostToLocalIndex(i)] = winnerLabelStrings[pot.winnerRank];
								}
							}
						}
						
						//  count number of winners
						int numWinners = 0;
						for (int i=0; i<8; i++) {
							if (pot.winner[i]) {
								numWinners++;
							}
						}
						int amountPerWinner = pot.totalPot / numWinners;
						potLeftovers[potIndex] = pot.totalPot % numWinners;
						
						System.out.println("amt = $"+pot.totalPot);
						System.out.println("amt per winner = $"+amountPerWinner);
						
						
						// send that amount to each winner of this pot
						boolean first = true;
						for (int i=0; i<8; i++) {
							
							if (gameState.player[i]!=null && pot.winner[i]) {
								chipAmounts.addSendToQueue(
										amountPerWinner,
										false, potIndex,
										true, hostToLocalIndex(i),
										0.0, first);
								first = false;
							}
						}
					}
					
					
				} else {
					
					// clear winner labels
					for (int i=0; i<8; i++)
						winnerLabels[i] = null;
					
					
					// collect leftover in each pot into main pot
					Host.GameSystem.Pot pot = postHandGameState.potTotal.splitPot;
					boolean first = true;
					for (int i=1; i<8; i++) {
						if (pot==null)
							break;
						System.out.println("pot "+i+" has leftover $"+potLeftovers[i]);
						if (potLeftovers[i] > 0) {
							chipAmounts.addSendToQueue(
									potLeftovers[i],
									false, i,
									false, 0,	 // send to main pot
									0.0, first);
							first = false;
						}
						pot = pot.splitPot;
					}
					
				}
			
				lastPotIndex = potIndex;					
				
					
			} else {
				System.out.println("unexpected object type received in OngoingMode");
			}
		}
	}
	


	
	protected void setButtonsEnable(boolean enable) {
		
		// enable/disable leave button
		leaveButton.setEnable(enable);
		
		Player player = null;
		int highestBetPlusMinRaise = -1;
		int playerTotalPlusBet = -1;
		
		
		if (gameState != null) {
			
			player = gameState.player[GUI.playerIndexInHost];
			highestBetPlusMinRaise = gameState.highestBet + minimumRaise;
			playerTotalPlusBet = player.totalChip + player.betAmount;
			
			// update raiseTextField text
			raiseTextField.setText(Integer.toString(
					Math.min(highestBetPlusMinRaise, playerTotalPlusBet)));
		} else {
			raiseTextField.setText("");
		}
		
		
		// enable/disable mainplayer buttons
		if (!enable || gameState==null || gameState.whoseTurn!=GUI.playerIndexInHost) {
			checkButton.setEnable(false);
			foldButton.setEnable(false);
			raiseButton.setEnable(false);
			allInButton.setEnable(false);
			raiseTextField.setEnable(false);
			
		} else {
			
			// fold and all in are always allowed
			foldButton.setEnable(true);
			allInButton.setEnable(true);
			
			// enable check button if player has more than highest bet
			// i.e. player can check without going all in
			checkButton.setEnable(playerTotalPlusBet > gameState.highestBet);
			
			// enable raise button and field if player has more than highestBetPlusMinRaise
			// i.e. player can bet/raise without going all in
			boolean raiseEnable = playerTotalPlusBet > highestBetPlusMinRaise;
			raiseButton.setEnable(raiseEnable);
			raiseTextField.setEnable(raiseEnable);
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		
		super.render(container, game, g);
		drawPlayerNames(g);
		
		
		// draw cards, chip amounts, and dealer chip
		// try to draw moving elements on top
		if (dealerChip.isMoving()) {
			chipAmounts.draw(g);
			cards.draw();
			dealerChip.draw();
		} else if (chipAmounts.sendOngoing()) {
			dealerChip.draw();
			cards.draw();
			chipAmounts.draw(g);
		} else {
			chipAmounts.draw(g);
			dealerChip.draw();
			cards.draw();
		}
		
				
		drawLabels(g);
		drawWinnerLabels(g);
		drawTotalAmounts(g);
		drawInteractiveElements(container, g);
		drawTimer(g);
		
		leaveButton.render(container, g, leaveButtonFont, Color.white, "Leave");
		
		popupHostConnectionLost.render(container, g);
		popupRaiseInvalid.render(container, g);
		popupAllInConfirm.render(container, g);
		popupLostGame.render(container, g);
		popupLeaveConfirm.render(container, g);
	}

	
	
	private double getRemainingTimeThisTurn() {
		double secondsElapsed = (System.nanoTime()-turnStartTimeNano)/1000000000.0;
		return Math.max(timePerTurn - secondsElapsed, 0.0);
	}
	
	
	
	private void drawTimer(Graphics g) {
		
		if (gameState!=null && gameState.whoseTurn==GUI.playerIndexInHost) {
			
			g.setColor(new Color(32, 32, 32, 128));
			g.fillRoundRect(timerPanelPosition[0], timerPanelPosition[1],
					timerPanelSize[0], timerPanelSize[1], 0);
			
			GUI.drawStringCenter(g, yourTurnFont, Color.white, "Your turn!",
					timerPanelPosition[0]+yourTurnTextOffset[0],
					timerPanelPosition[1]+yourTurnTextOffset[1]);
			
			String timeString = String.format("%d", (int)getRemainingTimeThisTurn());
			GUI.drawStringCenter(g, timerFont, Color.white, timeString, 
					timerPanelPosition[0]+timeTextOffset[0],
					timerPanelPosition[1]+timeTextOffset[1]);
		}
	}
	
	

	private void drawWinnerLabels(Graphics g) {
		for (int i=0; i<8; i++) {
			if (winnerLabels[i] != null) {
				drawPlayerLabel(g, i, winnerLabels[i], Color.white, winnerLabelColor);
			}
		}
	}
	
	
	
	private void drawLabels(Graphics g) {
		
		// no labels drawn during showdown or after postHandGameState is received
		if (gameState==null || lastFlopState==4 || gameState.showdown)
			return;
		
		for (int i=0; i<8; i++) {
			if (gameState.player[i] != null) {
				
				int localIndex = hostToLocalIndex(i);
				
				if (i==gameState.whoseTurn) {
					if (localIndex!=0) {
						String timeString = String.format("%d", (int)getRemainingTimeThisTurn());
						drawPlayerLabel(g, localIndex, "Thinking... "+timeString, Color.white, thinkingLabelColor);
					} else {
						drawPlayerLabel(g, localIndex, "Thinking...", Color.white, thinkingLabelColor);
					}
					continue;
				}
				/*
				// check isAllIn() instead of lastAction for the All In label
				if (gameState.player[i].isAllIn()) {
					drawPlayerLabel(g, localIndex, "All In $"+gameState.player[i].betAmount,
							Color.white, allInLabelColor);
					continue;
				}
				*/

				UserAction lastAction = gameState.player[i].latestAction;
				if (lastAction!=null) {
					switch (lastAction.action) {
					case CHECK:
						drawPlayerLabel(g, localIndex, "Check", Color.white, checkLabelColor);
						break;
					case CALL:
						drawPlayerLabel(g, localIndex, "Call $"+lastAction.raiseAmount, Color.white, checkLabelColor);
						break;
					case FOLD:
						drawPlayerLabel(g, localIndex, "Fold", Color.white, foldLabelColor);
						break;
					case BET:
						drawPlayerLabel(g, localIndex, "Bet $"+lastAction.raiseAmount, Color.white, raiseLabelColor);
						break;
					case RAISE:
						drawPlayerLabel(g, localIndex, "Raise to $"+lastAction.raiseAmount, Color.white, raiseLabelColor);
						break;
					case ALL_IN:
						drawPlayerLabel(g, localIndex, "All In $"+lastAction.raiseAmount, Color.white, allInLabelColor);
						break;
					default:
						break;
					}
				}
			}
		}
	}
	
	
	
	private void drawTotalAmounts(Graphics g) {
		if (gameState==null)
			return;
		
		for (int i=0; i<8; i++) {
			
			Player player = gameState.player[localToHostIndex(i)];			
			if (player != null) {
				if (i==0) {
					GUI.drawStringLeftCenter(g, totalAmountFont, Color.white,
							"$"+player.totalChip,
							mainPanelPosition[0]+mainTotalAmountOffset[0],
							mainPanelPosition[1]+mainTotalAmountOffset[1]);
				} else {
					GUI.drawStringRightCenter(g, totalAmountFont, Color.white,
							"$"+player.totalChip,
							playerPanelPositions[i][0]+playerTotalAmountOffset[0],
							playerPanelPositions[i][1]+playerTotalAmountOffset[1]);
				}
			}
		}
	}
	
	
	private void drawInteractiveElements(GUIContext container, Graphics g) {
		g.setColor(Color.white);
		checkButton.render(container, g, buttonFont, Color.white,
				checkOrCall ? "Check" : "Call $"+gameState.highestBet);
		foldButton.render(container, g,  buttonFont, Color.white, "Fold");
		raiseButton.render(container, g, buttonFont, Color.white,
				betOrRaise ? "Bet" : "Raise");
		allInButton.render(container, g, allInButtonFont, Color.white, "All In");
		
		raiseTextField.setRaiseByString(betOrRaise ? "Bet:" : "Raise to:");
		raiseTextField.render(container, g);
	}


	
	private void drawPlayerNames(Graphics g) {

		GUI.drawStringCenter(g, infoFont, Color.white, playerNamesLocal[0],
				mainPanelPosition[0]+mainNameOffset[0],
				mainPanelPosition[1]+mainNameOffset[1]);
		
		for (int i=1; i<8; ++i) {
			if (playerNamesLocal[i] != null) {
				GUI.drawStringLeftCenter(g, infoFont, Color.white, playerNamesLocal[i],
						playerPanelPositions[i][0]+playerNameOffset[0],
						playerPanelPositions[i][1]+playerNameOffset[1]);
			}
		}
	}
		
	
	public int getID() {
		return 4;
	}
}

/*
playerbox
170 x 140
locations: 25,290  25,100  215,10  415,10  615,10  805,100  805,290
offset values:
player string: 85,15 (center)
card1: 7,32
card2: 89,32
chip: 72,145

mainPlayerBox
500 x 150
location: 250,430
offset values:
player string: 250,15 (center)
card1: 172,35
card2: 254,35
checkButton: 10,35 150x45
foldButton: 10,90 150x45
raiseButton: 340,90 150x45
allInButton: 440,43 50x26
dollarSign: 340,54 (left-center)
raiseBy: 350,35
*/
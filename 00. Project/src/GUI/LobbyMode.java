package GUI;

import java.awt.Font;
import java.io.IOException;



import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.state.StateBasedGame;

import Network.HostSearcher;

public class LobbyMode extends TableMode {

	private TrueTypeFont mainStatusFont;
	private TrueTypeFont startButtonFont;

	private final int[] playerPanelAnimationOffset = {69, 54};
	private final int[] mainTextOffset = {250, 75};
	private final int[] mainStartButtonOffset = {150, 50};
	
	private final Color hostLabelColor = new Color(85, 163, 217, 242);
	private final Color joinedLabelColor = new Color(128, 128, 128, 242);

	private Animation[] waitingAnimations;
	private final int NUM_ANIMATIONS = 4;
	
	private Button startButton;
	
	
	private String[] playerNamesLocal;
	private int numPlayers;
	private int hostIndexLocal;
	
	
	
	
	
	@Override
	public void init(GameContainer container, final StateBasedGame game) throws SlickException {
		
		super.init(container, game);
		
		mainStatusFont = new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 32), true);
		startButtonFont = new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 28), true);
		
	
		
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
		
		
		
		startButton = new Button(container, GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_lightbluebig.png", 
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_lightbluebig_down.png",
				mainPanelPosition[0]+mainStartButtonOffset[0],
				mainPanelPosition[1]+mainStartButtonOffset[1],
				new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent source) {
						
						System.out.println("started game!");
						
						// send start signal to host
						try {
							GUI.cmh.send("start");
						} catch (IOException e) {
							System.out.println("Failed to send start signal!");
						}
					}
				});
		startButton.setEnable(false);
		
		
		waitingAnimations = new Animation[NUM_ANIMATIONS];
		
		SpriteSheet sheet = new SpriteSheet(GUI.RESOURCES_PATH+"loading2.png", 32, 32);
		for (int A=0; A<NUM_ANIMATIONS; ++A) {
			waitingAnimations[A] = new Animation();
			for (int i=0; i<8; ++i) {
				waitingAnimations[A].addFrame(sheet.getSprite(i, 0), 64);
			}
			waitingAnimations[A].setCurrentFrame(A*(waitingAnimations[A].getFrameCount()/NUM_ANIMATIONS));
		}
		
		playerNamesLocal = new String[8];
	}

	
	
	
	private void setButtonsEnable(boolean enable) {
		if (!enable) {
			startButton.setEnable(false);
			leaveButton.setEnable(false);
		} else {
			// start button enabled only if i'm the host and there are enough players
			startButton.setEnable(hostIndexLocal==0 && numPlayers>=2);
			// leave button disabled if i'm the host
			leaveButton.setEnable(hostIndexLocal!=0);
		}
	}
	
	
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
		super.update(container, game, delta);
		
		
		// on-enter-mode actions
		if (GUI.currentMode != 3) {
			
			if (GUI.currentMode==2) {
				// stop the host searcher if we came from joinMode
				HostSearcher.stop();
			}
			
			// clear players info
			for (int i=0; i<8; ++i)
				playerNamesLocal[i] = null;
			numPlayers = 0;
			hostIndexLocal = -1;
			
			// disable buttons initially
			setButtonsEnable(false);
			
			GUI.currentMode = 3;
		}
		

		/*
		// temporary method for transitioning between modes
		if (container.getInput().isKeyPressed(Input.KEY_1))
			game.enterState(1);
		else if (container.getInput().isKeyPressed(Input.KEY_2))
			game.enterState(2);
		else if (container.getInput().isKeyPressed(Input.KEY_4))
			game.enterState(4);
		*/
		
		// check if we're still connected to host
		if (GUI.hostConnectionError_flag) {
			GUI.hostConnectionError_flag = false;
			startButton.setEnable(false);
			popupHostConnectionLost.setVisible(null);
		}
		
		// check for new player list, convert to local order
		if (GUI.cmh != null) {
			Object receivedObject = GUI.cmh.getReceivedObject();
			if (receivedObject!=null) {
				
				if (receivedObject instanceof String) {
					
					if (((String)receivedObject).equals("start")) {
						// go to ongoing mode
						GUI.ongoingMode.setPlayerNamesLocal(playerNamesLocal);
						game.enterState(4);
					}
				
				
				} else if (receivedObject instanceof String[]) {
					
					String[] playerNames = (String[])receivedObject;
					
					System.out.println("received player names:");
					for (String s : playerNames) {
						System.out.println("\t"+s);
					}
					
					// find out which place we are in the server player list
					numPlayers = 0;
					GUI.playerIndexInHost = -1;
					for (int i=0; i<8; ++i) {
						if (playerNames[i] != null) {
							numPlayers++;
							if (playerNames[i].equals(GUI.playerName)) {
								GUI.playerIndexInHost = i;
							}
						}
					}
					
					// update local players list
					for (int i=0; i<8; ++i) {
						 String name = playerNames[localToHostIndex(i)];
						 if (i!=0 && name!=null && infoFont.getWidth(name)>90) {
							 int prefixLength = 5;
							 String shortName;
							 do {
								 prefixLength++;
								 shortName = name.substring(0, prefixLength)+"...";
							 } while (infoFont.getWidth(shortName)<=90);
							 playerNamesLocal[i] = name.substring(0, prefixLength-1)+"...";
						 } else {
							 playerNamesLocal[i] = name;
						 }
					}
					hostIndexLocal = (8 - GUI.playerIndexInHost) % 8;
					
					
					// update button states
					if (!popupLeaveConfirm.isVisible())
						setButtonsEnable(true);
					
				
				} else {
					System.out.println("unexpected object type received in LobbyMode");
				}
			}
		}
	}
	
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		
		super.render(container, game, g);
		
		leaveButton.render(container, g, leaveButtonFont, Color.white, "Leave");
		drawPlayerNamesAndStatuses(container, g);
		
		popupHostConnectionLost.render(container, g);
		popupLeaveConfirm.render(container, g);
	}
	
	
	
	
	private void drawPlayerNamesAndStatuses(GUIContext container, Graphics g) {
		
		boolean isHost = (hostIndexLocal==0);
		boolean enoughPlayers = numPlayers >= 2;
		
		g.setColor(Color.white);
		GUI.drawStringCenter(g, infoFont, Color.white, GUI.playerName, mainPanelPosition[0]+mainNameOffset[0],
				mainPanelPosition[1]+mainNameOffset[1]);
		
		for (int i=1; i<8; ++i) {
			
			if (i==hostIndexLocal) {
				
				g.setColor(Color.white);
				GUI.drawStringCenter(g, infoFont, Color.white, playerNamesLocal[i], playerPanelPositions[i][0]+playerNameOffset[0],
						playerPanelPositions[i][1]+playerNameOffset[1]);
				
				drawPlayerLabel(g, i, "Host", Color.white, hostLabelColor);
				
			}
			else if (playerNamesLocal[i] != null) {
				
				g.setColor(Color.white);
				GUI.drawStringCenter(g, infoFont, Color.white, playerNamesLocal[i], playerPanelPositions[i][0]+playerNameOffset[0],
						playerPanelPositions[i][1]+playerNameOffset[1]);
						
				drawPlayerLabel(g, i, "Joined", Color.white, joinedLabelColor);
			}
			else {
				Animation waitingAnimation = waitingAnimations[i%NUM_ANIMATIONS];
				waitingAnimation.draw(playerPanelPositions[i][0]+playerPanelAnimationOffset[0],
						playerPanelPositions[i][1]+playerPanelAnimationOffset[1]);
			}
		}
		
		g.setColor(Color.white);
		if (!enoughPlayers) {
			GUI.drawStringCenter(g, mainStatusFont, Color.white, "Waiting for more players ...", 
					mainPanelPosition[0]+mainTextOffset[0],
					mainPanelPosition[1]+mainTextOffset[1]);
		}
		else {
			if (!isHost) {
				GUI.drawStringCenter(g, mainStatusFont, Color.white, "Waiting for host to start game ...", 
						mainPanelPosition[0]+mainTextOffset[0],
						mainPanelPosition[1]+mainTextOffset[1]);
			}
			else {
				// draw start button
				startButton.render(container, g, startButtonFont, Color.white, "Start Game");
			}
		}
	}

	
	
	@Override
	public int getID() {
		
		return 3;
	}
}


/*
playerbox
170 x 140
locations: 25,290  25,100  215,10  415,10  615,10  805,100  805,290
offset values:
player string: 85,15 (center)


mainPlayerBox
500 x 150
location: 250,430
offset values:
player string: 250,15 (center)
*/
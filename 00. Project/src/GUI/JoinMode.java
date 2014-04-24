package GUI;

import java.util.*;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.StateBasedGame;

import GuiActionThreads.JoinHostThread;
import Network.HostSearcher;

public class JoinMode extends Mode {

	private final String[] joinListColumnNames = {"HOST", "IP ADDRESS"};//, "PLAYERS"};
	private final int[] joinListColumnWidths = {280, 180};//, 120};
	
	private final String lobbyFullString = "The lobby is full.";
	private final String nameTakenString = "The entered player name is already being used.";
	private final String failedJoinString = "Failed to connect to game lobby. Retry?";
	
		
	private Image background;
	private JoinList joinList;
	
	private PopupMessageTwoButtons popupFailedJoin;
	private PopupMessageOneButton popupNameTaken;
	
	
	// status flags
	public boolean joinHostSuccess_flag;
	public boolean joinHostLobbyFull_flag;
	public boolean joinHostNameTaken_flag;
	public boolean joinHostError_flag;
	
	private long refreshTimeNano;
		
	
	@Override
	public void init(GameContainer container, final StateBasedGame game)
			throws SlickException {
		
		super.init(container, game);
		
		background = new Image(GUI.RESOURCES_PATH+"background.png");
		
				
				
		popupFailedJoin = new PopupMessageTwoButtons(container, failedJoinString,
				new ComponentListener() {
					@Override
					public void componentActivated(AbstractComponent source) {	// ok action
						// JOIN GAME
						JoinHostThread jht = new JoinHostThread(GUI.hostIpString, GUI.playerName);
						jht.start();
						
						joinList.setVisible(false);
						popupLoading.setVisible(source);
					}
				}, new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent arg0) {	// cancel action

						joinList.setVisible(true);
						
						// REFRESH
						HostSearcher.checkAvailable();
						joinList.setLoading(true);
						refreshTimeNano = System.nanoTime();	// record time
					}
				});
		popupNameTaken = new PopupMessageOneButton(container, nameTakenString,
				new ComponentListener() {					
					@Override
					public void componentActivated(AbstractComponent source) {	// ok action
						updateGamesList();
						joinList.setVisible(true);
						
						// go to name prompt in startmode
						GUI.startMode.showPopupEnterName(GUI.startMode.joinGameButton);
						game.enterState(1);
					}
				});
		
		joinList = new JoinList(container, true, waitingAnimation,
				joinListColumnNames, joinListColumnWidths,
				new JoinList.IndexedComponentListener() {
					@Override
					public void componentActivated(AbstractComponent source,
							int index){								// join action
						
						// JOIN GAME
						GUI.hostIpString = joinList.getRow(index)[1];
						JoinHostThread jht = new JoinHostThread(GUI.hostIpString, GUI.playerName);
						jht.start();
						
						joinList.setVisible(false);
						popupLoading.setVisible(source);
					}
				},
				new ComponentListener() {	
					@Override
					public void componentActivated(AbstractComponent arg0) {	// refresh action
						
						System.out.println("refresh games list!");
						
						// REFRESH
						HostSearcher.checkAvailable();
						joinList.setLoading(true);
						refreshTimeNano = System.nanoTime();	// record time
					}
				},
				new ComponentListener() {			// cancel action
					
					@Override
					public void componentActivated(AbstractComponent arg0) {
						game.enterState(1);
					}
				});
		
	}
	

	
	public void updateGamesList() {
		
		List<String[]> gamesInfo = HostSearcher.getValidNamesAndIps();
		List<Boolean> gamesJoinable = new ArrayList<Boolean>();
		for (int i=0; i<gamesInfo.size(); ++i)
			gamesJoinable.add(true);
		joinList.setRowsData(gamesInfo);
		joinList.setRowsJoinable(gamesJoinable);
	}
	
	
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		/*
		// temporary method for transitioning between modes
		if (container.getInput().isKeyPressed(Input.KEY_1))
			game.enterState(1);
		else if (container.getInput().isKeyPressed(Input.KEY_3))
			game.enterState(3);
		else if (container.getInput().isKeyPressed(Input.KEY_4))
			game.enterState(4);
		*/
		
		
		// on-enter-mode actions
		if (GUI.currentMode != 2) {			
			// start the host searcher (will automatically call checkAvailable())
			HostSearcher.start(4320);
			joinList.setLoading(true);
			refreshTimeNano = System.nanoTime();	// record time
			
			GUI.currentMode = 2;
		}
		
	
		
		// if joinlist is refreshing, check if 1 second has passed
		if (joinList.isLoading()) {
			if (System.nanoTime() - refreshTimeNano >= 1000000000L) {
				joinList.setLoading(false);
				updateGamesList();
			}
		}
		
		
		// if we're trying to join a host, check the status of it
		if (popupLoading.isVisible()) {
			if (joinHostSuccess_flag){
				popupLoading.setInvisible();
				joinList.setVisible(true);
				// get game state before going to lobby mode?
				System.out.println("connection to host established... going to lobby");
				game.enterState(3);
			} else if (joinHostLobbyFull_flag) {
				popupLoading.setInvisible();
				joinList.setVisible(false);
				popupNameTaken.setMessageString(lobbyFullString);
				popupNameTaken.setVisible(popupLoading.getPopupSource());
			} else if (joinHostNameTaken_flag) {
				popupLoading.setInvisible();
				joinList.setVisible(false);
				popupNameTaken.setMessageString(nameTakenString);
				popupNameTaken.setVisible(popupLoading.getPopupSource());
			} else if (joinHostError_flag) {
				popupLoading.setInvisible();
				joinList.setVisible(false);
				popupFailedJoin.setVisible(popupLoading.getPopupSource());
			}
		}
		
		/*
		if (container.getInput().isKeyPressed(Input.KEY_F)) {
			if (!gamesInfo.isEmpty()) {
				gamesInfo.remove(0);
				gamesJoinable.remove(0);
			}
			joinList.setRowsData(gamesInfo);
			joinList.setRowsJoinable(gamesJoinable);
		}*/
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		
		background.draw(0, 0, container.getWidth(), container.getHeight());
		
		joinList.render(container, g);
		
		popupLoading.render(container, g);
		popupNameTaken.render(container, g);
		popupFailedJoin.render(container, g);
	}

	

	@Override
	public int getID() {
		return 2;
	}
	
	
}

package GUI;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.state.StateBasedGame;

import GuiActionThreads.StartHostThread;

public class StartMode extends Mode
{
	private Image background;

	private final int[] menuPanelPosition = {40, 355};
	private final int[] menuPanelSize = {220, 125};
	
	private final int[] hostGameButtonOffset = {10, 15};
	private final int[] joinGameButtonOffset = {10, 70};
	//private final int[] spectateGameButtonOffset = {10, 125};
	private final int[] exitButtonOffset = {10, 165};
	

	private final String namePromptString = "Enter player name:";
	private final String areYouSureExitString = "Are you sure you want to exit to desktop?";
	private final String hostFailedString = "Failed to start host! Retry?";
	
	private TrueTypeFont buttonFont;
	
	private Button hostGameButton;
	protected Button joinGameButton;
	//private Button spectateGameButton;
	private Button exitButton;
		
	
	private PopupMessageTwoButtons popupConfirmExit;
	private PopupPromptTwoButtons popupEnterName;
	private PopupMessageTwoButtons popupFailedHost;
	
	
	// status flags
	public boolean startHostSuccess_flag;
	public boolean startHostError_flag;
	
	
	public void init(GameContainer container, final StateBasedGame game) throws SlickException {
		
		super.init(container, game);
		
		background = new Image(GUI.RESOURCES_PATH+"background.png");
		
		buttonFont = new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 20), true);
				
		
		popupFailedHost = new PopupMessageTwoButtons(container, hostFailedString,
				new ComponentListener() {
					@Override
					public void componentActivated(AbstractComponent arg0) {	// ok action
						// retry starting host process
						StartHostThread sht = new StartHostThread(GUI.playerName);
						sht.start();
						System.out.println("HostSetup thread started!");
						
						setMenuEnable(false);
						popupLoading.setVisible(hostGameButton);
					}
				}, new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent arg0) {	// cancel action
						setMenuEnable(true);
					}
				});
		
		
		
		popupConfirmExit = new PopupMessageTwoButtons(container, areYouSureExitString,
				new ComponentListener() {
					@Override
					public void componentActivated(AbstractComponent source) {	// OK action
						setMenuEnable(true);
						
						if (source==(AbstractComponent)exitButton) {
							System.exit(0);
						}
						else {
							System.out.println("SOMETHING'S WRONG");
						}
					}
				},
				new ComponentListener() {
					@Override
					public void componentActivated(AbstractComponent source) {	// cancel action
						setMenuEnable(true);
					}
				});
		
		popupEnterName = new PopupPromptTwoButtons(container, namePromptString,
				new ComponentListener() {
					@Override
					public void componentActivated(AbstractComponent source) {	// OK action
						setMenuEnable(true);
						
						if (source==(AbstractComponent)hostGameButton) {
							
							// START HOST							
							// start thread to start host process and connect to it
							String hostName = popupEnterName.getText();
							GUI.playerName = hostName;
							StartHostThread sht = new StartHostThread(hostName);
							sht.start();
							System.out.println("HostSetup thread started!");
							
							setMenuEnable(false);
							popupLoading.setVisible(source);
						}
						else if (source==(AbstractComponent)joinGameButton){
							System.out.println("Player "+popupEnterName.getText()+" will join");
							
							// GO TO JOIN MODE
							GUI.playerName = popupEnterName.getText();							
							game.enterState(2);	
							setMenuEnable(true);
						}
						else {
							System.out.println("SOMETHING'S WRONG");
						}
					}
				},
				new ComponentListener() {
					@Override
					public void componentActivated(AbstractComponent source) {	// cancel action
						setMenuEnable(true);
					}
				});
		popupEnterName.setMaxLength(16);
		
		
		
		
		
		
		hostGameButton = new Button(container, GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_lightblue.png",
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_lightblue_down.png",
				menuPanelPosition[0]+hostGameButtonOffset[0],
				menuPanelPosition[1]+hostGameButtonOffset[1],
				new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent source) {
						System.out.println("host game button pressed!");
						showPopupEnterName(source);
					}
				});
		//hostGameButton.setAlphaWhileDisabled(1.0f);
		
		joinGameButton = new Button(container, GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_lightblue.png",
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_lightblue_down.png",
				menuPanelPosition[0]+joinGameButtonOffset[0],
				menuPanelPosition[1]+joinGameButtonOffset[1],
				new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent source) {
						
						System.out.println("join game button pressed!");
						showPopupEnterName(source);
					}
				});
		//joinGameButton.setAlphaWhileDisabled(1.0f);
		/*
		spectateGameButton = new Button(container, GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_lightblue.png",
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_lightblue_down.png",
				menuPanelPosition[0]+spectateGameButtonOffset[0],
				menuPanelPosition[1]+spectateGameButtonOffset[1],
				new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent source) {
						
						System.out.println("spectate game button pressed!");
						//showPopupLoading(source);
					}
				});
		//spectateGameButton.setAlphaWhileDisabled(1.0f);
		*/
		exitButton = new Button(container, GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_darkred.png",
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_darkred_down.png",
				menuPanelPosition[0]+exitButtonOffset[0],
				menuPanelPosition[1]+exitButtonOffset[1],
				new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent source) {
						
						System.out.println("exit button pressed!");
						setMenuEnable(false);
						popupConfirmExit.setVisible(source);
					}
				});
		//exitButton.setAlphaWhileDisabled(1.0f);

	}
	
	private void setMenuEnable(boolean enable) {
		hostGameButton.setEnable(enable);
		joinGameButton.setEnable(enable);
		//spectateGameButton.setEnable(enable);
		exitButton.setEnable(enable);
	}
	
	protected void showPopupEnterName(AbstractComponent source) {
		setMenuEnable(false);
		popupEnterName.setVisible(source);
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		/*
		// temporary method for transitioning between modes
		if (container.getInput().isKeyPressed(Input.KEY_2))
			game.enterState(2);
		else if (container.getInput().isKeyPressed(Input.KEY_3))
			game.enterState(3);
		else if (container.getInput().isKeyPressed(Input.KEY_4))
			game.enterState(4);
		*/
		
		
		// on-enter-mode actions
		if (GUI.currentMode != 1) {
			GUI.currentMode = 1;
		}
		
		
		// if loading screen is up, check status of whatever's loading
		if (popupLoading.isVisible()) {
			AbstractComponent source = popupLoading.getPopupSource();
			if (source==hostGameButton) {
				if (startHostSuccess_flag){
					popupLoading.setInvisible();
					setMenuEnable(true);
					game.enterState(3);
				} else if (startHostError_flag) {
					popupLoading.setInvisible();
					setMenuEnable(false);
					popupFailedHost.setVisible(source);
				}
			} else {
				System.out.println("SOMETHING'S WRONG");
			}
		}
		
		/*
		// temporary method for stopping the loading screen
		if (container.getInput().isKeyPressed(Input.KEY_F)) {
			if (popupMessageAnimation.isVisible()) {
				popupMessageAnimation.setInvisible();
				setMenuEnable(true);
			}
		}*/
	}
	

	
	
	
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		
		background.draw(0, 0, container.getWidth(), container.getHeight());
		
		drawMenuPanel(g);
		
		drawMenuButtons(container, g);

		popupConfirmExit.render(container, g);
		popupEnterName.render(container, g);
		popupLoading.render(container, g);
		popupFailedHost.render(container, g);
	}
	
	private void drawMenuPanel(Graphics g) {
		g.setColor(new Color(64, 64, 64, 160));
		g.fillRoundRect(menuPanelPosition[0], menuPanelPosition[1],
				menuPanelSize[0], menuPanelSize[1], 4);
	}
	
	private void drawMenuButtons(GUIContext container, Graphics g) {
		g.setColor(Color.white);
		
		hostGameButton.render(container, g, buttonFont, Color.white, "Host Game");
		joinGameButton.render(container, g, buttonFont, Color.white, "Join Game");
		//spectateGameButton.render(container, g, buttonFont, Color.white, "Spectate Game");
		exitButton.render(container, g, buttonFont, Color.white, "Exit");
	}
	
	
	public int getID() {
		return 1;
	}
}


/*
portTextField = new MyTextField(container, portTextFieldFont, menuPanelPosition[0]+portTextFieldOffset[0],
		menuPanelPosition[1]+portTextFieldOffset[1], portTextFieldSize[0], portTextFieldSize[1]);
portTextField.setBackgroundColor(new Color(255, 255, 255, 32));
portTextField.setBorderColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
portTextField.setTextColor(Color.white);
portTextField.setMaxLength(2);
portTextField.setNumeralsOnly(true);
portTextField.setAcceptingInput(true);
*/
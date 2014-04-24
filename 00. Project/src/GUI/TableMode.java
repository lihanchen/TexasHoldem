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
import org.newdawn.slick.state.StateBasedGame;

public class TableMode extends Mode {

	protected Image background;	
	protected TrueTypeFont infoFont;
	
	protected final int leaveButtonPosition[] = {850, 25};
	
	protected final int mainPanelPosition[] = {250, 430};
	protected final int mainPanelSize[] = {500, 150};
	protected final int mainNameOffset[] = {250, 15};
	protected final int[] mainPanelLabelOffset = {250, 80};
	protected final int[] mainLabelOffset = {400, 5};
	protected final int[] mainLabelSize = {95, 20};
	
	protected final int playerPanelPositions[][] = {{-1, -1},	// 0th entry not used
											{25, 290},
											{25, 100},
											{215, 10},
											{415, 10},
											{615, 10},
											{805, 100},
											{805, 290}};
	protected final int playerPanelSize[] = {170, 140};
	protected final int playerNameOffset[] = {85, 15};
	protected final int[] playerPanelLabelOffset = {85, 75};
	
	protected final int[] playerLabelOffset = {0, 62};
	protected final int[] playerLabelSize = {170, 40};
	
	protected final String leaveConfirmString = "Are you sure you want to leave?";
	protected final String hostConnectionLostString = "Lost connection to host process!";
	
	private TrueTypeFont mainLabelFont;
	private TrueTypeFont playerLabelFont;
	
	
	protected PopupMessageOneButton popupHostConnectionLost;
	protected PopupMessageTwoButtons popupLeaveConfirm;
	
	protected TrueTypeFont leaveButtonFont;
	protected Button leaveButton;
	
	
	@Override
	public void init(GameContainer container, final StateBasedGame game) throws SlickException {
		
		super.init(container, game);
		
		// load background image of table surface
		background = new Image(GUI.RESOURCES_PATH + "table_background.jpg");
		
		// load font
		infoFont = new TrueTypeFont(new java.awt.Font("Segoe UI Semibold", Font.PLAIN, 16), true);
		mainLabelFont = new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 12), true);
		playerLabelFont = new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 22), true);
		leaveButtonFont = new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 20), true);
		
		
		
		popupHostConnectionLost = new PopupMessageOneButton(container, hostConnectionLostString,
				new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent arg0) {	// ok action
						// no need to re-enable any buttons
						GUI.cmh.close();
						GUI.cmh = null;
						game.enterState(1);	// go back to StartMode
					}
				});
	}

	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
	}
	
	protected int hostToLocalIndex(int hostIndex) {
		return (hostIndex + 8 - GUI.playerIndexInHost) % 8;
	}
	protected int localToHostIndex(int localIndex) {
		return (localIndex + GUI.playerIndexInHost) % 8;
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// draw background
		background.draw(0, 0, container.getWidth(), container.getHeight());
		
		// draw leave button
		leaveButton.render(container, g, leaveButtonFont, Color.white, "Leave");
		
		drawPanels(g);
	}

	private void drawPanels(Graphics g) {
		
		g.setColor(new Color(32, 32, 32, 128));
		
		g.fillRoundRect(mainPanelPosition[0], mainPanelPosition[1],
				mainPanelSize[0], mainPanelSize[1], 0);	// mainplayer rect
		
		for (int i=1; i<8; ++i) {
			g.fillRoundRect(playerPanelPositions[i][0], playerPanelPositions[i][1],
					playerPanelSize[0], playerPanelSize[1], 0);
		}
	}
	
	protected void drawPlayerLabel(Graphics g, int player, String s,
			Color textColor, Color labelColor) {
		if (player==0) {
			g.setColor(labelColor);
			g.fillRoundRect(mainPanelPosition[0]+mainLabelOffset[0],
					mainPanelPosition[1]+mainLabelOffset[1],
					mainLabelSize[0], mainLabelSize[1], 0);
			
			GUI.drawStringCenter(g, mainLabelFont, textColor, s,
					mainPanelPosition[0]+mainLabelOffset[0]+mainLabelSize[0]/2,
					mainPanelPosition[1]+mainLabelOffset[1]+mainLabelSize[1]/2);
		} else {
			g.setColor(labelColor);
			g.fillRoundRect(playerPanelPositions[player][0]+playerLabelOffset[0],
					playerPanelPositions[player][1]+playerLabelOffset[1],
					playerLabelSize[0], playerLabelSize[1], 0);
			
			GUI.drawStringCenter(g, playerLabelFont, textColor, s,
					playerPanelPositions[player][0]+playerLabelOffset[0]+playerLabelSize[0]/2,
					playerPanelPositions[player][1]+playerLabelOffset[1]+playerLabelSize[1]/2);
		}
	}
	
	@Override
	public int getID() {
		return -1;
	}

}

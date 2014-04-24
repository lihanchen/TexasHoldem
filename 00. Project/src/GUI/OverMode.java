package GUI;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class OverMode extends BasicGameState
{
	private Image background;
	
	private TrueTypeFont msgFont;
	private TrueTypeFont buttonFont;

	private int[] msgOffset = {500, 200};
	private int[] backToMenuButtonOffset = {200, 400};
	private int[] spectateButtonOffset= {600, 400};
	
	private String gameOverWinString = "You won!";
	private String gameOverLoseString = "You lost!";
	private Button backToMenuButton;
	private Button spectateButton;
	
	
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		
		// load background image of table surface
		background = new Image(GUI.RESOURCES_PATH + "table_background.jpg");
		
		// load fonts
		buttonFont = new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 20), true);
		msgFont = new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 54), true);
		
		
		// make buttons
		backToMenuButton = new Button(container, GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_lightbluebig.png",
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_lightbluebig_down.png",
				backToMenuButtonOffset[0], backToMenuButtonOffset[1],
				new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent source) {
						
						System.out.println("back to menu button pressed!");
						
					}
				});
		
		spectateButton = new Button(container, GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_lightbluebig.png",
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_lightbluebig_down.png",
				spectateButtonOffset[0], spectateButtonOffset[1],
				new ComponentListener() {
					
					@Override
					public void componentActivated(AbstractComponent source) {
						
						System.out.println("spectate button pressed!");
						
					}
				});
	}
	
	
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

		
	}
	
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		
		// draw background
		background.draw(0, 0, container.getWidth(), container.getHeight());
		
		GUI.drawStringCenter(g, msgFont, Color.white, gameOverLoseString, msgOffset[0], msgOffset[1]);
		
		backToMenuButton.render(container, g, buttonFont, Color.white, "Back to Main Menu");
		spectateButton.render(container, g, buttonFont, Color.white, "Spectate This Game");
	}

	public int getID() {
		return 5;
	}
}

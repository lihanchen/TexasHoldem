package GUI;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


public class Mode extends BasicGameState {
	
	// elements common to all modes
	
	protected final String loadingString = "Loading...";
	
	protected Animation waitingAnimation;
	protected PopupMessageAnimation popupLoading;
	
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		
		waitingAnimation = new Animation();
		SpriteSheet sheet = new SpriteSheet(GUI.RESOURCES_PATH+"loading2.png", 32, 32);
		for (int i=0; i<8; ++i) {
			waitingAnimation.addFrame(sheet.getSprite(i, 0), 64);
		}
		popupLoading = new PopupMessageAnimation(loadingString, waitingAnimation);
	}

	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		
	}
	
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		
	}

	@Override
	public int getID() {
		return -1;
	}

}

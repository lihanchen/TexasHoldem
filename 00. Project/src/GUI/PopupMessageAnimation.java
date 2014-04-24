package GUI;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;

public class PopupMessageAnimation extends PopupMessage {

	private static final int[] animationOffset = {500, 200};
	private Animation animation;
	
	public PopupMessageAnimation(String messageString, Animation animation)
			throws SlickException {
		
		super(messageString);
		
		this.animation = animation;
	}
	
	@Override
	public void render(GUIContext container, Graphics g) {
		super.render(container, g);
		if (visible) {
			animation.draw(position[0]+animationOffset[0]-animation.getWidth()/2,
					position[1]+animationOffset[1]-animation.getHeight()/2);
		}
	}
}

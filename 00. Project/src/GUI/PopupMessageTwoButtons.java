package GUI;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;

public class PopupMessageTwoButtons extends PopupMessageOneButton {

	protected static final int[] cancelButtonOffset = {600, 200};
	private Button cancelButton;

	
	public PopupMessageTwoButtons(GUIContext container,	String messageString,
			ComponentListener onOkListener,
			ComponentListener onCancelListener) throws SlickException {
		
		super(container, messageString, onOkListener);
		
		cancelButton = new Button(container,
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_red.png",
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_red_down.png",
				position[0]+cancelButtonOffset[0],
				position[1]+cancelButtonOffset[1],
				new ExitListener(onCancelListener));
		
		cancelButton.setEnable(false);
	}
	
	@Override
	public void setVisible(AbstractComponent source) {
		super.setVisible(source);
		cancelButton.setEnable(true);
	}
	
	@Override
	public void setInvisible() {
		super.setInvisible();
		cancelButton.setEnable(false);
	}
	
	@Override
	public void render(GUIContext container, Graphics g) {
		super.render(container, g);
		
		if (visible) {
			cancelButton.render(container, g, buttonFont, Color.white, "Cancel");
		}
	}
}

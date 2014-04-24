package GUI;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.SlickException;

public class PopupMessageOneButton extends PopupMessage {

	protected static final TrueTypeFont buttonFont =
			new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 20), true);
	
	protected static final int[] okButtonOffset = {770, 200};
	
	
	protected Button okButton;
	
		
	public PopupMessageOneButton(GUIContext container, String messageString,
			ComponentListener onOkListener) throws SlickException {
		
		super(messageString);
				
		okButton = new Button(container,
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_green.png",
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_green_down.png",
				position[0]+okButtonOffset[0],
				position[1]+okButtonOffset[1],
				new ExitListener(onOkListener));
		
		okButton.setEnable(false);	
	}
	
	@Override
	public void setVisible(AbstractComponent source) {
		super.setVisible(source);
		okButton.setEnable(true);
	}
	
	@Override
	public void setInvisible() {
		super.setInvisible();
		okButton.setEnable(false);
	}
	
	@Override
	public void render(GUIContext container, Graphics g) {
		super.render(container, g);
		if (visible) {
			okButton.render(container, g, buttonFont, Color.white, "OK");
		}
	}
}

package GUI;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;

public class PopupMessage {
	
	protected static final int[] position = {0, 150};
	protected static final int[] size = {1000, 300};
	
	protected static final TrueTypeFont messageFont =
			new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 28), true);
	
	
	protected final int[] messageStringOffset = {500, 75};
	protected String messageString;
	
	protected AbstractComponent popupSource;
	
	protected boolean visible;
	
	
	protected class ExitListener implements ComponentListener {
		private ComponentListener listener;
		public ExitListener(ComponentListener listener) {this.listener=listener;}
		@Override
		public void componentActivated(AbstractComponent source) {
			setInvisible();
			// provided on-exit listener will be give source that caused the popup,
			// not the source that caused the exit (which is just okButton)
			listener.componentActivated(popupSource);
		}
	}
	
	public PopupMessage(String messageString) throws SlickException {
		this.messageString = messageString;
		visible = false;
	}
	
	public void setMessageString(String messageString) {
		this.messageString = messageString;
	}
	
	public void setVisible(AbstractComponent source) {
		popupSource = source;
		visible = true;
	}
	
	public AbstractComponent getPopupSource() {
		return popupSource;
	}
	
	public void setInvisible() {
		visible = false;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void render(GUIContext container, Graphics g) {
		if (visible) {
			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.85f));
			g.fillRect(position[0], position[1], size[0], size[1]);
			
			GUI.drawStringCenter(g, messageFont, Color.white, messageString,
					position[0]+messageStringOffset[0], position[1]+messageStringOffset[1]);
		}
	}
}

package GUI;


import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;


public class Button extends MyMouseOverArea{

	private float alphaWhileDisabled;
	
	
	public Button(GUIContext container, 
				String normalImagePath, String pressedImagePath, int x, int y,
				ComponentListener listener) throws SlickException {
		
		this(container, new Image(normalImagePath), new Image(pressedImagePath),
				x, y, listener);
	}
	
	public Button(GUIContext container, 
			Image normalImage, Image pressedImage, int x, int y,
			ComponentListener listener) throws SlickException {
	
	super(container, normalImage, x, y, listener);

	setMouseDownImage(pressedImage);
	setNormalColor(new Color(0.9f, 0.9f, 0.9f, 1.0f));
	setMouseOverColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
	setMouseDownColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
	setAcceptingInput(true);

	alphaWhileDisabled = 0.5f;
}
	
	
	
	/*
	public void setOnlyListener(ComponentListener listener) {
		super.removeAllListeners();
		super.addListener(listener);
	}
	*/
	
	public void setEnable(boolean enable) {
		if (enable && !super.isAcceptingInput()) {
			setNormalColor(new Color(0.9f, 0.9f, 0.9f, 1.0f));
			setAcceptingInput(true);
		}
		else if (!enable && super.isAcceptingInput()) {
			setAcceptingInput(false);
			setNormalColor(new Color(0.9f, 0.9f, 0.9f, alphaWhileDisabled));
		}
	}
		
	public boolean getEnable() {
		return isAcceptingInput();
	}
	
	public void setAlphaWhileDisabled(float alpha) {
		alphaWhileDisabled = alpha;
		if (!isAcceptingInput()) {
			setNormalColor(new Color(0.9f, 0.9f, 0.9f, alphaWhileDisabled));
		}
	}
	
	public float getAlphaWhileDisabled() {
		return alphaWhileDisabled;
	}
	
	public void render(GUIContext container, Graphics g,  TrueTypeFont font, Color c, String s) {
		
		super.render(container, g);
		
		// calculate where string should be drawn
		int x = getX() + (getWidth()-font.getWidth(s))/2;
		int y = getY() + (getHeight()-font.getHeight(s))/2;
		
		if (!isAcceptingInput()) {	// draw string at half alpha if button inactive
			font.drawString(x, y, s, c.multiply(new Color(1.0f, 1.0f, 1.0f, alphaWhileDisabled)));
		}
		else {
			font.drawString(x, y, s);
		}
	}
}

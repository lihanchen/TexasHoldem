package GUI;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.GUIContext;

public class RaiseTextField extends MyTextField{
	
	
	private TrueTypeFont raiseByFont;
	private TrueTypeFont dollarSignFont;
	
	private float alphaWhileDisabled;
	
	private String raiseByString;
	

	public RaiseTextField(GUIContext container, int x, int y) {
		
		super(container, new TrueTypeFont(new java.awt.Font("Segoe UI Semibold", Font.PLAIN, 16), true), 
				x+15, y+19, 75, 26);
		setBackgroundColor(new Color(1.0f, 1.0f, 1.0f, 0.2f));
		setBorderColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		setTextColor(Color.white);
		setMaxLength(6);
		setNumeralsOnly(true);
		setAcceptingInput(true);
		
		raiseByFont = new TrueTypeFont(new java.awt.Font("Segoe UI", Font.ITALIC, 12), true);
		dollarSignFont = new TrueTypeFont(new java.awt.Font("Segoe UI Semibold", Font.PLAIN, 18), true);
		
		alphaWhileDisabled = 0.5f;
		
		raiseByString = "  Bet:";
	}
	
	
	public void setRaiseByString(String raiseByString) {
		this.raiseByString = raiseByString;
	}
	
	public void setEnable(boolean enable) {
		
		if (enable && !isAcceptingInput()) {
			setAcceptingInput(true);
			setBackgroundColor(new Color(1.0f, 1.0f, 1.0f, 0.2f));
			setTextColor(Color.white);
		}
		else if (!enable && isAcceptingInput()) {
			setBackgroundColor(new Color(1.0f, 1.0f, 1.0f, 0.2f*alphaWhileDisabled));
			setTextColor(new Color(1.0f, 1.0f, 1.0f, alphaWhileDisabled));
			setFocus(false);
			setAcceptingInput(false);
		}
	}
	
	public boolean getEnable() {
		return isAcceptingInput();
	}
	
	public float getAlphaWhileDisabled() {
		return alphaWhileDisabled;
	}

	public void setAlphaWhileDisabled(float alpha) {
		alphaWhileDisabled = alpha;
		if (!isAcceptingInput()) {
			setBackgroundColor(new Color(1.0f, 1.0f, 1.0f, 0.2f*alphaWhileDisabled));
			setTextColor(new Color(1.0f, 1.0f, 1.0f, alphaWhileDisabled));
		}
	}
	
	@Override
	public void render(GUIContext container, Graphics g) {
		super.render(container, g);
		Color c;
		if (isAcceptingInput())
			c = new Color(1.0f, 1.0f, 1.0f, 1.0f);
		else
			c = new Color(1.0f, 1.0f, 1.0f, alphaWhileDisabled);
		
		dollarSignFont.drawString(-15+x, -19+y+32-dollarSignFont.getHeight()/2, "$", c);
		raiseByFont.drawString(-15+x+10, -19+y, raiseByString, c);
	}
	
	
}

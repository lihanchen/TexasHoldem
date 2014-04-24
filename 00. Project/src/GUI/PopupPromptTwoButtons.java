package GUI;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;

public class PopupPromptTwoButtons extends PopupMessageTwoButtons{


	//private final int[] messageStringOffset = {500, 75};
	private static final int[] textFieldOffset = {280, 110};
	private static final int[] textFieldSize = {440, 45};

	
	private static final TrueTypeFont textFieldFont =
			new TrueTypeFont(new java.awt.Font("Segoe UI", Font.PLAIN, 28), true);
	

	private MyTextField textField;
	
	

	public PopupPromptTwoButtons(GUIContext container, String messageString,
			ComponentListener onOkListener,
			ComponentListener onCancelListener) throws SlickException {
		
		super(container, messageString, onOkListener, onCancelListener);
		
		
		textField = new MyTextField(container, textFieldFont, position[0]+textFieldOffset[0],
				position[1]+textFieldOffset[1], textFieldSize[0], textFieldSize[1]);
		textField.setBackgroundColor(new Color(255, 255, 255, 32));
		textField.setBorderColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		textField.setTextColor(Color.white);
		textField.setOnTextChangeListener(new ComponentListener() {
			@Override
			public void componentActivated(AbstractComponent source) {

				okButton.setEnable(visible && !textField.getText().isEmpty());
			}
		});
		
		textField.setAcceptingInput(false);
	}
	
	public void setMaxLength(int length) {
		textField.setMaxLength(length);
	}
	
	@Override
	public void setVisible(AbstractComponent source) {
		super.setVisible(source);
		textField.setAcceptingInput(true);
		okButton.setEnable(!textField.getText().isEmpty());
	}
	
	@Override
	public void setInvisible() {
		super.setInvisible();
		textField.setAcceptingInput(false);
	}
	
	
	public String getText() {
		return textField.getText();
	}
	
	@Override
	public void render(GUIContext container, Graphics g) {
		super.render(container, g);
		
		if (visible) {
			g.setColor(Color.white);
			textField.render(container, g);
		}
	}
}

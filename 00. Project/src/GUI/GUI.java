package GUI;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;

import Network.ClientMessageHandler;


public class GUI extends StateBasedGame
{
	public static final String RESOURCES_PATH = "./resources/";
	public static final String CARDSPRITES_FOLDER = "cardsprites2/";
	public static final String BUTTONS_FOLDER = "buttons/";
	
	public static StartMode startMode;
	public static JoinMode joinMode;
	public static LobbyMode lobbyMode;
	public static OngoingMode ongoingMode;
	
	public static int currentMode;
	
	
	public static ClientMessageHandler cmh;
	public static String hostIpString;
	
	
	protected static String playerName;
	protected static int playerIndexInHost = -1;
	public static boolean hostConnectionError_flag;
	
	public GUI(String name) {
		super(name);		
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException
	{
		hostConnectionError_flag = false;
		
		startMode = new StartMode();
		joinMode = new JoinMode();
		lobbyMode = new LobbyMode();
		ongoingMode = new OngoingMode();
		
		currentMode = -1;
		
		this.addState(startMode);
		this.addState(joinMode);
		this.addState(lobbyMode);
		this.addState(ongoingMode);
		//this.addState(new OverMode());
	}
	
	
	protected static void drawStringCenter(Graphics g, TrueTypeFont font, Color c, String s, int x, int y) {
		if (s != null) {
			font.drawString(x-font.getWidth(s)/2, y-font.getHeight(s)/2, s, c);
		}
	}
	
	protected static void drawStringLeftCenter(Graphics g, TrueTypeFont font, Color c, String s, int x, int y) {
		if (s!=null)
			font.drawString(x, y-font.getHeight(s)/2, s, c);
	}
	
	protected static void drawStringRightCenter(Graphics g, TrueTypeFont font, Color c, String s, int x, int y) {
		if (s!=null)
			font.drawString(x-font.getWidth(s), y-font.getHeight(s)/2, s, c);
	}
}
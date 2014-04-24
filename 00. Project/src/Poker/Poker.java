package Poker;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import GUI.GUI;



public class Poker {
	
	public static void main(String[] args) throws Exception {
		//new musics().start();
		try {
			
			AppGameContainer app = new AppGameContainer(new GUI("Poker"));
			app.setDisplayMode(1000, 600, false);
			app.setIcon(GUI.RESOURCES_PATH+"icon.png");
			//app.setVSync(true);
			app.setTargetFrameRate(60);
			app.start();
		}
		catch (SlickException e) {
			System.out.println("ERROR: GUI could not be started");
		}
	
	}	
}

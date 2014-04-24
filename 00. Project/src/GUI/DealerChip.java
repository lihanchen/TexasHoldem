package GUI;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class DealerChip {

	private Image chip;
	private double currX, currY;
	private double destX, destY;
	private final double moveSpeed = 0.7;	// pixels per ms
	
	private int[][] dealerChipPositions;
	
	public DealerChip(int[][] dealerChipPositions, int initialDealer) throws SlickException {
		this.dealerChipPositions = dealerChipPositions;
		chip = new Image(GUI.RESOURCES_PATH + "dchip25.png");
		
		currX = dealerChipPositions[initialDealer][0];
		currY = dealerChipPositions[initialDealer][1];
		destX = currX;
		destY = currY;
	}
	
	public void moveTo(int dealer) {
		destX = dealerChipPositions[dealer][0];
		destY = dealerChipPositions[dealer][1];
	}
	
	public boolean isMoving() {
		return (currX!=destX || currY!=destY);
	}
	
	public void update(double delta) {
		// update screen position
		if (currX != destX || currY != destY) {
			double dX = destX - currX;
			double dY = destY - currY;
			double distToDest = java.lang.Math.sqrt(dX*dX+dY*dY);
			double dDist = delta * moveSpeed;
			
			if (dDist >= distToDest) {
				currX = destX;
				currY = destY;
			}
			else {
				currX += dX / distToDest * dDist;
				currY += dY / distToDest * dDist;
			}
		}
	}
	
	public void draw() {
		chip.draw((int)currX, (int)currY);
	}
}

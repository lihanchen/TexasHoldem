package GUI;

import java.util.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import Poker.Music;

public class Card {
	
	private static Set<Card> movingCards = new HashSet<Card>();
	
	private Image backImage;
	
	// face image
	private Image currFaceImage;
	private Image destFaceImage;
	
	// screen position
	private double currX;
	private double currY;
	private double destX;
	private double destY;
	private final double moveSpeed = 1.0;	// pixels per ms
	
	// face up or down
	private boolean currFaceUp;
	private boolean destFaceUp;
	private double theta;	// iterates 0 to 180
	private final double thetaSpeed = 0.5;
	
	// visibility
	private double currAlpha;
	private double destAlpha;
	private final double alphaSpeed = 0.003;	// alpha per ms
	
	private boolean isMoving;
	
	
	public Card(Image backImage, Image faceImage, int[] initialPosition, boolean initialVisible) {
		this(backImage, faceImage, initialPosition[0], initialPosition[1], initialVisible);
	}
	
	private Card(Image backImage, Image faceImage, int initialX, int initialY, boolean initialVisible) {
		
		currFaceImage = faceImage;
		destFaceImage = currFaceImage;
		
		this.backImage = backImage;
		
		currX = initialX;
		currY = initialY;
		destX = currX;
		destY = currY;
		
		currFaceUp = false;
		destFaceUp = currFaceUp;
		theta = 0.0;
		
		currAlpha = initialVisible ? 1.0 : 0.0;	// card initially not visible
		destAlpha = currAlpha;
		
		isMoving = false;
	}
	
	
	public void setFaceImage(Image faceImage) {
		destFaceImage = faceImage;
	}
	
	private boolean isFaceVisible() {		
		if (!isMoving)
			return currFaceUp;
		
		// if we're moving...
		if (destFaceUp)
			return theta > 90.0;
		
		return theta < 90.0;
	}
	
	
	
	public void setFaceImage(Host.GameSystem.Card card) {
		int suit = card.getKind()-1;
		int value = card.getNumber();
		setFaceImage(Cards.cardFaces[suit][value]);
	}
	
	// to animate card
	public void setState(int[] destPosition, Boolean visible, Boolean faceUp,
			boolean instant) {
		
		boolean playSound = false;
		
		if (destPosition!=null && !(destPosition[0]==currX && destPosition[1]==currY)) {
			destX = destPosition[0];
			destY = destPosition[1];
			isMoving = true;
			playSound = true;
		}
		if (faceUp!=null && faceUp!=currFaceUp) {
			destFaceUp = faceUp;
			isMoving = true;
			playSound = true;
		}
		if (visible!=null) {
			double alpha = visible.booleanValue() ? 1.0 : 0.0;
			if (alpha!=currAlpha) {
				destAlpha = alpha;
				isMoving = true;
			}
		}
		if (instant) {
			currX = destX;
			currY = destY;
			currFaceUp = destFaceUp;
			currAlpha = destAlpha;
			isMoving = false;
			playSound = false;
		}
		
		if (isMoving)
			movingCards.add(this);
		else
			movingCards.remove(this);
		
		if (playSound)
			Music.twoCardsSound();
	}
	
	
	// poll card state
	public boolean isMoving() {
		return isMoving;
	}
	public boolean isVisible() {
		return currAlpha >= 0.5;
	}
	public boolean isFaceUp() {
		return currFaceUp;
	}
	public static boolean areAnyMoving() {
		return !movingCards.isEmpty();
	}
	
	// updates current screen position of card if it's not yet at its destination
	public void update(double delta) {
		
		if (!isFaceVisible())
			currFaceImage = destFaceImage;
		
		if (isMoving) {
			
			// update screen position
			boolean positionChanging = false;
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
					positionChanging = true;
				}
			}
			
			// update card visibility
			boolean alphaChanging = false;
			if (currAlpha < destAlpha) {
				double dAlpha = delta * alphaSpeed;
				if (dAlpha >= destAlpha-currAlpha) {
					currAlpha = destAlpha;
				}
				else {
					currAlpha += dAlpha;
					alphaChanging = true;
				}
			}
			else if (currAlpha > destAlpha) {
				double dAlpha = delta * alphaSpeed;
				if (dAlpha >= currAlpha-destAlpha) {
					currAlpha = destAlpha;
				}
				else {
					currAlpha -= dAlpha;
					alphaChanging = true;
				}
			}
			
			// update card theta
			boolean thetaChanging = false;
			if (currFaceUp != destFaceUp) {
				double dTheta = delta * thetaSpeed;
				if (dTheta >= 180.0-theta) {
					theta = 0.0;
					currFaceUp = destFaceUp;
				}
				else {
					theta += dTheta;
					thetaChanging = true;
				}
			}
			
			if (!positionChanging && !alphaChanging && !thetaChanging) {
				if (isMoving)
					movingCards.remove(this);
				isMoving = false;
			}
		}
	}
	
	
	// draws the card
	public void draw() {
		
		if (currAlpha==0.0) {
			return;
		}
		
		// determine which side of card is visible right now
		Image visible = (currFaceUp==(theta<=90.0)) ? currFaceImage : backImage;
				
		if (theta == 0.0) {
			visible.draw((int)currX, (int)currY, new Color(1.0f, 1.0f, 1.0f, (float)currAlpha));
		}
		else {
			double s = java.lang.Math.sin(java.lang.Math.toRadians(theta));
			double perspectiveScale = 1.0 + 0.2*s;
			double c = java.lang.Math.abs(java.lang.Math.cos(java.lang.Math.toRadians(theta)));
			
			// calculate card center and image half-dimensions
			double leftNdcX, leftNdcY, rightNdcX, rightNdcY;
			
			if (theta <= 90.0) {
				// left edge up, right edge sliding leftward
				leftNdcX = perspectiveScale;
				leftNdcY = perspectiveScale;
				rightNdcX = 2.0*c-1.0;	// transform [0,1] to [-1,1]
				rightNdcY = 1.0;
			}
			else {
				// right edge up, right edge sliding rightward
				leftNdcX = 1.0;
				leftNdcY = 1.0;
				rightNdcX = (2.0*c-1.0)*perspectiveScale;
				rightNdcY = perspectiveScale;
			}
			/*
			if (theta <= 90.0) {
				// left edge up, both edges slide inward
				leftNdcX = c*perspectiveScale;
				leftNdcY = perspectiveScale;
				rightNdcX = c;
				rightNdcY = 1.0;
			}
			else {
				// right edge up, right edge sliding rightward
				leftNdcX = c;
				leftNdcY = 1.0;
				rightNdcX = c*perspectiveScale;
				rightNdcY = perspectiveScale;
			}*/
			double halfWidth = (double)visible.getWidth() / 2.0;
			double halfHeight = (double)visible.getHeight() / 2.0;
			double centerX = currX + halfWidth;
			double centerY = currY + halfHeight;
			
			visible.drawWarped(
					(float)(centerX - leftNdcX*halfWidth), (float)(centerY - leftNdcY*halfHeight),
					(float)(centerX - leftNdcX*halfWidth), (float)(centerY + leftNdcY*halfHeight),
					(float)(centerX + rightNdcX*halfWidth), (float)(centerY + rightNdcY*halfHeight),
					(float)(centerX + rightNdcX*halfWidth), (float)(centerY - rightNdcY*halfHeight)
			);
		}
	}
}

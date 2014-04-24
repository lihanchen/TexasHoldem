package GUI;

import java.util.*;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Cards {

	
	private class QueuedAction {
		private double waitTime;
		private boolean letPrevActionsFinish;
		private boolean started;
		
		private Card card;
		private int[] position;
		private Boolean visible;
		private Boolean faceUp;
		private boolean instant;
		
		private QueuedAction(Card card, int[] position, Boolean visible, Boolean faceUp,
				boolean instant, double waitTime, boolean letPrevActionsFinish) {
			this.card = card;
			this.position = position;
			this.visible = visible;
			this.faceUp = faceUp;
			this.waitTime = waitTime;
			this.instant = instant;
			this.letPrevActionsFinish = letPrevActionsFinish;
			started = false;
		}
		private double countDown(double delta) {
			double remainingDelta;
			if (!(letPrevActionsFinish && Card.areAnyMoving())) {
				if (delta >= waitTime) {
					if (!started) {
						card.setState(position, visible, faceUp, instant);
						started = true;
					}
					waitTime = 0.0;
					remainingDelta = delta - waitTime;
				} else {
					waitTime -= delta;
					remainingDelta = 0.0;
				}
			} else {
				remainingDelta = 0.0;
			}
			return remainingDelta;
		}
		private boolean hasExecuted() {
			return started;
		}
	}
	
	
	private final int[] deckPosition = {463, 230};
	private final int[][] centerCardPositions = {{283, 230}, {373, 230},
			{463, 230}, {553, 230}, {643, 230}};
	private int[][][] playerCardPositions;
	
	
	protected static Image[][] cardFaces;
	private static Image cardBack;
	
	protected Card[] centerCards;
	protected Card[][] playerCards;
	

	private Card[] playerCardsDealOrder;
	
	private Queue<QueuedAction> actionQueue;
	
	
	
	public Cards(int[][][] playerCardPositions) throws SlickException {
		
		this.playerCardPositions = playerCardPositions;
		
		
		// load 52 card face images
		cardFaces = new Image[4][14];	// extra column so card values match index
		String indexStr;
		for (int i=1; i<=13; ++i) {
			indexStr = String.format("%02d", i);
			cardFaces[0][i] = new Image(GUI.RESOURCES_PATH+GUI.CARDSPRITES_FOLDER+"c"+indexStr+".png");
			cardFaces[1][i] = new Image(GUI.RESOURCES_PATH+GUI.CARDSPRITES_FOLDER+"h"+indexStr+".png");
			cardFaces[2][i] = new Image(GUI.RESOURCES_PATH+GUI.CARDSPRITES_FOLDER+"d"+indexStr+".png");
			cardFaces[3][i] = new Image(GUI.RESOURCES_PATH+GUI.CARDSPRITES_FOLDER+"s"+indexStr+".png");
		}
		// load card back
		cardBack = new Image(GUI.RESOURCES_PATH+GUI.CARDSPRITES_FOLDER+"BackBlue.png");
				
		
		// initialize cards
		//Image initialFaceImage = cardFaces[0][1];
		centerCards = new Card[5];
		for (int i=0; i<5; ++i) {
			centerCards[i] = new Card(cardBack, cardFaces[1][i+1], deckPosition, false);
		}
		playerCards = new Card[8][2];
		for (int i=0; i<8; ++i) {
			playerCards[i][0] = new Card(cardBack, cardFaces[0][i+1], deckPosition, false);
			playerCards[i][1] = new Card(cardBack, cardFaces[2][i+1], deckPosition, false);
		}
				
		actionQueue = new ArrayDeque<QueuedAction>();
		
		// initial deal order shouldn't matter
		playerCardsDealOrder = new Card[16];
		for (int i=0; i<8; ++i) {
			playerCardsDealOrder[2*i] = playerCards[i][0];
			playerCardsDealOrder[2*i+1] = playerCards[i][1];
		}
	}
	
	public boolean actionQueueEmpty() {
		return actionQueue.isEmpty();
	}
	
	
	
	// set all cards invisible, facedown, at deck position instantaneously
	public void resetCards() {
		for (int i=0; i<5; ++i) {
			centerCards[i].setState(deckPosition, false, false, true);
		}
		for (int i=0; i<8; ++i) {
			playerCards[i][0].setState(deckPosition, false, false, true);
			playerCards[i][1].setState(deckPosition, false, false, true);
		}
	}
	
	// QueueAction params:
	// card, 
	// position, visible, faceUp, instant, waitTime, letPrevActionsFinish
	
	public void collectCards(double waitTime) {
		
		System.out.println("###GUI ACTION: collect cards");
		
		// add a spacer to let previous actions finish
		actionQueue.add(new QueuedAction(centerCards[0],
				null, null, null, false, waitTime, true));
		
		// flip all cards face down
		for (int i=0; i<5; ++i) {
			actionQueue.add(new QueuedAction(centerCards[i],
					null, null, false, false, 0.0, false));
		}
		for (int i=0; i<8; ++i) {
			actionQueue.add(new QueuedAction(playerCards[i][0],
					null, null, false, false, 0.0, false));
			actionQueue.add(new QueuedAction(playerCards[i][1],
					null, null, false, false, 0.0, false));
		}
		// move all cards to deck position
		for (int i=0; i<5; ++i) {
			actionQueue.add(new QueuedAction(centerCards[i],
					deckPosition, null, null, false, 0.0, i==0));
		}
		for (int i=0; i<8; ++i) {
			actionQueue.add(new QueuedAction(playerCards[i][0],
					deckPosition, null, null, false, 0.0, false));
			actionQueue.add(new QueuedAction(playerCards[i][1],
					deckPosition, null, null, false, 0.0, false));
		}
		// set player cards visible (instant)
		for (int i=0; i<8; ++i) {
			actionQueue.add(new QueuedAction(playerCards[i][0],
					null, true, null, true, 0.0, i==0));
			actionQueue.add(new QueuedAction(playerCards[i][1],
					null, true, null, true, 0.0, false));
		}
		// set center cards invisible and into their positions (instant)
		for (int i=0; i<5; ++i) {
			actionQueue.add(new QueuedAction(centerCards[i],
					centerCardPositions[i], false, null, true, 0.0, i==0));
		}
	}
	
	
	public void dealCards(int dealer, double waitTime, String[] playerName) {
		
		
		System.out.println("###GUI ACTION: deal cards");
		
		// add a spacer to let previous actions finish
		actionQueue.add(new QueuedAction(centerCards[0],
				null, null, null, false, waitTime, true));
		
		// set non-existant players' cards invisible (instant)
		for (int i=0; i<8; ++i) {
			if (playerName[i]==null) {
				actionQueue.add(new QueuedAction(playerCards[i][0], 
						null, false, null, true, 0.0, false));
				actionQueue.add(new QueuedAction(playerCards[i][1], 
						null, false, null, true, 0.0, false));
			}
		}
		// deal cards to existing players, set deal order for rendering purposes
		int player = dealer+1;	// start one left of dealer
		for (int i=0; i<8; ++i) {
			player %= 8;
			Card card = playerCards[player][0];
			if (playerName[player]!=null) {
				actionQueue.add(new QueuedAction(card,
						playerCardPositions[player][0], null, null, false, 100.0, i==0));
			}
			playerCardsDealOrder[i] = card;
			player++;
		}
		for (int i=0; i<8; ++i) {
			player %= 8;
			Card card = playerCards[player][1];
			if (playerName[player]!=null) {
				actionQueue.add(new QueuedAction(card,
						playerCardPositions[player][1], null, null, false, 100.0, false));
			}
			playerCardsDealOrder[8+i] = card;
			player++;
		}
	}
	
	
	public void dealFlop(double waitTime) {
		
		System.out.println("###GUI ACTION: reveal flop");
		
		// turn visible
		for(int i=0; i<3; ++i) {
			actionQueue.add(new QueuedAction(centerCards[i],
					null, true, null, false, i==0 ? waitTime : 100.0, i==0));
		}
		// flip
		for(int i=0; i<3; ++i) {
			actionQueue.add(new QueuedAction(centerCards[i],
					null, null, true, false, 100.0, i==0));
		}
	}
	public void dealTurn(double waitTime) {
		
		System.out.println("###GUI ACTION: reveal turn");
		
		// turn visible
		actionQueue.add(new QueuedAction(centerCards[3],
				null, true, null, false, waitTime, true));
		// flip
		actionQueue.add(new QueuedAction(centerCards[3],
				null, null, true, false, 100.0, true));

	}
	public void dealRiver(double waitTime) {
		
		System.out.println("###GUI ACTION: reveal river");
		
		// turn visible
		actionQueue.add(new QueuedAction(centerCards[4],
				null, true, null, false, waitTime, true));
		// flip
		actionQueue.add(new QueuedAction(centerCards[4],
				null, null, true, false, 100.0, true));

	}
	
	public void showMainPlayerCards() {
		
		System.out.println("###GUI ACTION: reveal main player cards");
		
		actionQueue.add(new QueuedAction(playerCards[0][0],
				null, null, true, false, 0.0, true));
		actionQueue.add(new QueuedAction(playerCards[0][1],
				null, null, true, false, 100.0, false));
	}
	
	public void showPlayerCards(int player) {
		
		System.out.println("###GUI ACTION: reveal player "+player+" cards");
		
		// don't wait for prevactions to finish.
		// this method is usually called on multipe players
		actionQueue.add(new QueuedAction(playerCards[player][0],
				null, null, true, false, 0.0, false));
		actionQueue.add(new QueuedAction(playerCards[player][1],
				null, null, true, false, 0.0, false));
	}
	
	public void hidePlayerCards(int player) {
		
		System.out.println("###GUI ACTION: hide player "+player+" cards");
		
		// don't wait for prevactions to finish.
		// this method is usually called on multipe players
		actionQueue.add(new QueuedAction(playerCards[player][0],
				null, null, false, false, 0.0, false));
		actionQueue.add(new QueuedAction(playerCards[player][1],
				null, null, false, false, 0.0, false));
	}
	
	
	public void fold(int player) {
		
		System.out.println("###GUI ACTION: fold player "+player+" cards");
		
		// flip cards face down
		actionQueue.add(new QueuedAction(playerCards[player][0],
				null, null, false, false, 0.0, true));
		actionQueue.add(new QueuedAction(playerCards[player][1],
				null, null, false, false, 100.0, false));
		// set cards invisible
		actionQueue.add(new QueuedAction(playerCards[player][0],
				null, false, null, false, 0.0, true));
		actionQueue.add(new QueuedAction(playerCards[player][1],
				null, false, null, false, 100.0, false));
		
	}
	
	
	public void update(double delta) {
		// count down time for head of action queue, execute action if time expires
		// remove after starting/completing when appropriate
		
		QueuedAction qa = actionQueue.peek();
		double remainingDelta = delta;
		// execute as many actions as possible with this delta
		while (qa != null) {
			remainingDelta = qa.countDown(remainingDelta);
			if (qa.hasExecuted()) {
				actionQueue.remove();
				qa = actionQueue.peek();
			} else {
				break;
			}
		}
		
		/*
		if (qa != null) {
			qa.countDown(delta);
			if (qa.canRemoveFromQueue())
				actionQueue.remove(qa);
		}*/
		// update all cards
		for (int i=0; i<5; ++i) {
			centerCards[i].update(delta);
		}
		for (int i=0; i<8; ++i) {
			playerCards[i][0].update(delta);
			playerCards[i][1].update(delta);
		}
	}
	
	
	public void draw() {
		
		// draw nonmoving cards first
		for (int i=0; i<16; ++i) {
			if (!playerCardsDealOrder[i].isMoving())
				playerCardsDealOrder[i].draw();
		}
		for (int i=15; i>=0; --i) {
			if (playerCardsDealOrder[i].isMoving())
				playerCardsDealOrder[i].draw();
		}
		
		// draw center cards, nonmoving first
		for (int i=4; i>=0; --i) {
			if (!centerCards[i].isMoving())
				centerCards[i].draw();
		}
		for (int i=4; i>=0; --i) {
			if (centerCards[i].isMoving())
				centerCards[i].draw();
		}
	}
}

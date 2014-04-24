package Host.Test;

import Host.GameSystem.Card;
import Host.GameSystem.Rank;

public class TestRank {
	public TestRank() {
	}

	// fullhouse check
	public static void fullhouse_check(Rank calrank) {
		System.out.println("Testing Fullhouse");

		Card[] temp = new Card[7];
		for (int i = 1; i < 14; i++) {
			for (int j = 1; j < 14; j++) {
				if (j != i) {
					temp[0] = new Card(1, i);
					temp[1] = new Card(2, i);
					temp[2] = new Card(3, i);
					temp[3] = new Card(4, j);
					temp[4] = new Card(1, j);
					temp[5] = new Card(1, 0);
					temp[6] = new Card(1, 0);

					calrank.findBestHand(temp);
				}
			}
		}

		// calrank.findWinner(flop, hand);

	}

	public static void fourcard_check(Rank calrank) {
		System.out.println("Testing FourCard");
		Card[] temp = new Card[7];
		for (int i = 1; i < 14; i++) {
			for (int j = 1; j < 14; j++) {
				if (j != i) {
					temp[0] = new Card(1, i);
					temp[1] = new Card(2, i);
					temp[2] = new Card(3, i);
					temp[3] = new Card(4, i);
					temp[4] = new Card(1, j);
					temp[5] = new Card(1, 0);
					temp[6] = new Card(1, 0);
					calrank.findBestHand(temp);
				}
			}
		}

		// calrank.findWinner(flop, hand);
	}

	// straighflush check
	public static void straightflush_check(Rank calrank) {
		System.out.println("Testing StraightFlush");
		Card[] temp = new Card[7];
		for (int kind = 1; kind < 5; kind++) {
			for (int i = 1; i < 10; i++) {
				temp[0] = new Card(kind, i + 4);
				temp[1] = new Card(kind, i + 3);
				temp[2] = new Card(kind, i + 2);
				temp[3] = new Card(kind, i + 1);
				temp[4] = new Card(0, 1);
				temp[5] = new Card(kind, i);
				temp[6] = new Card(0, 1);
				calrank.findBestHand(temp);
			}
		}

		// calrank.findWinner(flop, hand);
	}

	// flush check
	public static void flush_check(Rank calrank) {
		System.out.println("Testing Flush");
		Card[] temp = new Card[7];
		for (int kind = 1; kind < 5; kind++) {
			for (int i = 2; i < 10; i++) {
				for (int j = i + 5; j < 15; j++) {
					temp[0] = new Card(kind, i);
					temp[1] = new Card(kind, i + 1);
					temp[2] = new Card(kind, i + 2);
					temp[3] = new Card(kind, i + 3);
					temp[4] = new Card(kind, j);
					temp[5] = new Card(2, 0);
					temp[6] = new Card(3, 0);
					calrank.findBestHand(temp);
				}
			}
		}

	}

	// straight check
	public static void straight_check(Rank calrank) {
		System.out.println("Testing Straight");
		Card[] temp = new Card[7];
		for (int i = 1; i < 10; i++) {
			temp[0] = new Card(1, i + 4);
			temp[1] = new Card(3, i + 3);
			temp[2] = new Card(1, i + 2);
			temp[3] = new Card(3, i + 1);
			temp[4] = new Card(0, 1);
			temp[5] = new Card(1, i);
			temp[6] = new Card(0, 1);
			calrank.findBestHand(temp);
		}

		// calrank.findWinner(flop, hand);
	}

	// three of kinds
	public static void threeofkind_check(Rank calrank) {
		System.out.println("Testing Three of Kinds");
		Card[] temp = new Card[7];
		for (int i = 1; i < 14; i++) {
			for (int j = 1; j < 13; j++) {
				if (j != i) { // There are some case where it is four card.
								// Usually the pair will be always x,x,x,21,20
								// (temporary)
					temp[0] = new Card(1, i);
					temp[1] = new Card(2, i);
					temp[2] = new Card(3, i);
					temp[3] = new Card(4, j);
					temp[4] = new Card(1, j + 1);
					temp[5] = new Card(1, 14);
					temp[6] = new Card(1, 13);
					calrank.findBestHand(temp);
				}
			}
		}

		// calrank.findWinner(flop, hand);
	}

	// two pairs
	public static void twopair_check(Rank calrank) {
		System.out.println("Testing Two Pair");
		Card[] temp = new Card[7];
		for (int i = 1; i < 14; i++) {
			for (int j = 1; j < 14; j++) {
				for (int k = 1; k < 14; k++) {
					if (j != i && k != i && k != j) {
						temp[0] = new Card(1, i);
						temp[1] = new Card(2, i);
						temp[2] = new Card(3, k);
						temp[3] = new Card(4, j);
						temp[4] = new Card(1, j);
						temp[5] = new Card(1, 0);
						temp[6] = new Card(1, 0);
						calrank.findBestHand(temp);
					}
				}
			}
		}

		// calrank.findWinner(flop, hand);
	}

	/**
	 * There are some exception cases. The testing will start 3 3 13 12 11 3 3
	 * 12 11 10 3 3 11 10 9 . . . . . . 13 13 5 4 3 It is starting with 3
	 * because there are temporary values 0 and 2 that exist for preventing null
	 * pointer error Therefore there is other ranks happening.
	 */
	public static void onepair_check(Rank calrank) {
		System.out.println("Testing OnePair");

		Card[] temp = new Card[7];
		for (int i = 3; i < 14; i++) {
			for (int j = 14; j > 2; j--) {
				if (j != i && (j - 1) != i && (j - 2) != i && (j - 1) != 2
						&& (j - 2) != 2) {
					temp[0] = new Card(1, i);
					temp[1] = new Card(2, i);
					temp[2] = new Card(3, j);
					temp[3] = new Card(4, j - 1);
					temp[4] = new Card(1, j - 2);
					temp[5] = new Card(2, 0);
					temp[6] = new Card(3, 2);
					calrank.findBestHand(temp);
				}
			}
		}

		// calrank.findWinner(flop, hand);
	}

	/**
	 * There are some exception cases. The testing will start 
	 * 4 5 6 7 9 [i i+1 i+2 i+3 i+5] 
	 * 4 5 6 7 10 
	 * 4 5 6 7 11 
	 *   .    . 
	 *   .    . 
	 *   .    . 
	 * 9 10 11 12 14 
	 * It is starting with 4 because there are temporary values 0 and 2 that exist for
	 * preventing null pointer error. 3 makes straight. 1 will be converted to 14
	 * 
	 * 1 3 5 7 9 
	 * 3 5 7 9 11 
	 * 4 6 8 10 12 
	 * 6 8 10 12 14
	 */

	public static void highcard_check(Rank calrank) {
		System.out.println("Testing HighCard");
		Card[] temp = new Card[7];
		for (int i = 4; i < 10; i++) {
			for (int j = i + 5; j < 15; j++) {
				temp[0] = new Card(1, i);
				temp[1] = new Card(2, i + 1);
				temp[2] = new Card(3, i + 2);
				temp[3] = new Card(4, i + 3);
				temp[4] = new Card(1, j);
				temp[5] = new Card(2, 0);
				temp[6] = new Card(3, 2);
				calrank.findBestHand(temp);
			}
		}
		
		//i i+1 i+3 i+4 i+5
		//System.out.println("i i+1 i+3 i+4 i+5");
		for(int k=1;k<3;k++){
			if(k==2){
				//System.out.println("i i+2 i+3 i+4 i+5");
			}
			for (int i = 3; i < 10; i++) {
				if (i != 2) {
					temp[0] = new Card(1, i);
					temp[1] = new Card(2, i + k);
					temp[2] = new Card(3, i + k+2);
					temp[3] = new Card(4, i + k+3);
					temp[4] = new Card(1, i + k+4);
					temp[5] = new Card(2, 0);
					temp[6] = new Card(3, 2);
					calrank.findBestHand(temp);
				}
			}
		}
		//i i+2 i+3 i+4 i+5
		//System.out.println("i i+2 i+3 i+4 i+5");
		for (int i = 3; i < 10; i++) {
			if (i != 2) {
				temp[0] = new Card(1, i);
				temp[1] = new Card(2, i + 2);
				temp[2] = new Card(3, i + 3);
				temp[3] = new Card(4, i + 4);
				temp[4] = new Card(1, i + 5);
				temp[5] = new Card(2, 0);
				temp[6] = new Card(3, 2);
				calrank.findBestHand(temp);
			}
		}
		
		for(int k=3;k<9;k++){
			//System.out.println("i i+"+k+" i+"+(k+1)+" i+"+(k+2)+" i+"+(k+3));
			for (int i = 1; i < 12-k; i++) {
				if (i != 2) {
					temp[0] = new Card(1, i);
					temp[1] = new Card(2, i + k);
					temp[2] = new Card(3, i + k+1);
					temp[3] = new Card(4, i + k+2);
					temp[4] = new Card(1, i + k+3);
					temp[5] = new Card(2, 0);
					temp[6] = new Card(3, 2);
					calrank.findBestHand(temp);
				}
			}
		}
		
		
		
		//i i+2 i+4 i+6 i+8
		//System.out.println("i i+2 i+4 i+6 i+8");
		for (int i = 1; i < 7; i++) {
			if (i != 2) {
				temp[0] = new Card(1, i);
				temp[1] = new Card(2, i + 2);
				temp[2] = new Card(3, i + 4);
				temp[3] = new Card(4, i + 6);
				temp[4] = new Card(1, i + 8);
				temp[5] = new Card(2, 0);
				temp[6] = new Card(3, 2);
				calrank.findBestHand(temp);
			}
		}

	}

	public static void main(String[] args) {
		Rank obj1 = new Rank();
		System.out.println("ROYAL_STRAIGHT_FLUSH(9) STRAIGHT_FLUSH(8) FOURCARD(7) FULLHOUSE(6) FLUSH(5) STRAIGHT(4) THREEPAIR(3) TWOPAIR(2) ONEPAIR(1) NOPAIR(0)");
		straightflush_check(obj1);
		System.out.println("=============================================[next rank]====================================================================");
		fourcard_check(obj1);
		System.out.println("=============================================[next rank]====================================================================");
		fullhouse_check(obj1);
		System.out.println("=============================================[next rank]====================================================================");
		straight_check(obj1);
		System.out.println("=============================================[next rank]====================================================================");
		flush_check(obj1);
		System.out.println("=============================================[next rank]====================================================================");
		threeofkind_check(obj1);
		System.out.println("=============================================[next rank]====================================================================");
		twopair_check(obj1);
		System.out.println("=============================================[next rank]====================================================================");
		onepair_check(obj1);
		System.out.println("=============================================[next rank]====================================================================");
		highcard_check(obj1);
		System.out.println("=============================================[   end   ]====================================================================");

	}
}

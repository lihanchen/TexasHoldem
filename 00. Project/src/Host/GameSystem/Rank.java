package Host.GameSystem;

import java.util.*;

//For debugging Goto Deck.java
public class Rank {

	private static int final_rank;
	private static boolean Ace;
	private static boolean Ace_Pair;

	public static final int ROYAL_STRAIGHT_FLUSH = 9;
	public static final int STRAIGHT_FLUSH = 8;
	public static final int FOURCARD = 7;
	public static final int FULLHOUSE = 6;
	public static final int FLUSH = 5;
	public static final int STRAIGHT = 4;
	public static final int THREEPAIR = 3;
	public static final int TWOPAIR = 2;
	public static final int ONEPAIR = 1;
	public static final int NOPAIR = 0;

		//initialize variable
	public Rank() {
		final_rank=0;
		Ace=false;
	}
		
		/**Merge player card and flop card. Card[]flop represent flops and Card[][]hand represent each player's hand.
		 * After merge [0-1] will be player's hand index and [2-6] will be flop index.
		 * **/
	
	public Card[][] merge(Card[] flop, Card[][] hand) { 
		Card[][] merge_arr = new Card[GameSystem.MAXPLAYER][7];
		for (int i = 0; i < GameSystem.MAXPLAYER; i++) {
			if (hand[i] != null) {
				for (int j = 0; j < 2; j++) {
					merge_arr[i][j] = hand[i][j];
				}
				for (int j = 2, m = 0; j < 7; j++, m++) {
					merge_arr[i][j] = flop[m];
				}
			} else {
				merge_arr[i] = null;
			}
		}
		return merge_arr;
	}
	// exist for debugging
	private void printMerge(Card[][] merge_arr) { 

		for (int i = 0; i < GameSystem.MAXPLAYER; i++) {
			if (merge_arr[i] != null) {
				for (int j = 0; j < 7; j++) {
					System.out.println("merge_arr[" + i + "][" + j + "]: "
							+ merge_arr[i][j].toString());
				}
			}

		}
	}

	// split pot winner not handled yet.
	public boolean[] findWinner(Pot pot, Card[] flop, Card[][] hand) {

		int rank[] = new int[8];
		int max_rank = 0;
		int max_rank_count = 0; // number of players who has max_rank card
		Card[][] merge_arr = merge(flop, hand);
		System.out.println("ROYAL_STRAIGHT_FLUSH(9) STRAIGHT_FLUSH(8) FOURCARD(7) FULLHOUSE(6) FLUSH(5) STRAIGHT(4) THREEPAIR(3) TWOPAIR(2) ONEPAIR(1) NOPAIR(0)");
		for (int i = 0; i < GameSystem.MAXPLAYER; i++) { // find players rank and store their rank to the rank array
			if (merge_arr[i] != null) {
				findBestHand(merge_arr[i]);
				rank[i] = final_rank;
				System.out.println("player " + i + " rank is " + final_rank);
			}
		}
		
		
		for (int i = 0; i < 8; i++) { // gets max_rank
			if (rank[i] > max_rank) {
				max_rank = rank[i];
			}
		}

		
		boolean winner[] = new boolean[8];
		for (int i = 0; i < GameSystem.MAXPLAYER; i++) {
			if (rank[i] == max_rank) {
				max_rank_count++;
				winner[i] = true;
			} else {
				merge_arr[i] = null;
				winner[i] = false;
			}
		}
		// temporary exist for debug (
		for (int i = 0; i < GameSystem.MAXPLAYER; i++) {
			if (merge_arr[i] != null) {
				int[] rank1 = findBestHand(merge_arr[i]);
				System.out.println("Player " + i + " best set is "
						+ Arrays.toString(rank1));
			}
		}
		// )
		
		if (max_rank_count > 1) {
			winner = compareHands(merge_arr, max_rank);
		}
		// debugging purpose

		// System.out.println(Arrays.toString(winner));
		System.out.print("From player[0-7], The WINNER IS PLAYER ");
		for (int i = 0; i < 8; i++) {
			if (winner[i] == true) {
				System.out.print((i) + " ");
			}
		}
		System.out.println("!");
		
		pot.winnerRank = max_rank;
		return winner;
	}

	// compare hands between two players
	private boolean[] compareHands(Card[][] merge_arr, int max_rank) {
		boolean tie[] = new boolean[8];
		int highest_index = 0;
		if (max_rank >= 0) {
			int highest = -1;
			for (int i = 0; i < 8; i++) {
				if (merge_arr[i] != null) {
					if (highest == -1) {
						highest = i;
					} else {
						int[] rank1 = findBestHand(merge_arr[highest]);
						int[] rank2 = findBestHand(merge_arr[i]);
						for (int j = 0; j < 5; j++) {
							if (rank1[j] > rank2[j])
								break;
							else if (rank1[j] < rank2[j]) {
								highest = i;
								break;
							}
						}
					}
				}
			}

			int[] rank1 = findBestHand(merge_arr[highest]);
			for (int t = 0; t < 8; t++) {
				int equal_count = 0;
				if (merge_arr[t] != null) {
					int[] rank2 = findBestHand(merge_arr[t]);
					for (int i = 0; i < 5; i++) {
						if (rank1[i] == rank2[i]) {
							equal_count++;
						}
					}
				}
				if (equal_count == 5) {
					tie[t] = true;
				}
			}
			return tie;
		}
		return null;
	}

	// finds the best hands of the player
	public int[] findBestHand(Card[] cards) {

		// combine cards

		// find the best hand
		int highHand[] = null;
		if (isRoyalStraightFlush(cards) != null) {
			highHand = isRoyalStraightFlush(cards);
			final_rank = 9;
		} else if (isStraightFlush(cards) != null) {
			highHand = isStraightFlush(cards);
			final_rank = 8;
		} else if (isFourCard(cards) != null) {
			highHand = isFourCard(cards);
			final_rank = 7;
		} else if (isFullHouse(cards) != null) {
			highHand = isFullHouse(cards);
			final_rank = 6;
		} else if (isFlush(cards) != null) {
			highHand = isFlush(cards);
			final_rank = 5;
		} else if (isStraight(cards) != null) {
			highHand = isStraight(cards);
			final_rank = 4;
		} else if (isThreeOfKind(cards) != null) {
			highHand = isThreeOfKind(cards);
			final_rank = 3;
		} else if (isTwoPair(cards) != null) {
			highHand = isTwoPair(cards);
			final_rank = 2;
		} else if (isOnePair(cards) != null) {
			highHand = isOnePair(cards);
			final_rank = 1;
		} else { 
			highHand = noPair(cards);
			final_rank = 0;
		}
		System.out.print("rank:"+final_rank+" | Five Cards: ");
		printCard(highHand);
		
		return highHand;
	}
	
	private void printCard(int[]highHand){
			System.out.print("[ ");
		for(int i=0;i<5;i++){
			if(highHand[i]==11){
				System.out.print("J");
			}else if(highHand[i]==12){
				System.out.print("Q");
			}else if(highHand[i]==13){
				System.out.print("K");
			}else if(highHand[i]==14){
				System.out.print("A");
			}else if(highHand[i]==1){
				System.out.print("A");
			}else{
				System.out.print(highHand[i]);
			}
			if(i!=4){
				System.out.print(", ");
			}
		}
			System.out.println(" ]");
	}

	private int[] isRoyalStraightFlush(Card cards[]) {
		int royal_flush_helper = 0;
		int[] best_set = new int[5];
		int kind = isSameKind(cards);
		if (kind != 0) {
			for (int i = 0; i < 7; i++) { // checks for royal_flush
				if (cards[i].getKind() == kind
						&& (cards[i].getNumber() == 1
								|| cards[i].getNumber() == 10
								|| cards[i].getNumber() == 11
								|| cards[i].getNumber() == 12 || cards[i]
								.getNumber() == 13)) {

					best_set[royal_flush_helper++] = cards[i].getNumber();

				}
			}
			if (royal_flush_helper == 5) {
				return best_set;
			} else {
				return null;
			}
		}
		return null;
	}

	private int[] isStraightFlush(Card cards[]) {
		int temp[] = new int[7];
		int temp_1[] = new int[7];
		int count;
		int best_set[] = new int[5];
		int kind = isSameKind(cards);
		if (kind != 0) {
			for (int i = 0; i < 7; i++) {
				if (cards[i].getKind() == kind) {
					temp[i] = cards[i].getNumber();
				}
			}
			Arrays.sort(temp);
			for (int j = 0, k = 6; j < 7; j++, k--) {
				temp_1[k] = temp[j];
			}

			for (int i = 2; i >= 0; i--) {
				count = 0;
				int k = i;
				best_set[count++] = temp[i + 4];
				for (int j = i + 4; j > k; j--) {
					if (temp[j - 1] + 1 == temp[j]) {
						best_set[count++] = temp[j - 1];
					} else if (temp[j - 1] == temp[j]) {
						k--;
					}
					if (k < 0)
						break;
				}
				if (count == 5) {
					return best_set;
				}
			}
		}

		return null;
	}

	private int[] isFourCard(Card cards[]) {
		int temp[];
		int best_set[] = new int[5];
		int high_num;
		int pop_num;
		temp = sort_toIntArrayAce(cards);
		if (findPair(cards) >= 4) {
			pop_num = getMostPopularElement(temp);
			if (pop_num == 14) {
				Ace_Pair = true;
			} else {
				Ace_Pair = false;
			}
			for (int i = 6; i >= 0; i--) {
				if (temp[i] == 1 && Ace_Pair != true) {
					Ace = true;
				}
			}
			int i;
			for (i = 6; temp[i] == pop_num; i--) {
			}

			high_num = temp[i];

			if (high_num > pop_num && pop_num != 14) {
				for (int j = 0; j < 4; j++) {
					best_set[j] = pop_num;
				}
				if (Ace == false) {
					best_set[4] = high_num;
				} else if (Ace == true) {
					best_set[4] = 1;
				}
			} else if (pop_num == 14) {
				for (int j = 0; j < 4; j++) {
					best_set[j] = pop_num;
				}
				best_set[4] = high_num;
			} else {
				best_set[4] = high_num;
				for (int j = 0; j < 4; j++) {
					best_set[j] = pop_num;
				}
			}
			return best_set;
		}
		return null;
	}

	private int[] isFullHouse(Card cards[]) {
		int temp[];
		int best_set[] = new int[5];
		int pop_num1;
		int pop_num2 = 0, pop_num3 = 0, pop_num4 = 0;
		temp = sort_toIntArrayAce(cards);
		if (findPair(cards) == 3) {
			pop_num1 = getMostPopularElement(temp);
			for (int i = 6; i > 0; i--) {
				if ((temp[i] == temp[i - 1]) && (temp[i] != pop_num1)) {
					pop_num2 = temp[i];
					break;
				}
			}
			for (int i = 0; i < 7; i++) {
				if (temp[i] == 1) {
					pop_num3++;
				}
			}
			if (pop_num3 == 3 && pop_num2 != 1) {
				pop_num1 = 1;
			}
			if (pop_num2 == 1) {
				Ace_Pair = true;
			}
			if (pop_num2 != 0) {
				if (pop_num1 > pop_num2 && Ace_Pair == false) {
					for (int j = 0; j < 3; j++) {
						best_set[j] = pop_num1;
					}
					for (int j = 3; j < 5; j++) {
						best_set[j] = pop_num2;
					}
				} else if (pop_num1 < pop_num2 && Ace_Pair == false) {
					for (int j = 0; j < 3; j++) {
						best_set[j] = pop_num1;
					}
					for (int j = 3; j < 5; j++) {
						best_set[j] = pop_num2;
					}
				} else if (Ace_Pair == true) {
					for (int j = 0; j < 3; j++) {
						best_set[j] = 1;
					}
					for (int j = 3; j < 5; j++) {
						best_set[j] = pop_num2;
					}
				}
				return best_set;
			}
		}
		return null;
	}

	private int[] isFlush(Card[] card) {

		int kind = isSameKind(card);
		int valid;
		int best_set[] = new int[7];
		int temp[] = new int[5];
		if (kind > 0 && kind < 5) {
			for (int i = 0; i < 7; i++) {
				if (card[i].getKind() == kind) {
					valid = card[i].getNumber();
					best_set[i] = valid;
				}
			}
			for (int i = 0; i < 5; i++) { // returns 14 to 1.
				if (best_set[i] == 1) {
					best_set[i] = 14;
				}
			}
			Arrays.sort(best_set);
			for (int j = 2, k = 4; j < 7; j++, k--) {
				temp[k] = best_set[j];
			}

			return temp;
		} else {
			return null;
		}
	}

	private int isSameKind(Card[] card) {

		int isSameKind = 0;
		int clover_num = 0; // CLOVER = 1;
		int heart_num = 0; // HEART = 2;
		int diamond_num = 0; // DIAMOND = 3;
		int spade_num = 0; // SPADE = 4;

		for (int i = 0; i < 7; i++) {
			switch (card[i].getKind()) {
			case 1:
				clover_num++;
				break;
			case 2:
				heart_num++;
				break;
			case 3:
				diamond_num++;
				break;
			case 4:
				spade_num++;
				break;
			}
		}
		// for debugging goto Deck.java
		if (clover_num >= 5) {
			isSameKind = 1;
		} else if (heart_num >= 5) {
			isSameKind = 2;
		} else if (diamond_num >= 5) {
			isSameKind = 3;
		} else if (spade_num >= 5) {
			isSameKind = 4;
		}
		return isSameKind;
	}

	private int[] isStraight(Card[] cards) {
		int temp[]; // temporary array that will hold 1 set of 7 cards
		int count = 0;
		int best_set[] = new int[5];
		temp = sort_toIntArray(cards);

		int helper = 0;
		for (int i = 0; i < 7; i++) {
			if (temp[i] == 1) {
				helper = 1;
			}
			if (temp[i] == 10 && helper == 1) {
				helper = 2;
			}
			if (temp[i] == 11 && helper == 2) {
				helper = 3;
			}
			if (temp[i] == 12 && helper == 3) {
				helper = 4;
			}
			if (temp[i] == 13 && helper == 4) {
				helper = 5;
			}
		}
		if (helper == 5) {
			best_set[0] = 14;
			best_set[1] = 13;
			best_set[2] = 12;
			best_set[3] = 11;
			best_set[4] = 10;
			return best_set;
		}

		for (int i = 2; i >= 0; i--) {
			count = 0;
			int k = i;
			int straight[] = new int[5];
			straight[count++] = temp[i + 4];
			for (int j = i + 4; j > k; j--) {
				if (temp[j - 1] + 1 == temp[j]) {
					straight[count++] = temp[j - 1];
				} else if (temp[j - 1] == temp[j]) {
					k--;
				}

				if (k < 0)
					break;
			}
			if (count == 5) {
				return straight;
			}
		}

		return null;
	}

	private int[] isThreeOfKind(Card cards[]) {
		int temp[];
		int pop_num;
		int cardcount = 0;
		int best_set[] = new int[5];
		temp = sort_toIntArrayAce(cards);

		Arrays.sort(temp);
		pop_num = getMostPopularElement(temp);

		if (findPair(cards) == 3 && isFullHouse(cards) == null) {
			for (int j = 0; j < 3; j++) {
				best_set[j] = pop_num;
			}
			for (int i = 6; i >= 0; i--) { //
				if (temp[i] != pop_num) {
					best_set[3] = temp[i];
					break;
				}
			}
			for (int i = 6; i >= 0; i--) {
				if (temp[i] != pop_num && temp[i] != best_set[3]) {
					best_set[4] = temp[i];
					break;
				}
			}

			return best_set;
		}
		return null;
	}

	// debugged and fixed errors
	private int[] isTwoPair(Card cards[]) {

		int temp[];
		int pop_num1;
		int pop_num2 = 0;
		int high_num = 0;
		int best_set1[] = new int[5];

		temp = sort_toIntArrayAce(cards);

		Arrays.sort(temp);
		pop_num1 = getMostPopularElement(temp);

		for (int i = 5; i >= 0; i--) {
			if ((temp[i] == temp[i + 1]) && temp[i] != pop_num1) {
				pop_num2 = temp[i];
				break;
			}
		}

		if (findPair(cards) == 2 && pop_num2 != 0) {
			// setting higher pair out of two pairs
			// higher pair will be store as first two; then lower pair gets
			// stored
			if (pop_num1 > pop_num2) {
				for (int j = 0; j < 2; j++) {
					best_set1[j] = pop_num1;
				}
				for (int j = 2; j < 4; j++) {
					best_set1[j] = pop_num2;
				}
			} else {
				for (int j = 0; j < 2; j++) {
					best_set1[j] = pop_num2;
				}
				for (int j = 2; j < 4; j++) {
					best_set1[j] = pop_num1;
				}
			}
			for (int i = 6; i >= 0; i--) {
				if (temp[i] != pop_num1 && temp[i] != pop_num2) {
					best_set1[4] = temp[i];
					break;
				}
			}
			return best_set1;
		} else {
			return null;
		}
	}

	private int[] isOnePair(Card cards[]) {

		int best_set[] = new int[5];
		int card[] = sort_toIntArrayAce(cards);

		boolean ch = false;
		for (int i = 0; i < 6; i++) {
			for (int j = i + 1; j < 7; j++) {
				if (card[i] == card[j]) {
					best_set[0] = card[i];
					best_set[1] = best_set[0];
					card[i] = 0;
					card[j] = 0;
					ch = true;
					break;
				}
			}
			if (ch)
				break;
		}
		if (!ch)
			return null;

		int index = 2;
		for (int i = 6; i >= 0; i--) {
			int num = card[i];
			if (num != 0)
				best_set[index++] = num;

			if (index == 5)
				break;
		}
		return best_set;
	}

	private int[] noPair(Card cards[]) {
		if (findPair(cards) == 0) {
			int temp[];
			boolean test = false;
			int best_set[] = new int[5];
			temp = sort_toIntArrayAce(cards);
			for (int i = 6; i >= 0; i--) {
				if (temp[i] == 1) {
					test = true;
					best_set[0] = temp[i];
					break;
				}
			}
			if (test == false) {
				for (int i = 6, j = 0; j < 5; i--, j++) {
					best_set[j] = temp[i];
				}
			} else {
				for (int i = 6, j = 1; j < 5; i--, j++) {
					best_set[j] = temp[i];
				}
			}

			return best_set;
		} else {
			return null;
		}
	}

	private static int findPair(Card[] cards) {
		int temp[];
		int max_pair = 0;
		int pair_helper = 0;

		temp = sort_toIntArray(cards);

		for (int i = 0; i < 6; i++) {
			if (temp[i] == temp[i + 1]) {
				int j = i;
				pair_helper = 1;
				while (temp[j] == temp[j + 1]) {
					pair_helper++;
					j++;
					if (j == 6) {
						break;
					}
				}
				i = j;
				if (max_pair < pair_helper) {
					max_pair = pair_helper;
				}
			}
		}
		return max_pair;
	}

	private int getMostPopularElement(int[] a) {

		int maxElementIndex = getArrayMaximumElementIndex(a);
		int[] b = new int[a[maxElementIndex] + 1];
		for (int i = 0; i < a.length; i++) {
			++b[a[i]];
		}
		return getArrayMaximumElementIndex(b);
	}

	private int getArrayMaximumElementIndex(int[] a) {
		int maxElementIndex = 0;
		for (int i = 1; i < a.length; i++) {
			if (a[i] >= a[maxElementIndex]) {
				maxElementIndex = i;
			}
		}
		return maxElementIndex;
	}

	private static int[] sort_toIntArray(Card[] cards) {
		int temp[] = new int[7];
		for (int i = 0; i < 7; i++) {
			temp[i] = cards[i].getNumber();
		}
		Arrays.sort(temp);
		return temp;
	}

	private int[] sort_toIntArrayAce(Card[] cards) {
		int temp[] = new int[7];
		for (int i = 0; i < 7; i++) {
			temp[i] = cards[i].getNumber();
			if (temp[i] == 1)
				temp[i] = 14;
		}
		Arrays.sort(temp);
		return temp;
	}

}

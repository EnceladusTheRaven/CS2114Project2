package poker;

public class Hand
{

    // ~ Fields ................................................................

    public static final int ACE_HIGH = 14;

    private Card[] cards;
    private HandRank rank;
    private int[] tiebreakers;

    // ~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a hand from the hole cards and community cards and evaluates it.
     *
     * @param hole
     *            the player's private cards
     * @param community
     *            the shared cards dealt so far
     * @throws IllegalArgumentException
     *             if either chain is null, or if there are fewer than 5 cards
     *             in total
     */
    public Hand(LinkedChain<Card> hole, LinkedChain<Card> community)
    {
        validate(hole, community);
        cards = combine(hole, community);
        findBestHand();
    }


    /**
     * Gets the category of this hand's best 5 cards.
     *
     * @return the hand category, such as {@code HandRank.FLUSH}
     */
    public HandRank getRank()
    {
        return rank;
    }


    /**
     * Compares this hand to another to decide which one wins. Categories are
     * compared first; if they match, the tiebreaker values are compared in
     * order.
     *
     * @param other
     *            the hand to compare against
     * @return a positive number if this hand wins, a negative number if it
     *             loses, or 0 if the hands tie and the pot is split
     * @throws NullPointerException
     *             if other is null
     */
    public int compareTo(Hand other)
    {
        return compareScores(
            this.rank,
            this.tiebreakers,
            other.rank,
            other.tiebreakers);
    }


    /**
     * Returns the name of this hand's category.
     *
     * @return the category name, such as "FULL_HOUSE"
     */
    @Override
    public String toString()
    {
        return rank.toString();
    }


    /**
     * Checks that both chains exist and hold at least 5 cards between them.
     *
     * @param hole
     *            the player's private cards
     * @param community
     *            the shared cards dealt so far
     * @throws IllegalArgumentException
     *             if either chain is null, or if there are fewer than 5 cards
     *             in total
     */
    private void validate(LinkedChain<Card> hole, LinkedChain<Card> community)
    {
        if (hole == null || community == null)
        {
            throw new IllegalArgumentException("Card chains cannot be null");
        }
        int total = hole.size() + community.size();
        if (total < 5)
        {
            throw new IllegalArgumentException(
                "Need at least 5 cards, you have " + total);
        }
    }


    /**
     * Copies the hole cards and community cards into a single array so that
     * combinations can be picked by index.
     *
     * @param hole
     *            the player's private cards
     * @param community
     *            the shared cards dealt so far
     * @return an array holding every card from both chains
     */
    private Card[] combine(LinkedChain<Card> hole, LinkedChain<Card> community)
    {
        Card[] all = new Card[hole.size() + community.size()];
        for (int i = 0; i < hole.size(); i++)
        {
            all[i] = hole.get(i);
        }
        for (int i = 0; i < community.size(); i++)
        {
            all[hole.size() + i] = community.get(i);
        }
        return all;
    }


    /**
     * Tries every way to choose 5 of the cards and keeps the best combination.
     * Each loop starts after the previous one so no card or combination is
     * picked twice.
     */
    private void findBestHand()
    {
        int n = cards.length;
        for (int i = 0; i < n; i++)
        {
            for (int j = i + 1; j < n; j++)
            {
                for (int k = j + 1; k < n; k++)
                {
                    for (int l = k + 1; l < n; l++)
                    {
                        for (int m = l + 1; m < n; m++)
                        {
                            Card[] five = { cards[i], cards[j], cards[k],
                                cards[l], cards[m] };
                            scoreAndKeepIfBetter(five);
                        }
                    }
                }
            }
        }
    }


    /**
     * Scores one 5-card combination and keeps it if it beats the best found so
     * far.
     *
     * @param five
     *            exactly 5 cards to score
     */
    private void scoreAndKeepIfBetter(Card[] five)
    {
        // Card values, with an Ace as 14, sorted high to low.
        int[] values = new int[5];
        for (int i = 0; i < 5; i++)
        {
            int r = five[i].getValue();
            if (r == 1) {
                values[i] = ACE_HIGH;
            } else {
                values[i] = r;
            }
        }
        sortDescending(values);

        // Flush: all five cards share the first card's suit.
        boolean isFlush = true;
        for (int i = 1; i < 5; i++)
        {
            if (!five[i].getSuit().equals(five[0].getSuit()))
            {
                isFlush = false;
            }
        }

        int straightHigh = straightHighCard(values);

        // Group equal values: most copies first, then higher value first.
        // For example, K K 7 7 2 gives groups [K, 7, 2] with counts [2, 2, 1].
        int[] groupRanks = new int[5];
        int[] groupCounts = new int[5];
        int groups = groupByCount(values, groupRanks, groupCounts);

        // Decide the category, checking the strongest first.
        HandRank newRank;
        int[] newTiebreakers;

        if (isFlush && straightHigh == ACE_HIGH)
        {
            newRank = HandRank.ROYAL_FLUSH;
            newTiebreakers = new int[] { straightHigh };
        }
        else if (isFlush && straightHigh > 0)
        {
            newRank = HandRank.STRAIGHT_FLUSH;
            newTiebreakers = new int[] { straightHigh };
        }
        else if (groupCounts[0] == 4)
        {
            newRank = HandRank.FOUR_OF_A_KIND;
            newTiebreakers = copy(groupRanks, groups);
        }
        else if (groupCounts[0] == 3 && groupCounts[1] == 2)
        {
            newRank = HandRank.FULL_HOUSE;
            newTiebreakers = copy(groupRanks, groups);
        }
        else if (isFlush)
        {
            newRank = HandRank.FLUSH;
            newTiebreakers = values;
        }
        else if (straightHigh > 0)
        {
            newRank = HandRank.STRAIGHT;
            newTiebreakers = new int[] { straightHigh };
        }
        else if (groupCounts[0] == 3)
        {
            newRank = HandRank.THREE_OF_A_KIND;
            newTiebreakers = copy(groupRanks, groups);
        }
        else if (groupCounts[0] == 2 && groupCounts[1] == 2)
        {
            newRank = HandRank.TWO_PAIR;
            newTiebreakers = copy(groupRanks, groups);
        }
        else if (groupCounts[0] == 2)
        {
            newRank = HandRank.ONE_PAIR;
            newTiebreakers = copy(groupRanks, groups);
        }
        else
        {
            newRank = HandRank.HIGH_CARD;
            newTiebreakers = values;
        }

        if (rank == null
            || compareScores(newRank, newTiebreakers, rank, tiebreakers) > 0)
        {
            rank = newRank;
            tiebreakers = newTiebreakers;
        }
    }


    /**
     * Finds the top card of a straight. A-2-3-4-5 counts as a straight with 5
     * as its top card.
     *
     * @param values
     *            5 card values sorted from high to low
     * @return the value of the straight's top card, or 0 if the values are not
     *             a straight
     */
    private static int straightHighCard(int[] values)
    {
        boolean inARow = true;
        for (int i = 1; i < 5; i++)
        {
            if (values[i] != values[i - 1] - 1)
            {
                inARow = false;
            }
        }
        if (inARow)
        {
            return values[0];
        }
        if (values[0] == ACE_HIGH && values[1] == 5 && values[2] == 4
            && values[3] == 3 && values[4] == 2)
        {
            return 5;
        }
        return 0;
    }


    /**
     * Groups equal card values and orders the groups by how many times each
     * value appears (most first), then by value (highest first).
     * 
     * @param values
     *            5 card values sorted from high to low
     * @param groupRanks
     *            filled with each distinct value, in group order
     * @param groupCounts
     *            filled with how many times each value appears
     * @return the number of distinct values
     */
    private static
        int
        groupByCount(int[] values, int[] groupRanks, int[] groupCounts)
    {
        int groups = 0;
        for (int i = 0; i < 5; i++)
        {
            if (groups > 0 && groupRanks[groups - 1] == values[i])
            {
                groupCounts[groups - 1]++;
            }
            else
            {
                groupRanks[groups] = values[i];
                groupCounts[groups] = 1;
                groups++;
            }
        }
        // Sort by count, highest first. Equal counts keep their high-to-low
        // value order because values was already sorted.
        for (int i = 1; i < groups; i++)
        {
            int r = groupRanks[i];
            int c = groupCounts[i];
            int j = i - 1;
            while (j >= 0 && groupCounts[j] < c)
            {
                groupRanks[j + 1] = groupRanks[j];
                groupCounts[j + 1] = groupCounts[j];
                j--;
            }
            groupRanks[j + 1] = r;
            groupCounts[j + 1] = c;
        }
        return groups;
    }


    /**
     * Compares two scored hands: by category first, then by tiebreaker values
     * from left to right.
     * 
     * @param rankA
     *            the first hand's category
     * @param tiesA
     *            the first hand's tiebreaker values
     * @param rankB
     *            the second hand's category
     * @param tiesB
     *            the second hand's tiebreaker values
     * @return a positive number if the first hand wins, a negative number if
     *             the second hand wins, or 0 if they tie
     */
    private static
        int
        compareScores(HandRank rankA, int[] tiesA, HandRank rankB, int[] tiesB)
    {
        int byRank = rankA.compareTo(rankB);
        if (byRank != 0)
        {
            return byRank;
        }
        for (int i = 0; i < tiesA.length && i < tiesB.length; i++)
        {
            if (tiesA[i] != tiesB[i])
            {
                return tiesA[i] - tiesB[i];
            }
        }
        return 0;
    }


    /**
     * Sorts an array from highest to lowest using insertion sort.
     * 
     * @param a
     *            the array to sort in place
     */
    private static void sortDescending(int[] a)
    {
        for (int i = 1; i < a.length; i++)
        {
            int key = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] < key)
            {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }


    /**
     * Copies the start of an array into a new array.
     * 
     * @param source
     *            the array to copy from
     * @param length
     *            how many elements to copy
     * @return a new array holding the first length elements of source
     */
    private static int[] copy(int[] source, int length)
    {
        int[] result = new int[length];
        for (int i = 0; i < length; i++)
        {
            result[i] = source[i];
        }
        return result;
    }
}

package poker;

import student.TestCase;

/**
 * Tests for Hand: every hand category, Ace-high and Ace-low straights,
 * 5-, 6- and 7-card hands, tiebreakers, and bad input.
 *
 * @author Robert Laing (rlaing4308)
 * @version 2026.10.12
 */
public class HandTest
    extends TestCase
{
    /**
     * Builds a chain of cards from short codes such as "AS KH 10D 2C".
     * A = 1, J = 11, Q = 12, K = 13; the last letter is the suit
     * (S spades, H hearts, D diamonds, C clubs).
     *
     * @param codes space-separated card codes, or "" for no cards
     * @return a chain holding those cards
     */
    private LinkedChain<Card> cards(String codes)
    {
        LinkedChain<Card> chain = new LinkedChain<>();
        if (codes.isEmpty())
        {
            return chain;
        }
        for (String code : codes.split(" "))
        {
            String value = code.substring(0, code.length() - 1);
            char suit = code.charAt(code.length() - 1);

            int number;
            if (value.equals("A"))
            {
                number = 1;
            }
            else if (value.equals("J"))
            {
                number = 11;
            }
            else if (value.equals("Q"))
            {
                number = 12;
            }
            else if (value.equals("K"))
            {
                number = 13;
            }
            else
            {
                number = Integer.parseInt(value);
            }

            String suitName;
            if (suit == 'S')
            {
                suitName = "spades";
            }
            else if (suit == 'H')
            {
                suitName = "hearts";
            }
            else if (suit == 'D')
            {
                suitName = "diamonds";
            }
            else
            {
                suitName = "clubs";
            }

            chain.add(new Card(number, suitName));
        }
        return chain;
    }

    /**
     * Builds a Hand from hole-card and community-card codes.
     *
     * @param hole      the two hole cards
     * @param community the community cards
     * @return the evaluated hand
     */
    private Hand hand(String hole, String community)
    {
        return new Hand(cards(hole), cards(community));
    }

    // ----------------------------------------------------------
    // Every category, strongest to weakest

    /**
     * 10-J-Q-K-A all one suit is a royal flush.
     */
    public void testRoyalFlush()
    {
        assertEquals(HandRank.ROYAL_FLUSH,
            hand("AH KH", "QH JH 10H 2C 3D").getRank());
    }

    /**
     * Five in a row, all one suit, is a straight flush.
     */
    public void testStraightFlush()
    {
        assertEquals(HandRank.STRAIGHT_FLUSH,
            hand("9S 8S", "7S 6S 5S AH KD").getRank());
    }

    /**
     * A-2-3-4-5 all one suit is a straight flush, not a royal flush.
     */
    public void testWheelStraightFlush()
    {
        assertEquals(HandRank.STRAIGHT_FLUSH,
            hand("AD 2D", "3D 4D 5D KC QH").getRank());
    }

    /**
     * Four cards of one value.
     */
    public void testFourOfAKind()
    {
        assertEquals(HandRank.FOUR_OF_A_KIND,
            hand("7C 7D", "7H 7S 2C 3D 9H").getRank());
    }

    /**
     * Three of a kind plus a pair.
     */
    public void testFullHouse()
    {
        assertEquals(HandRank.FULL_HOUSE,
            hand("KC KD", "KH 4S 4C 2D 9H").getRank());
    }

    /**
     * Two sets of three still make a full house.
     */
    public void testFullHouseFromTwoTrips()
    {
        assertEquals(HandRank.FULL_HOUSE,
            hand("KC KD", "KH 4S 4C 4D 9H").getRank());
    }

    /**
     * Five cards of one suit.
     */
    public void testFlush()
    {
        assertEquals(HandRank.FLUSH,
            hand("2H 9H", "JH 4H 6H KC 3D").getRank());
    }

    /**
     * Five values in a row.
     */
    public void testStraight()
    {
        assertEquals(HandRank.STRAIGHT,
            hand("9C 8D", "7H 6S 5C 2D KH").getRank());
    }

    /**
     * The Ace plays low in A-2-3-4-5.
     */
    public void testWheelStraight()
    {
        assertEquals(HandRank.STRAIGHT,
            hand("AC 2D", "3H 4S 5C 9D KH").getRank());
    }

    /**
     * The Ace plays high in 10-J-Q-K-A.
     */
    public void testBroadwayStraight()
    {
        assertEquals(HandRank.STRAIGHT,
            hand("AC KD", "QH JS 10C 2D 3H").getRank());
    }

    /**
     * Straights do not wrap around: Q-K-A-2-3 is not a straight.
     */
    public void testNoWrapAroundStraight()
    {
        assertEquals(HandRank.HIGH_CARD,
            hand("QC KD", "AH 2S 3C 8D 9H").getRank());
    }

    /**
     * Three cards of one value.
     */
    public void testThreeOfAKind()
    {
        assertEquals(HandRank.THREE_OF_A_KIND,
            hand("QC QD", "QH 2S 5C 8D 9H").getRank());
    }

    /**
     * Two different pairs.
     */
    public void testTwoPair()
    {
        assertEquals(HandRank.TWO_PAIR,
            hand("JC JD", "4H 4S 9C 2D KH").getRank());
    }

    /**
     * Two cards of one value.
     */
    public void testOnePair()
    {
        assertEquals(HandRank.ONE_PAIR,
            hand("10C 10D", "2H 5S 7C 9D KH").getRank());
    }

    /**
     * Nothing matches.
     */
    public void testHighCard()
    {
        assertEquals(HandRank.HIGH_CARD,
            hand("2C 5D", "7H 9S JC KD 3H").getRank());
    }

    // ----------------------------------------------------------
    // Picking the best 5 of 5, 6 or 7 cards

    /**
     * When 7 cards hold both a straight and a flush, the flush is kept.
     */
    public void testPicksBestOfSeven()
    {
        assertEquals(HandRank.FLUSH,
            hand("9H 8H", "7H 6C 5H 2H KD").getRank());
    }

    /**
     * Exactly 5 cards (the flop) can be evaluated.
     */
    public void testFiveCards()
    {
        assertEquals(HandRank.THREE_OF_A_KIND,
            hand("AH AD", "AC 7S 2D").getRank());
    }

    /**
     * 6 cards (the turn) can be evaluated.
     */
    public void testSixCards()
    {
        assertEquals(HandRank.FULL_HOUSE,
            hand("AH AD", "AC 7S 2D 7H").getRank());
    }

    // ----------------------------------------------------------
    // compareTo

    /**
     * A higher category wins, in both directions.
     */
    public void testHigherCategoryWins()
    {
        Hand flush = hand("2H 9H", "JH 4H 6H KC 3D");
        Hand straight = hand("9C 8D", "7H 6S 5C 2D KH");

        assertTrue(flush.compareTo(straight) > 0);
        assertTrue(straight.compareTo(flush) < 0);
    }

    /**
     * Same category: the higher pair wins.
     */
    public void testHigherPairWins()
    {
        String board = "2C 7D 9H JS 4C";

        assertTrue(hand("KC KD", board).compareTo(hand("QC QD", board)) > 0);
    }

    /**
     * Same pair: the higher kicker wins.
     */
    public void testKickerWins()
    {
        String board = "AH 8D 3D 2H 6C";

        assertTrue(hand("AC KS", board).compareTo(hand("AS QD", board)) > 0);
    }

    /**
     * Same two pair: the fifth card decides.
     */
    public void testTwoPairKicker()
    {
        String board = "KH KD 9C 9S 2H";

        assertTrue(hand("AC 5S", board).compareTo(hand("QC 5D", board)) > 0);
    }

    /**
     * A 6-high straight beats the A-2-3-4-5 straight.
     */
    public void testWheelIsLowestStraight()
    {
        String board = "3H 4S 5C 9D KH";

        assertTrue(hand("6C 2D", board).compareTo(hand("AC 2D", board)) > 0);
    }

    /**
     * Full house: the higher three of a kind wins.
     */
    public void testFullHouseHigherTripsWins()
    {
        Hand kings = hand("KC KD", "KH 2S 2C 8D 9H");
        Hand queens = hand("QC QD", "QH AS AC 8D 9H");

        assertTrue(kings.compareTo(queens) > 0);
    }

    /**
     * When the board is the best hand for both, the pot is split.
     */
    public void testSplitPot()
    {
        String board = "AH KH QS JD 10C";

        assertEquals(0, hand("2C 3D", board).compareTo(hand("2H 3S", board)));
    }

    /**
     * A sixth card outside the best 5 does not break a tie.
     */
    public void testCardOutsideBestFiveIgnored()
    {
        String board = "AH AD KC QS JH";

        assertEquals(0, hand("3C 2S", board).compareTo(hand("4C 2D", board)));
    }

    // ----------------------------------------------------------
    // toString and bad input

    /**
     * toString() gives the category name.
     */
    public void testToString()
    {
        assertEquals("FLUSH", hand("2H 9H", "JH 4H 6H KC 3D").toString());
    }

    /**
     * Fewer than 5 cards is rejected.
     */
    public void testTooFewCards()
    {
        Exception thrown = null;

        try
        {
            hand("AH AD", "AC 7S");
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    /**
     * A null chain is rejected.
     */
    public void testNullChains()
    {
        Exception nullHole = null;
        Exception nullCommunity = null;

        try
        {
            new Hand(null, cards("AC 7S 2D"));
        }
        catch (Exception e)
        {
            nullHole = e;
        }

        try
        {
            new Hand(cards("AH AD"), null);
        }
        catch (Exception e)
        {
            nullCommunity = e;
        }

        assertTrue(nullHole instanceof IllegalArgumentException);
        assertTrue(nullCommunity instanceof IllegalArgumentException);
    }
}
package poker;

import student.TestCase;

public class DeckTest
    extends TestCase
{
    private Deck deck;

    /**
     * Creates a full, shuffled deck before each test.
     */
    public void setUp()
    {
        deck = new Deck();
        deck.reset();
    }

    // ----------------------------------------------------------
    /**
     * A new deck has no cards until reset() builds it.
     */
    public void testNewDeckIsEmpty()
    {
        assertEquals(0, new Deck().cardsLeft());
    }

    /**
     * reset() builds 52 cards.
     */
    public void testResetHas52Cards()
    {
        assertEquals(52, deck.cardsLeft());
    }

    /**
     * Dealing every card gives 52 different cards: no duplicates, none lost.
     */
    public void testAllCardsDistinct()
    {
        LinkedChain<Card> dealt = new LinkedChain<>();
        for (int i = 0; i < 52; i++)
        {
            Card card = deck.deal();
            assertNotNull(card);
            assertFalse(dealt.contains(card));
            dealt.add(card);
        }
        assertEquals(0, deck.cardsLeft());
    }

    /**
     * Every value 1-13 appears once in each of the four suits.
     */
    public void testEveryCardPresent()
    {
        LinkedChain<Card> dealt = new LinkedChain<>();
        while (deck.cardsLeft() > 0)
        {
            dealt.add(deck.deal());
        }

        String[] suits = { "hearts", "diamonds", "clubs", "spades" };
        for (String suit : suits)
        {
            for (int value = 1; value <= 13; value++)
            {
                assertTrue(dealt.contains(new Card(value, suit)));
            }
        }
    }

    // ----------------------------------------------------------
    /**
     * deal() removes one card at a time.
     */
    public void testDeal()
    {
        deck.deal();
        deck.deal();

        assertEquals(50, deck.cardsLeft());
    }

    /**
     * deal() on an empty deck returns null.
     */
    public void testDealEmpty()
    {
        assertNull(new Deck().deal());
    }

    // ----------------------------------------------------------
    /**
     * reset() after dealing brings the deck back to 52.
     */
    public void testResetAfterDealing()
    {
        for (int i = 0; i < 10; i++)
        {
            deck.deal();
        }

        deck.reset();

        assertEquals(52, deck.cardsLeft());
    }

    /**
     * shuffle() keeps the same 52 cards, and works on an empty deck too.
     */
    public void testShuffle()
    {
        deck.shuffle();
        assertEquals(52, deck.cardsLeft());

        Deck empty = new Deck();
        empty.shuffle();
        assertEquals(0, empty.cardsLeft());
    }
}
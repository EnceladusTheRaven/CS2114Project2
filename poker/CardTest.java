package poker;

import student.TestCase;

public class CardTest
    extends TestCase
{
    private Card aceOfSpades;

    /**
     * Creates a card before each test.
     */
    public void setUp()
    {
        aceOfSpades = new Card(1, "spades");
    }

    // ----------------------------------------------------------
    /**
     * The getters return what the constructor was given.
     */
    public void testGetters()
    {
        assertEquals(1, aceOfSpades.getValue());
        assertEquals("spades", aceOfSpades.getSuit());
    }

    // ----------------------------------------------------------
    /**
     * Cards with the same value and suit are equal.
     */
    public void testEqualsSameCard()
    {
        assertTrue(aceOfSpades.equals(aceOfSpades));
        assertTrue(aceOfSpades.equals(new Card(1, "spades")));
    }

    /**
     * Cards differing in value or suit, null, and other types are not equal.
     */
    @SuppressWarnings("unlikely-arg-type")
    public void testEqualsDifferent()
    {
        assertFalse(aceOfSpades.equals(new Card(2, "spades")));
        assertFalse(aceOfSpades.equals(new Card(1, "hearts")));
        assertFalse(aceOfSpades.equals(null));
        assertFalse(aceOfSpades.equals("Ace of spades"));
    }

    /**
     * Face cards and Aces print their names; others print their number.
     */
    public void testToString()
    {
        assertEquals("Ace of spades", aceOfSpades.toString());
        assertEquals("Jack of hearts", new Card(11, "hearts").toString());
        assertEquals("Queen of clubs", new Card(12, "clubs").toString());
        assertEquals("King of diamonds", new Card(13, "diamonds").toString());
        assertEquals("7 of hearts", new Card(7, "hearts").toString());
    }
}
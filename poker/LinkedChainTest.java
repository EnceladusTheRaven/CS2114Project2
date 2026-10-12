package poker;

import java.util.NoSuchElementException;
import student.TestCase;

/**
 * Tests for the generic LinkedChain, using Strings for simple cases and
 * Cards and Bets to show the same class works for more than one type.
 *
 * @author Robert Laing (rlaing4308)
 * @version 2026.10.12
 */
public class LinkedChainTest
    extends TestCase
{
    private LinkedChain<String> chain;

    /**
     * Creates an empty chain before each test.
     */
    public void setUp()
    {
        chain = new LinkedChain<>();
    }

    /**
     * Fills the chain with A, B, C (A at the head).
     */
    private void addABC()
    {
        chain.add("A");
        chain.add("B");
        chain.add("C");
    }

    // ----------------------------------------------------------
    /**
     * A new chain is empty.
     */
    public void testNewChainIsEmpty()
    {
        assertTrue(chain.isEmpty());
        assertEquals(0, chain.size());
        assertEquals("[]", chain.toString());
    }

    // ----------------------------------------------------------
    /**
     * add() appends to the end and keeps insertion order.
     */
    public void testAdd()
    {
        addABC();

        assertFalse(chain.isEmpty());
        assertEquals(3, chain.size());
        assertEquals("A", chain.get(0));
        assertEquals("C", chain.get(2));
        assertEquals("[A, B, C]", chain.toString());
    }

    /**
     * add(null) is rejected and the chain is unchanged.
     */
    public void testAddNull()
    {
        Exception thrown = null;

        try
        {
            chain.add(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(0, chain.size());
    }

    // ----------------------------------------------------------
    /**
     * addFirst() puts the item at the head, on empty and non-empty chains.
     */
    public void testAddFirst()
    {
        chain.addFirst("B");
        chain.addFirst("A");
        chain.add("C");

        assertEquals("[A, B, C]", chain.toString());
        assertEquals("A", chain.peek());
    }

    /**
     * addFirst(null) is rejected.
     */
    public void testAddFirstNull()
    {
        Exception thrown = null;

        try
        {
            chain.addFirst(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    // ----------------------------------------------------------
    /**
     * peek() returns the head without removing it.
     */
    public void testPeek()
    {
        addABC();

        assertEquals("A", chain.peek());
        assertEquals(3, chain.size());
    }

    /**
     * peek() on an empty chain throws.
     */
    public void testPeekEmpty()
    {
        Exception thrown = null;

        try
        {
            chain.peek();
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertNotNull(thrown);
        assertTrue(thrown instanceof NoSuchElementException);
    }

    // ----------------------------------------------------------
    /**
     * get() returns the element at an index without removing it.
     */
    public void testGet()
    {
        addABC();

        assertEquals("B", chain.get(1));
        assertEquals(3, chain.size());
    }

    /**
     * get() with a negative or too-large index throws.
     */
    public void testGetOutOfRange()
    {
        addABC();
        Exception low = null;
        Exception high = null;

        try
        {
            chain.get(-1);
        }
        catch (Exception e)
        {
            low = e;
        }

        try
        {
            chain.get(3);
        }
        catch (Exception e)
        {
            high = e;
        }

        assertTrue(low instanceof IndexOutOfBoundsException);
        assertTrue(high instanceof IndexOutOfBoundsException);
    }

    // ----------------------------------------------------------
    /**
     * set() replaces the element and returns the old one.
     */
    public void testSet()
    {
        addABC();

        assertEquals("B", chain.set(1, "X"));
        assertEquals("[A, X, C]", chain.toString());
        assertEquals(3, chain.size());
    }

    /**
     * set() with a bad index or a null item throws and changes nothing.
     */
    public void testSetInvalid()
    {
        addABC();
        Exception badIndex = null;
        Exception nullItem = null;

        try
        {
            chain.set(5, "X");
        }
        catch (Exception e)
        {
            badIndex = e;
        }

        try
        {
            chain.set(0, null);
        }
        catch (Exception e)
        {
            nullItem = e;
        }

        assertTrue(badIndex instanceof IndexOutOfBoundsException);
        assertTrue(nullItem instanceof IllegalArgumentException);
        assertEquals("[A, B, C]", chain.toString());
    }

    // ----------------------------------------------------------
    /**
     * getIndex() finds the first match, using equals().
     */
    public void testGetIndex()
    {
        addABC();
        chain.add("B");

        assertEquals(0, chain.getIndex("A"));
        assertEquals(1, chain.getIndex("B"));
        assertEquals(2, chain.getIndex(new String("C")));
    }

    /**
     * getIndex() returns -1 for a missing item or null.
     */
    public void testGetIndexMissing()
    {
        addABC();

        assertEquals(-1, chain.getIndex("Z"));
        assertEquals(-1, chain.getIndex(null));
    }

    // ----------------------------------------------------------
    /**
     * contains() is true for an item equal to one in the chain.
     */
    public void testContains()
    {
        addABC();

        assertTrue(chain.contains("B"));
        assertTrue(chain.contains(new String("C")));
    }

    /**
     * contains() is false for a missing item or null.
     */
    public void testContainsMissing()
    {
        addABC();

        assertFalse(chain.contains("Z"));
        assertFalse(chain.contains(null));
    }

    // ----------------------------------------------------------
    /**
     * count() counts duplicates, which the chain allows.
     */
    public void testCount()
    {
        chain.add("A");
        chain.add("B");
        chain.add("A");

        assertEquals(2, chain.count("A"));
        assertEquals(1, chain.count("B"));
    }

    /**
     * count() is 0 for a missing item or null.
     */
    public void testCountMissing()
    {
        addABC();

        assertEquals(0, chain.count("Z"));
        assertEquals(0, chain.count(null));
    }

    // ----------------------------------------------------------
    /**
     * removeFirst() returns the head and shrinks the chain.
     */
    public void testRemoveFirst()
    {
        addABC();

        assertEquals("A", chain.removeFirst());
        assertEquals("[B, C]", chain.toString());
        assertEquals(2, chain.size());
    }

    /**
     * removeFirst() on the last element leaves a usable empty chain.
     */
    public void testRemoveFirstLastElement()
    {
        chain.add("A");

        assertEquals("A", chain.removeFirst());
        assertTrue(chain.isEmpty());

        // The tail must have been cleared, so add() still works.
        chain.add("B");
        assertEquals("[B]", chain.toString());
    }

    /**
     * removeFirst() on an empty chain throws.
     */
    public void testRemoveFirstEmpty()
    {
        Exception thrown = null;

        try
        {
            chain.removeFirst();
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertNotNull(thrown);
        assertTrue(thrown instanceof NoSuchElementException);
    }

    // ----------------------------------------------------------
    /**
     * remove() works on the head, the middle and the tail.
     */
    public void testRemove()
    {
        addABC();
        chain.add("D");

        assertTrue(chain.remove("A"));   // head
        assertTrue(chain.remove("C"));   // middle
        assertTrue(chain.remove("D"));   // tail
        assertEquals("[B]", chain.toString());

        // Removing the tail must update it, so add() goes to the right place.
        chain.add("E");
        assertEquals("[B, E]", chain.toString());
    }

    /**
     * remove() removes only the first of several duplicates.
     */
    public void testRemoveDuplicate()
    {
        chain.add("A");
        chain.add("B");
        chain.add("A");

        assertTrue(chain.remove("A"));
        assertEquals("[B, A]", chain.toString());
    }

    /**
     * remove() returns false and changes nothing for a missing item, null,
     * or an empty chain.
     */
    public void testRemoveMissing()
    {
        assertFalse(chain.remove("A"));

        addABC();
        assertFalse(chain.remove("Z"));
        assertFalse(chain.remove(null));
        assertEquals("[A, B, C]", chain.toString());
    }

    // ----------------------------------------------------------
    /**
     * removeAt() works at the head, middle and tail.
     */
    public void testRemoveAt()
    {
        addABC();
        chain.add("D");

        assertEquals("A", chain.removeAt(0));
        assertEquals("C", chain.removeAt(1));
        assertEquals("D", chain.removeAt(1));
        assertEquals("[B]", chain.toString());

        chain.add("E");
        assertEquals("[B, E]", chain.toString());
    }

    /**
     * removeAt() with a bad index throws and changes nothing.
     */
    public void testRemoveAtOutOfRange()
    {
        addABC();
        Exception thrown = null;

        try
        {
            chain.removeAt(3);
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertTrue(thrown instanceof IndexOutOfBoundsException);
        assertEquals(3, chain.size());
    }

    // ----------------------------------------------------------
    /**
     * clear() empties the chain, and it can be reused afterward.
     */
    public void testClear()
    {
        addABC();
        chain.clear();

        assertTrue(chain.isEmpty());
        assertEquals("[]", chain.toString());

        chain.add("Z");
        assertEquals("Z", chain.peek());
    }

    // ----------------------------------------------------------
    /**
     * toArray() copies the elements in order.
     */
    public void testToArray()
    {
        addABC();

        Object[] array = chain.toArray();

        assertEquals(3, array.length);
        assertEquals("A", array[0]);
        assertEquals("C", array[2]);
    }

    /**
     * toArray() on an empty chain gives an empty array.
     */
    public void testToArrayEmpty()
    {
        assertEquals(0, chain.toArray().length);
    }

    // ----------------------------------------------------------
    /**
     * The same class stores Cards and Bets: two unrelated types.
     */
    public void testReuseWithCardsAndBets()
    {
        LinkedChain<Card> cards = new LinkedChain<>();
        cards.add(new Card(1, "spades"));
        cards.add(new Card(13, "hearts"));

        LinkedChain<Bet> bets = new LinkedChain<>();
        Bet blind = new Bet("You", Action.BET, 10, Street.PREFLOP);
        Bet call = new Bet("Opponent", Action.CALL, 10, Street.PREFLOP);
        bets.add(blind);
        bets.add(call);

        assertEquals(new Card(1, "spades"), cards.removeFirst());
        assertTrue(cards.contains(new Card(13, "hearts")));
        assertEquals(2, bets.size());
        assertEquals(call, bets.get(1));
    }
}
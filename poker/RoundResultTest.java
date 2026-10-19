package poker;

public class RoundResultTest
    extends student.TestCase
{
    private RoundResult showdownWin;
    private RoundResult foldWin;
    private RoundResult tie;
 
    /**
     * Creates one result of each kind before each test.
     */
    public void setUp()
    {
        showdownWin = new RoundResult(3, "You", HandRank.FLUSH, 80);
        foldWin = new RoundResult(1, "Opponent", null, 15);
        tie = new RoundResult(4, HandRank.TWO_PAIR, 60);
    }
 
    /**
     * Calls the win constructor and returns the exception it throws, or null.
     */
    @SuppressWarnings("unused")
    private Exception tryWin(int hand, String winner, HandRank rank, int pot)
    {
        try
        {
            new RoundResult(hand, winner, rank, pot);
        }
        catch (Exception e)
        {
            return e;
        }
        return null;
    }
 
    /**
     * Calls the tie constructor and returns the exception it throws, or null.
     */
    @SuppressWarnings("unused")
    private Exception tryTie(int hand, HandRank rank, int pot)
    {
        try
        {
            new RoundResult(hand, rank, pot);
        }
        catch (Exception e)
        {
            return e;
        }
        return null;
    }
 
    /**
     * A showdown win stores every value.
     */
    public void testShowdownWin()
    {
        assertEquals(3, showdownWin.getHandNumber());
        assertEquals("You", showdownWin.getWinner());
        assertEquals(HandRank.FLUSH, showdownWin.getWinningHand());
        assertEquals(80, showdownWin.getPotSize());
        assertFalse(showdownWin.isTie());
        assertFalse(showdownWin.endedInFold());
    }
 
    /**
     * A win by fold has no winning hand.
     */
    public void testFoldWin()
    {
        assertEquals("Opponent", foldWin.getWinner());
        assertNull(foldWin.getWinningHand());
        assertTrue(foldWin.endedInFold());
        assertFalse(foldWin.isTie());
    }
 
    /**
     * The player can also win by fold.
     */
    public void testPlayerWinsByFold()
    {
        RoundResult result = new RoundResult(2, "You", null, 30);
 
        assertTrue(result.endedInFold());
        assertEquals("Hand 2: You won 30 by fold", result.toString());
    }
 
    /**
     * A tie has no winner, keeps the shared hand, and is not a fold.
     */
    public void testTie()
    {
        assertTrue(tie.isTie());
        assertNull(tie.getWinner());
        assertEquals(HandRank.TWO_PAIR, tie.getWinningHand());
        assertEquals(60, tie.getPotSize());
        assertFalse(tie.endedInFold());
    }
 
    /**
     * Each kind of result has its own one-line description.
     */
    public void testToString()
    {
        assertEquals("Hand 3: You won 80 with FLUSH", showdownWin.toString());
        assertEquals("Hand 1: Opponent won 15 by fold", foldWin.toString());
        assertEquals("Hand 4: Tie with TWO_PAIR, pot of 60 split",
            tie.toString());
    }
 
    /**
     * A winner whose name is "Split" or "Tie" is still a normal win.
     */
    public void testNameLikeTieIsStillAWin()
    {
        RoundResult result = new RoundResult(1, "Tie", HandRank.ONE_PAIR, 20);
 
        assertFalse(result.isTie());
        assertEquals("Tie", result.getWinner());
    }
 
    /**
     * A win with hand number 0 is rejected.
     */
    @SuppressWarnings("unused")
    public void testWinHandNumberZero()
    {
        Exception thrown = null;
 
        try
        {
            new RoundResult(0, "You", null, 10);
        }
        catch (Exception e)
        {
            thrown = e;
        }
 
        assertNotNull(thrown);
    }
 
    /**
     * A tie with hand number 0 is rejected.
     */
    @SuppressWarnings("unused")
    public void testTieHandNumberZero()
    {
        Exception thrown = null;
 
        try
        {
            new RoundResult(0, HandRank.FLUSH, 10);
        }
        catch (Exception e)
        {
            thrown = e;
        }
 
        assertNotNull(thrown);
    }
 
    /**
     * A win with a negative hand number is rejected.
     */
    @SuppressWarnings("unused")
    public void testWinHandNumberNegative()
    {
        Exception thrown = null;
 
        try
        {
            new RoundResult(-1, "You", null, 10);
        }
        catch (Exception e)
        {
            thrown = e;
        }
 
        assertNotNull(thrown);
    }
 
    /**
     * A tie with a negative hand number is rejected.
     */
    @SuppressWarnings("unused")
    public void testTieHandNumberNegative()
    {
        Exception thrown = null;
 
        try
        {
            new RoundResult(-1, HandRank.FLUSH, 10);
        }
        catch (Exception e)
        {
            thrown = e;
        }
 
        assertNotNull(thrown);
    }
 
    /**
     * A win with a pot of 0 is rejected.
     */
    @SuppressWarnings("unused")
    public void testWinPotZero()
    {
        Exception thrown = null;
 
        try
        {
            new RoundResult(1, "You", null, 0);
        }
        catch (Exception e)
        {
            thrown = e;
        }
 
        assertNotNull(thrown);
    }
 
    /**
     * A tie with a pot of 0 is rejected.
     */
    @SuppressWarnings("unused")
    public void testTiePotZero()
    {
        Exception thrown = null;
 
        try
        {
            new RoundResult(1, HandRank.FLUSH, 0);
        }
        catch (Exception e)
        {
            thrown = e;
        }
 
        assertNotNull(thrown);
    }
 
    /**
     * A win with a negative pot is rejected.
     */
    @SuppressWarnings("unused")
    public void testWinPotNegative()
    {
        Exception thrown = null;
 
        try
        {
            new RoundResult(1, "You", null, -5);
        }
        catch (Exception e)
        {
            thrown = e;
        }
 
        assertNotNull(thrown);
    }
 
    /**
     * A tie with a negative pot is rejected.
     */
    @SuppressWarnings("unused")
    public void testTiePotNegative()
    {
        Exception thrown = null;
 
        try
        {
            new RoundResult(1, HandRank.FLUSH, -5);
        }
        catch (Exception e)
        {
            thrown = e;
        }
 
        assertNotNull(thrown);
    }
 
    /**
     * A null winner is rejected.
     */
    @SuppressWarnings("unused")
    public void testNullWinner()
    {
        Exception thrown = null;
 
        try
        {
            new RoundResult(1, null, null, 10);
        }
        catch (Exception e)
        {
            thrown = e;
        }
 
        assertNotNull(thrown);
    }
 
    /**
     * An empty winner name is rejected.
     */
    @SuppressWarnings("unused")
    public void testEmptyWinner()
    {
        Exception thrown = null;
 
        try
        {
            new RoundResult(1, "", HandRank.FLUSH, 10);
        }
        catch (Exception e)
        {
            thrown = e;
        }
 
        assertNotNull(thrown);
    }
 
    /**
     * A winner name of only spaces is rejected.
     */
    @SuppressWarnings("unused")
    public void testBlankWinner()
    {
        Exception thrown = null;
 
        try
        {
            new RoundResult(1, "   ", null, 10);
        }
        catch (Exception e)
        {
            thrown = e;
        }
 
        assertNotNull(thrown);
    }
 
    /**
     * A tie without a hand is rejected: folds cannot tie.
     */
    @SuppressWarnings("unused")
    public void testTieWithoutHand()
    {
        Exception thrown = null;
 
        try
        {
            new RoundResult(1, null, 10);
        }
        catch (Exception e)
        {
            thrown = e;
        }
 
        assertNotNull(thrown);
    }
 
 
    /**
     * Two results with the same values are different events, so they are
     * not equal and both stay in the history.
     */
    public void testSameValuesAreDifferentEvents()
    {
        RoundResult first = new RoundResult(1, "You", null, 15);
        RoundResult second = new RoundResult(1, "You", null, 15);
 
        assertFalse(first.equals(second));
 
        LinkedChain<RoundResult> history = new LinkedChain<>();
        history.add(first);
        history.add(second);
        assertEquals(2, history.size());
        assertEquals(1, history.count(first));
    }

}


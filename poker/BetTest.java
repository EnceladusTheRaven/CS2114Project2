package poker;

import student.TestCase;

public class BetTest
    extends TestCase
{
    private Bet raise;

    /**
     * Creates a bet before each test.
     */
    public void setUp()
    {
        raise = new Bet("You", Action.RAISE, 40, Street.FLOP);
    }

    /**
     * Calls the Bet constructor and returns the exception it throws, or null.
     */
    private Exception tryBet(String bettor, Action action, int amount,
        Street street)
    {
        try
        {
            new Bet(bettor, action, amount, street);
        }
        catch (Exception e)
        {
            return e;
        }
        return null;
    }

    // ----------------------------------------------------------
    /**
     * The getters return what the constructor was given.
     */
    public void testGetters()
    {
        assertEquals("You", raise.getBettor());
        assertEquals(Action.RAISE, raise.getAction());
        assertEquals(40, raise.getAmount());
        assertEquals(Street.FLOP, raise.getStreet());
    }

    /**
     * BET and CALL are also allowed.
     */
    public void testBetAndCallAllowed()
    {
        assertNull(tryBet("You", Action.BET, 10, Street.PREFLOP));
        assertNull(tryBet("Opponent", Action.CALL, 10, Street.RIVER));
    }

    // ----------------------------------------------------------
    /**
     * Any null argument is rejected.
     */
    public void testNullArguments()
    {
        assertTrue(tryBet(null, Action.BET, 10, Street.FLOP)
            instanceof IllegalArgumentException);
        assertTrue(tryBet("You", null, 10, Street.FLOP)
            instanceof IllegalArgumentException);
        assertTrue(tryBet("You", Action.BET, 10, null)
            instanceof IllegalArgumentException);
    }

    /**
     * A zero or negative amount is rejected.
     */
    public void testAmountNotPositive()
    {
        assertTrue(tryBet("You", Action.BET, 0, Street.FLOP)
            instanceof IllegalArgumentException);
        assertTrue(tryBet("You", Action.BET, -5, Street.FLOP)
            instanceof IllegalArgumentException);
    }

    /**
     * CHECK and FOLD move no chips, so they are not bets.
     */
    public void testCheckAndFoldRejected()
    {
        assertTrue(tryBet("You", Action.CHECK, 10, Street.FLOP)
            instanceof IllegalArgumentException);
        assertTrue(tryBet("You", Action.FOLD, 10, Street.FLOP)
            instanceof IllegalArgumentException);
    }
}
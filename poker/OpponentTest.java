package poker;

import student.TestCase;

public class OpponentTest
    extends TestCase
{
    private Opponent opponent;

    /**
     * Creates an opponent with 100 chips before each test.
     */
    public void setUp()
    {
        opponent = new Opponent("Bot", 100);
    }

    /**
     * Gives the opponent two hole cards.
     */
    private void deal(Card first, Card second)
    {
        opponent.receiveCard(first);
        opponent.receiveCard(second);
    }

    /**
     * Makes a chain of community cards.
     */
    private LinkedChain<Card> board(Card... cards)
    {
        LinkedChain<Card> chain = new LinkedChain<>();
        for (Card card : cards)
        {
            chain.add(card);
        }
        return chain;
    }

    // ----------------------------------------------------------
    // Constructor and state

    /**
     * The constructor sets the starting state.
     */
    public void testConstructorAndGetters()
    {
        assertEquals("Bot", opponent.getName());
        assertEquals(100, opponent.getChips());
        assertEquals(0, opponent.getCurrentBet());
        assertFalse(opponent.hasFolded());
        assertEquals(0, opponent.getHoleCards().size());
    }

    /**
     * A null name is rejected.
     */
    public void testConstructorNullName()
    {
        Exception thrown = null;

        try
        {
            new Opponent(null, 100);
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertTrue(thrown instanceof IllegalArgumentException);
    }

    /**
     * Negative chips are rejected.
     */
    public void testConstructorNegativeChips()
    {
        Exception thrown = null;

        try
        {
            new Opponent("Bot", -1);
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertTrue(thrown instanceof IllegalArgumentException);
    }

    /**
     * receiveCard() adds a hole card.
     */
    public void testReceiveCard()
    {
        Card card = new Card(5, "hearts");
        opponent.receiveCard(card);

        assertEquals(1, opponent.getHoleCards().size());
        assertEquals(card, opponent.getHoleCards().get(0));
    }

    /**
     * receiveCard(null) is rejected.
     */
    public void testReceiveNullCard()
    {
        Exception thrown = null;

        try
        {
            opponent.receiveCard(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertTrue(thrown instanceof IllegalArgumentException);
    }

    /**
     * A third hole card is rejected.
     */
    public void testReceiveThirdCard()
    {
        deal(new Card(2, "hearts"), new Card(3, "hearts"));
        Exception thrown = null;

        try
        {
            opponent.receiveCard(new Card(4, "hearts"));
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertTrue(thrown instanceof IllegalStateException);
    }

    /**
     * placeBet() moves chips into the current bet.
     */
    public void testPlaceBet()
    {
        opponent.placeBet(30);

        assertEquals(70, opponent.getChips());
        assertEquals(30, opponent.getCurrentBet());
    }

    /**
     * Betting more than you have, or a negative amount, is rejected.
     */
    public void testPlaceBetInvalid()
    {
        Exception tooMuch = null;
        Exception negative = null;

        try
        {
            opponent.placeBet(101);
        }
        catch (Exception e)
        {
            tooMuch = e;
        }

        try
        {
            opponent.placeBet(-1);
        }
        catch (Exception e)
        {
            negative = e;
        }

        assertTrue(tooMuch instanceof IllegalArgumentException);
        assertTrue(negative instanceof IllegalArgumentException);
        assertEquals(100, opponent.getChips());
    }

    /**
     * winChips() adds chips; a negative amount is rejected.
     */
    public void testWinChips()
    {
        opponent.winChips(50);
        assertEquals(150, opponent.getChips());

        Exception thrown = null;
        try
        {
            opponent.winChips(-1);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    /**
     * fold(), resetCurrentBet() and reset() change the right state.
     */
    public void testFoldAndReset()
    {
        deal(new Card(2, "hearts"), new Card(3, "hearts"));
        opponent.placeBet(20);
        opponent.fold();

        assertTrue(opponent.hasFolded());

        opponent.resetCurrentBet();
        assertEquals(0, opponent.getCurrentBet());

        opponent.placeBet(10);
        opponent.reset();
        assertEquals(0, opponent.getHoleCards().size());
        assertEquals(0, opponent.getCurrentBet());
        assertFalse(opponent.hasFolded());
        assertEquals(70, opponent.getChips());
    }

    // ----------------------------------------------------------
    // Decisions before the flop

    /**
     * A strong hand with no bet to call always bets.
     */
    public void testStrongHandBets()
    {
        deal(new Card(1, "spades"), new Card(1, "hearts"));

        assertEquals(Action.BET, opponent.chooseAction(0, 10));
    }

    /**
     * Ace-King counts as strong.
     */
    public void testAceKingIsStrong()
    {
        deal(new Card(1, "spades"), new Card(13, "hearts"));

        assertEquals(Action.BET, opponent.chooseAction(0, 10));
    }

    /**
     * A high pair (10 or above) counts as strong.
     */
    public void testHighPairIsStrong()
    {
        deal(new Card(10, "spades"), new Card(10, "hearts"));

        assertEquals(Action.BET, opponent.chooseAction(0, 10));
    }

    /**
     * Without enough chips to bet, it checks even with a strong hand.
     */
    public void testChecksWhenItCannotBet()
    {
        Opponent poor = new Opponent("Poor", 5);
        poor.receiveCard(new Card(1, "spades"));
        poor.receiveCard(new Card(1, "hearts"));

        assertEquals(Action.CHECK, poor.chooseAction(0, 10));
    }

    /**
     * A strong hand facing a bet raises or calls, never folds.
     */
    public void testStrongHandFacingBet()
    {
        deal(new Card(1, "spades"), new Card(1, "hearts"));

        Action action = opponent.chooseAction(10, 10);

        assertTrue(action == Action.RAISE || action == Action.CALL);
    }

    /**
     * A low pair is medium strength and calls a bet.
     */
    public void testLowPairCalls()
    {
        deal(new Card(5, "spades"), new Card(5, "hearts"));

        assertEquals(Action.CALL, opponent.chooseAction(20, 10));
    }

    /**
     * A Jack-high hand is medium strength and calls a bet.
     */
    public void testJackHighCalls()
    {
        deal(new Card(11, "spades"), new Card(3, "hearts"));

        assertEquals(Action.CALL, opponent.chooseAction(20, 10));
    }

    /**
     * Suited cards close together are medium strength and call a bet.
     */
    public void testSuitedConnectorsCall()
    {
        deal(new Card(5, "hearts"), new Card(6, "hearts"));

        assertEquals(Action.CALL, opponent.chooseAction(20, 10));
    }

    /**
     * A weak hand calls a small bet (10 times the bet fits in its chips).
     */
    public void testWeakHandCallsSmallBet()
    {
        deal(new Card(2, "clubs"), new Card(7, "diamonds"));

        assertEquals(Action.CALL, opponent.chooseAction(5, 10));
    }

    /**
     * A weak hand folds to a big bet.
     */
    public void testWeakHandFoldsToBigBet()
    {
        deal(new Card(2, "clubs"), new Card(7, "diamonds"));

        assertEquals(Action.FOLD, opponent.chooseAction(20, 10));
    }

    /**
     * With fewer than two hole cards the hand counts as weak.
     */
    public void testNoCardsIsWeak()
    {
        assertEquals(Action.FOLD, opponent.chooseAction(50, 10));
    }

    // ----------------------------------------------------------
    // Decisions after the flop

    /**
     * Three of a kind on the flop is strong, so it bets.
     */
    public void testTripsOnFlopBets()
    {
        deal(new Card(9, "spades"), new Card(9, "hearts"));
        opponent.setCommunityCards(board(new Card(9, "clubs"),
            new Card(2, "diamonds"), new Card(5, "hearts")));

        assertEquals(Action.BET, opponent.chooseAction(0, 10));
    }

    /**
     * One pair on the flop is medium, so it calls a bet.
     */
    public void testPairOnFlopCalls()
    {
        deal(new Card(9, "spades"), new Card(4, "hearts"));
        opponent.setCommunityCards(board(new Card(9, "clubs"),
            new Card(2, "diamonds"), new Card(13, "hearts")));

        assertEquals(Action.CALL, opponent.chooseAction(20, 10));
    }

    /**
     * Nothing on the flop is weak, so it folds to a big bet.
     */
    public void testNothingOnFlopFolds()
    {
        deal(new Card(3, "spades"), new Card(4, "hearts"));
        opponent.setCommunityCards(board(new Card(9, "clubs"),
            new Card(12, "diamonds"), new Card(13, "hearts")));

        assertEquals(Action.FOLD, opponent.chooseAction(20, 10));
    }

    /**
     * With fewer than 3 community cards it uses the pre-flop rules.
     */
    public void testTwoCommunityCardsUsesPreflopRules()
    {
        deal(new Card(1, "spades"), new Card(1, "hearts"));
        opponent.setCommunityCards(board(new Card(2, "clubs"),
            new Card(7, "diamonds")));

        assertEquals(Action.BET, opponent.chooseAction(0, 10));
    }

    // ----------------------------------------------------------
    // chooseRaiseAmount

    /**
     * A weak hand raises exactly the minimum.
     */
    public void testWeakRaiseIsMinimum()
    {
        deal(new Card(2, "clubs"), new Card(7, "diamonds"));

        assertEquals(10, opponent.chooseRaiseAmount(10));
    }

    /**
     * A strong hand raises the minimum or double the minimum.
     */
    public void testStrongRaiseAmount()
    {
        deal(new Card(1, "spades"), new Card(1, "hearts"));

        int amount = opponent.chooseRaiseAmount(10);

        assertTrue(amount == 10 || amount == 20);
    }

    /**
     * A raise is never more than the opponent's chips.
     */
    public void testRaiseCappedAtChips()
    {
        Opponent poor = new Opponent("Poor", 15);
        poor.receiveCard(new Card(2, "clubs"));
        poor.receiveCard(new Card(7, "diamonds"));

        assertEquals(15, poor.chooseRaiseAmount(20));
    }
}
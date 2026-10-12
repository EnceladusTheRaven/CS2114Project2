package poker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import student.TestCase;

public class TableTest
    extends TestCase
{
    private Player player;
    private Opponent opponent;
    private Table table;

    /**
     * Creates a table with 500 chips each and blinds of 5/10.
     */
    public void setUp()
    {
        player = new Player("You", 500, new Scanner(""));
        opponent = new Opponent("Opponent", 500);
        table = new Table(player, opponent, 5);
    }

    // ----------------------------------------------------------
    // Opponents with fixed behavior

    /**
     * An opponent that never bets: checks when it can, otherwise calls.
     *
     * @param chips its starting chips
     * @return the opponent
     */
    private Opponent passive(int chips)
    {
        return new Opponent("Opponent", chips)
        {
            @Override
            public Action chooseAction(int toCall, int minRaise)
            {
                return toCall == 0 ? Action.CHECK : Action.CALL;
            }
        };
    }

    /**
     * An opponent that bets whenever nobody has bet, and otherwise calls.
     *
     * @return the opponent
     */
    private Opponent aggressive()
    {
        return new Opponent("Opponent", 500)
        {
            @Override
            public Action chooseAction(int toCall, int minRaise)
            {
                return toCall == 0 ? Action.BET : Action.CALL;
            }
        };
    }

    /**
     * An opponent that checks when it can and folds to any bet.
     *
     * @return the opponent
     */
    private Opponent folder()
    {
        return new Opponent("Opponent", 500)
        {
            @Override
            public Action chooseAction(int toCall, int minRaise)
            {
                return toCall == 0 ? Action.CHECK : Action.FOLD;
            }
        };
    }

    /**
     * An opponent that always says CHECK, even when that is illegal.
     *
     * @return the opponent
     */
    private Opponent alwaysChecks()
    {
        return new Opponent("Opponent", 500)
        {
            @Override
            public Action chooseAction(int toCall, int minRaise)
            {
                return Action.CHECK;
            }
        };
    }

    /**
     * Plays a whole game with the given input and opponent, and returns
     * everything the game printed.
     *
     * @param input       the player's typed lines
     * @param playerChips the player's starting chips
     * @param opp         the opponent to play against
     * @return the game's printed output
     */
    private String playGame(String input, int playerChips, Opponent opp)
    {
        Scanner scanner = new Scanner(input);
        player = new Player("You", playerChips, scanner);
        opponent = opp;
        table = new Table(player, opponent, 5);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try
        {
            System.setOut(new PrintStream(output));
            table.play(scanner);
        }
        finally
        {
            System.setOut(originalOut);
        }
        return output.toString();
    }

    // ----------------------------------------------------------
    // Constructor and state methods

    /**
     * The constructor sets up an empty table.
     */
    public void testConstructorAndGetters()
    {
        assertNotNull(table.getDeck());
        assertEquals(player, table.getPlayer());
        assertEquals(opponent, table.getOpponent());
        assertEquals(0, table.getCommunityCards().size());
        assertEquals(0, table.getBets().size());
        assertEquals(Street.PREFLOP, table.getStreet());
        assertEquals(5, table.getSmallBlind());
        assertEquals(10, table.getBigBlind());
        assertFalse(table.isHandInProgress());
        assertTrue(table.isDealerPlayer());
        assertEquals(0, table.potTotal());
    }

    /**
     * startHand() shuffles a full deck and starts a hand.
     */
    public void testStartHand()
    {
        table.startHand();

        assertTrue(table.isHandInProgress());
        assertEquals(52, table.getDeck().cardsLeft());
        assertEquals(Street.PREFLOP, table.getStreet());
    }

    /**
     * startHand() during a hand does nothing (the deck is not reshuffled).
     */
    public void testStartHandTwice()
    {
        table.startHand();
        table.getDeck().deal();

        table.startHand();

        assertEquals(51, table.getDeck().cardsLeft());
    }

    /**
     * endHand() ends the hand and passes the dealer button.
     */
    public void testEndHand()
    {
        table.startHand();
        table.endHand();

        assertFalse(table.isHandInProgress());
        assertFalse(table.isDealerPlayer());
    }

    /**
     * endHand() with no hand in progress does nothing.
     */
    public void testEndHandWithoutHand()
    {
        table.endHand();

        assertTrue(table.isDealerPlayer());
    }

    /**
     * addBet() records bets during a hand, and potTotal() adds them up.
     */
    public void testAddBetAndPotTotal()
    {
        table.startHand();
        table.addBet(new Bet("You", Action.BET, 10, Street.PREFLOP));
        table.addBet(new Bet("Opponent", Action.CALL, 10, Street.PREFLOP));

        assertEquals(2, table.getBets().size());
        assertEquals(20, table.potTotal());
    }

    /**
     * addBet() ignores null, and ignores bets when no hand is in progress.
     */
    public void testAddBetIgnored()
    {
        table.addBet(new Bet("You", Action.BET, 10, Street.PREFLOP));
        table.startHand();
        table.addBet(null);

        assertEquals(0, table.getBets().size());
    }

    /**
     * addCommunityCard() adds at most 5 cards, and only during a hand.
     */
    public void testAddCommunityCard()
    {
        table.addCommunityCard(new Card(1, "spades"));
        assertEquals(0, table.getCommunityCards().size());

        table.startHand();
        table.addCommunityCard(null);
        for (int i = 1; i <= 6; i++)
        {
            table.addCommunityCard(new Card(i, "hearts"));
        }

        assertEquals(5, table.getCommunityCards().size());
    }

    /**
     * setStreet() changes the round only during a hand, and ignores null.
     */
    public void testSetStreet()
    {
        table.setStreet(Street.FLOP);
        assertEquals(Street.PREFLOP, table.getStreet());

        table.startHand();
        table.setStreet(Street.TURN);
        assertEquals(Street.TURN, table.getStreet());

        table.setStreet(null);
        assertEquals(Street.TURN, table.getStreet());
    }

    // ----------------------------------------------------------
    // Whole games

    /**
     * Folding pre-flop as the small blind loses 5 chips.
     */
    public void testFoldFirstHand()
    {
        String output = playGame("fold\nleave\n", 500,
            new Opponent("Opponent", 500));

        assertEquals(495, player.getChips());
        assertEquals(505, opponent.getChips());
        assertTrue(output.contains("You fold."));
        assertTrue(output.contains("Hands played: 1"));
        assertTrue(output.contains("Net result:   -5"));
    }

    /**
     * The dealer alternates: in hand 2 the player is the big blind and
     * loses 10 by folding.
     */
    public void testDealerAlternates()
    {
        playGame("fold\nplay\nfold\nleave\n", 500, passive(500));

        assertEquals(485, player.getChips());
        assertEquals(515, opponent.getChips());
    }

    /**
     * Checking down to the river reaches a showdown, and no chips are
     * created or lost.
     */
    public void testShowdown()
    {
        String output = playGame("call\ncheck\ncheck\ncheck\nleave\n", 500,
            passive(500));

        assertTrue(output.contains("SHOWDOWN"));
        assertEquals(1000, player.getChips() + opponent.getChips());
    }

    /**
     * Saying "raise" with no bet to match is rejected with a reason.
     */
    public void testRaiseWithNoBetRejected()
    {
        String output = playGame(
            "call\nraise\ncheck\ncheck\ncheck\nleave\n", 500, passive(500));

        assertTrue(output.contains("No bet to match"));
        assertEquals(1000, player.getChips() + opponent.getChips());
    }

    /**
     * Saying "check" when facing a bet is rejected; the player then calls.
     */
    public void testCheckFacingBetRejected()
    {
        String output = playGame(
            "call\ncheck\ncall\ncall\ncall\ncall\nleave\n", 500, aggressive());

        assertTrue(output.contains("Opponent bets"));
        assertTrue(output.contains("You call"));
        assertEquals(1000, player.getChips() + opponent.getChips());
    }

    /**
     * When the player bets and the opponent folds, the player wins the pot.
     */
    public void testOpponentFolds()
    {
        String output = playGame("raise\n20\nleave\n", 500, folder());

        assertTrue(output.contains("Opponent folds."));
        assertTrue(output.contains("You win 40."));
        assertEquals(510, player.getChips());
    }

    /**
     * An illegal opponent choice (CHECK facing a raise) becomes a call.
     */
    public void testIllegalOpponentChoiceBecomesCall()
    {
        String output = playGame("raise\n20\ncheck\ncheck\ncheck\nleave\n",
            500, alwaysChecks());

        assertTrue(output.contains("Opponent calls 20."));
        assertEquals(1000, player.getChips() + opponent.getChips());
    }

    /**
     * A raise bigger than the opponent can call is capped at what it has.
     */
    public void testRaiseCapped()
    {
        String output = playGame("raise\n200\nleave\n", 500, passive(50));

        assertTrue(output.contains("Raise capped at 40"));
        assertEquals(550, player.getChips() + opponent.getChips());
    }

    /**
     * Poker commands between hands are rejected.
     */
    public void testCommandBetweenHandsRejected()
    {
        String output = playGame("fold\nraise\nleave\n", 500, passive(500));

        assertTrue(output.contains("No hand in progress"));
    }

    /**
     * Running out of input at the "play or leave" prompt ends the game.
     */
    public void testInputEndsAtPrompt()
    {
        String output = playGame("fold\n", 500, passive(500));

        assertTrue(output.contains("Summary"));
    }

    /**
     * A player who can't afford the big blind can't start a hand.
     */
    public void testPlayerCantAffordBlind()
    {
        String output = playGame("", 5, passive(500));

        assertTrue(output.contains("You can't afford the big blind"));
        assertTrue(output.contains("Hands played: 0"));
    }

    /**
     * When the opponent can't afford the big blind, the player wins.
     */
    public void testOpponentCantAffordBlind()
    {
        String output = playGame("", 500, passive(5));

        assertTrue(output.contains("You win the game"));
    }

    // ----------------------------------------------------------
    // main

    /**
     * main() starts a game that reads from System.in.
     */
    public void testMain()
    {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try
        {
            System.setIn(new ByteArrayInputStream("fold\nleave\n".getBytes()));
            System.setOut(new PrintStream(output));
            Table.main(new String[0]);
        }
        finally
        {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        assertTrue(output.toString().contains("Welcome to Practice Poker"));
        assertTrue(output.toString().contains("Hands played: 1"));
    }
}
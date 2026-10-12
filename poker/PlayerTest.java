package poker;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import student.TestCase;

public class PlayerTest
    extends TestCase
{
    private Player player;

    public void setUp()
    {
        player = new Player("Shrey", 100);
    }

    public void testConstructorAndGetters()
    {
        assertEquals("Shrey", player.getName());
        assertEquals(100, player.getChips());
        assertEquals(0, player.getCurrentBet());
        assertEquals(100, player.getStartingChips());
        assertFalse(player.hasFolded());
        assertEquals(0, player.getHoleCards().size());
    }

    public void testConstructorNullName()
    {
        Exception thrown = null;

        try
        {
            new Player(null, 100);
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    public void testConstructorNegativeChips()
    {
        Exception thrown = null;

        try
        {
            new Player("Shrey", -1);
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    public void testReceiveCard()
    {
        Card card = new Card(1, "spades");

        player.receiveCard(card);

        assertEquals(1, player.getHoleCards().size());
        assertEquals(card, player.getHoleCards().get(0));
    }

    public void testReceiveNullCard()
    {
        Exception thrown = null;

        try
        {
            player.receiveCard(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    public void testReceiveMoreThanTwoCards()
    {
        player.receiveCard(new Card(1, "spades"));
        player.receiveCard(new Card(2, "hearts"));

        Exception thrown = null;

        try
        {
            player.receiveCard(new Card(3, "clubs"));
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalStateException);
    }

    public void testPlaceBet()
    {
        player.placeBet(25);

        assertEquals(75, player.getChips());
        assertEquals(25, player.getCurrentBet());
    }

    public void testPlaceBetTooMuch()
    {
        Exception thrown = null;

        try
        {
            player.placeBet(101);
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    public void testPlaceBetNegative()
    {
        Exception thrown = null;

        try
        {
            player.placeBet(-1);
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    public void testWinChips()
    {
        player.winChips(50);

        assertEquals(150, player.getChips());
    }

    public void testWinNegativeChips()
    {
        Exception thrown = null;

        try
        {
            player.winChips(-1);
        }
        catch (Exception e)
        {
            thrown = e;
        }

        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    public void testFold()
    {
        assertFalse(player.hasFolded());

        player.fold();

        assertTrue(player.hasFolded());
    }

    public void testResetCurrentBet()
    {
        player.placeBet(20);

        assertEquals(20, player.getCurrentBet());

        player.resetCurrentBet();

        assertEquals(0, player.getCurrentBet());
    }

    public void testReset()
    {
        player.receiveCard(new Card(10, "clubs"));
        player.placeBet(20);
        player.fold();

        player.reset();

        assertEquals(0, player.getHoleCards().size());
        assertEquals(0, player.getCurrentBet());
        assertFalse(player.hasFolded());

        // Total chips should NOT reset between hands.
        assertEquals(80, player.getChips());
    }

    public void testChooseCheck()
    {
        InputStream original = System.in;

        try
        {
            System.setIn(
                new ByteArrayInputStream("check\n".getBytes()));

            Player inputPlayer = new Player("Shrey", 100);

            assertEquals(
                Action.CHECK,
                inputPlayer.chooseAction(0, 10));
        }
        finally
        {
            System.setIn(original);
        }
    }

    public void testChooseActionRejectsInvalid()
    {
        InputStream original = System.in;

        try
        {
            System.setIn(
                new ByteArrayInputStream(
                    "check\ncall\n".getBytes()));

            Player inputPlayer = new Player("Shrey", 100);

            // CHECK is illegal because there are 10 chips to call.
            // It should re-prompt and then accept CALL.
            assertEquals(
                Action.CALL,
                inputPlayer.chooseAction(10, 10));
        }
        finally
        {
            System.setIn(original);
        }
    }

    public void testChooseRaiseAmount()
    {
        InputStream original = System.in;

        try
        {
            System.setIn(
                new ByteArrayInputStream("20\n".getBytes()));

            Player inputPlayer = new Player("Shrey", 100);

            assertEquals(
                20,
                inputPlayer.chooseRaiseAmount(10));
        }
        finally
        {
            System.setIn(original);
        }
    }

    public void testChooseRaiseAmountRejectsBadInput()
    {
        InputStream original = System.in;

        try
        {
            System.setIn(
                new ByteArrayInputStream(
                    "hello\n5\n200\n20\n".getBytes()));

            Player inputPlayer = new Player("Shrey", 100);

            // hello = nonnumeric
            // 5 = below minimum
            // 200 = more chips than player has
            // 20 = valid
            assertEquals(
                20,
                inputPlayer.chooseRaiseAmount(10));
        }
        finally
        {
            System.setIn(original);
        }
    }
}
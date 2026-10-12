package poker;

import java.util.Scanner;

public class Player
{
    private String name;
    private int chips;
    private int currentBet;
    private LinkedChain<Card> holeCards;
    private boolean folded;
    private Scanner input;
    private final int startingChips;

    public Player(String name, int chips)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Name cannot be null");
        }

        if (chips < 0)
        {
            throw new IllegalArgumentException("Chips cannot be negative");
        }

        this.name = name;
        this.chips = chips;
        this.currentBet = 0;
        this.holeCards = new LinkedChain<>();
        this.folded = false;
        this.input = new Scanner(System.in);
        this.startingChips = chips;
    }

    public String getName()
    {
        return name;
    }

    public int getChips()
    {
        return chips;
    }

    public int getCurrentBet()
    {
        return currentBet;
    }

    public LinkedChain<Card> getHoleCards()
    {
        return holeCards;
    }

    public boolean hasFolded()
    {
        return folded;
    }

    public int getStartingChips()
    {
        return startingChips;
    }

    public void receiveCard(Card c)
    {
        if (c == null)
        {
            throw new IllegalArgumentException("Card cannot be null");
        }

        if (holeCards.size() >= 2)
        {
            throw new IllegalStateException("Already holding two cards");
        }

        holeCards.add(c);
    }

    public void placeBet(int amount)
    {
        if (amount < 0 || amount > chips)
        {
            throw new IllegalArgumentException(
                "Invalid bet " + amount + " with " + chips + " chips");
        }

        chips -= amount;
        currentBet += amount;
    }

    public void winChips(int amount)
    {
        if (amount < 0)
        {
            throw new IllegalArgumentException("Cannot win negative chips");
        }

        chips += amount;
    }

    public void fold()
    {
        folded = true;
    }

    public void resetCurrentBet()
    {
        currentBet = 0;
    }

    public void reset()
    {
        holeCards.clear();
        currentBet = 0;
        folded = false;
    }

    public Action chooseAction(int toCall, int minRaise)
    {
        while (true)
        {
            System.out.println("Choose action: check, bet, call, raise, or fold");
            String choice = input.nextLine().trim().toUpperCase();

            if (choice.equals("CHECK") && toCall == 0)
            {
                return Action.CHECK;
            }

            if (choice.equals("BET") && toCall == 0 && chips > 0)
            {
                return Action.BET;
            }

            if (choice.equals("CALL") && toCall > 0 && toCall <= chips)
            {
                return Action.CALL;
            }

            if (choice.equals("RAISE")
                && chips > toCall + minRaise)
            {
                return Action.RAISE;
            }

            if (choice.equals("FOLD"))
            {
                return Action.FOLD;
            }

            System.out.println("Invalid action. Try again.");
        }
    }

    public int chooseRaiseAmount(int minRaise)
    {
        while (true)
        {
            System.out.println(
                "Enter raise amount (minimum " + minRaise + "):");

            String text = input.nextLine();

            try
            {
                int amount = Integer.parseInt(text);

                if (amount >= minRaise && amount <= chips)
                {
                    return amount;
                }

                System.out.println("Invalid raise amount. Try again.");
            }
            catch (NumberFormatException e)
            {
                System.out.println("Please enter a number.");
            }
        }
    }
}
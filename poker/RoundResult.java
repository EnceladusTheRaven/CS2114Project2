package poker;

public class RoundResult
{
    private final int handNumber;
    private final String winner;
    private final HandRank winningHand;
    private final int potSize;
    private final boolean tie;

    /**
     * Constructor that includes a winner
     *
     * @param handNumber
     *            which hand this was, starting at 1
     * @param winner
     *            the winner's name
     * @param winningHand
     *            the winning hand's category, or null if the hand ended because
     *            someone folded
     * @param potSize
     *            the number of chips in the pot
     * @throws IllegalArgumentException
     *             if winner name is empty, hand < 1 or pot < 2
     */
    public RoundResult(
        int handNumber,
        String winner,
        HandRank winningHand,
        int potSize)
    {
        if (handNumber < 1)
        {
            throw new IllegalArgumentException(
                "Hand number must be at least 1.");
        }
        
        if (potSize <= 0)
        {
            throw new IllegalArgumentException(
                "Pot size must be greater than zero.");
        }
        
        if (winner == null || winner.trim().isEmpty())
        {
            throw new IllegalArgumentException("Winner must have a name.");
        }

        this.handNumber = handNumber;
        this.winner = winner;
        this.winningHand = winningHand;
        this.potSize = potSize;
        this.tie = false;
    }


    /**
     * Overloaded constructor in case of ties
     * 
     * @param handNumber
     *            which hand this was, starting at 1
     * @param tiedHand
     *            the category both players had
     * @param potSize
     *            the number of chips in the pot
     * @throws IllegalArgumentException
     *             if handNumber is less than 1, tiedHand is null, or potSize is
     *             not positive
     */
    public RoundResult(int handNumber, HandRank tiedHand, int potSize)
    {
        if (handNumber < 1)
        {
            throw new IllegalArgumentException(
                "Hand number must be at least 1.");
        }
        
        if (potSize <= 0)
        {
            throw new IllegalArgumentException(
                "Pot size must be greater than zero.");
        }
        if (tiedHand == null)
        {
            throw new IllegalArgumentException(
                "A tie happens at the showdown, so it must have a hand.");
        }

        this.handNumber = handNumber;
        this.winner = null;
        this.winningHand = tiedHand;
        this.potSize = potSize;
        this.tie = true;
    }


    /**
     * Gets which hand this was.
     *
     * @return the hand number, starting at 1
     */
    public int getHandNumber()
    {
        return handNumber;
    }


    /**
     * Gets who won the pot.
     *
     * @return the winner's name, or null if the hand was a tie
     */
    public String getWinner()
    {
        return winner;
    }


    /**
     * Gets the winning hand's category, or the tied hand's category.
     *
     * @return the category, or null if the hand ended in a fold
     */
    public HandRank getWinningHand()
    {
        return winningHand;
    }


    /**
     * Gets the size of the pot.
     *
     * @return the number of chips in the pot
     */
    public int getPotSize()
    {
        return potSize;
    }


    /**
     * Checks whether the hand was a tie
     *
     * @return true for a tie
     */
    public boolean isTie()
    {
        return tie;
    }


    /**
     * Checks whether the hand ended because someone folded.
     *
     * @return true if nobody showed their cards
     */
    public boolean endedInFold()
    {
        return winningHand == null;
    }


    /**
     * Describes the result in one line
     *
     * @return a one-line description
     */
    @Override
    public String toString()
    {
        String start = "Hand " + handNumber + ": ";
        if (tie)
        {
            return start + "Tie with " + winningHand + ", pot of " + potSize
                + " split";
        }
        if (endedInFold())
        {
            return start + winner + " won " + potSize + " by fold";
        }
        return start + winner + " won " + potSize + " with " + winningHand;
    }
}

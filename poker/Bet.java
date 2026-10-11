package poker;
public class Bet
{
    /**
     * The possible betting actions.
     */
    public enum Action
    {
        BET, CALL, RAISE
    }

    /**
     * The possible betting rounds.
     */
    public enum Street
    {
        PREFLOP, FLOP, TURN, RIVER
    }

    private final String bettor;
    private final Action action;
    private final int amount;
    private final Street street;

    /**
     * Creates a new Bet.
     *
     * @param bettor the name of the player making the wager
     * @param action the betting action
     * @param amount the number of chips wagered
     * @param street the betting round
     * @throws IllegalArgumentException if an argument is invalid
     */
    public Bet(String bettor, Action action, int amount, Street street)
    {
        if (bettor == null || action == null || street == null)
        {
            throw new IllegalArgumentException(
                    "Arguments cannot be null.");
        }

        if (amount <= 0)
        {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero.");
        }

        this.bettor = bettor;
        this.action = action;
        this.amount = amount;
        this.street = street;
    }

    /**
     * Gets the name of the player who made the wager.
     *
     * @return the bettor's name
     */
    public String getBettor()
    {
        return bettor;
    }

    /**
     * Gets the betting action.
     *
     * @return the action performed
     */
    public Action getAction()
    {
        return action;
    }

    /**
     * Gets the number of chips wagered.
     *
     * @return the wager amount
     */
    public int getAmount()
    {
        return amount;
    }

    /**
     * Gets the betting round.
     *
     * @return the street on which the bet was made
     */
    public Street getStreet()
    {
        return street;
    }
}

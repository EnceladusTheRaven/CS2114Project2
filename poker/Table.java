package poker;
public class Table
{
    //~ Fields ................................................................

    private Deck deck;
    private Player player;
    private Opponent opponent;
    private LinkedChain<Card> communityCards;
    private LinkedChain<Bet> bets;
    private Street street;
    private int smallBlind;
    private int bigBlind;
    private boolean handInProgress;
    private boolean dealerIsPlayer;

    //~ Constructors ..........................................................

    /**
     * Creates a poker table for a player and an opponent.
     *
     * @param player the human player
     * @param opponent the computer opponent
     * @param smallBlind the small blind amount
     */
    public Table(Player player, Opponent opponent, int smallBlind)
    {
        this.deck = new Deck();
        this.player = player;
        this.opponent = opponent;
        this.communityCards = new LinkedChain<>();
        this.bets = new LinkedChain<>();
        this.smallBlind = smallBlind;
        this.bigBlind = smallBlind * 2;
        this.handInProgress = false;
        this.dealerIsPlayer = true;
        this.street = Street.PREFLOP;
    }

    //~ Public Methods ........................................................

    /**
     * Returns the deck.
     *
     * @return the deck of cards
     */
    public Deck getDeck()
    {
        return deck;
    }

    /**
     * Returns the human player.
     *
     * @return the player
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * Returns the opponent.
     *
     * @return the opponent
     */
    public Opponent getOpponent()
    {
        return opponent;
    }

    /**
     * Returns the community cards.
     *
     * @return the community cards
     */
    public LinkedChain<Card> getCommunityCards()
    {
        return communityCards;
    }

    /**
     * Returns the betting history for the current hand.
     *
     * @return the bets made during the hand
     */
    public LinkedChain<Bet> getBets()
    {
        return bets;
    }

    /**
     * Returns the current betting round.
     *
     * @return the current street
     */
    public Street getStreet()
    {
        return street;
    }

    /**
     * Returns the small blind amount.
     *
     * @return the small blind
     */
    public int getSmallBlind()
    {
        return smallBlind;
    }

    /**
     * Returns the big blind amount.
     *
     * @return the big blind
     */
    public int getBigBlind()
    {
        return bigBlind;
    }

    /**
     * Checks whether a hand is in progress.
     *
     * @return true if a hand is in progress
     */
    public boolean isHandInProgress()
    {
        return handInProgress;
    }

    /**
     * Checks whether the player is the dealer.
     *
     * @return true if the player is the dealer
     */
    public boolean isDealerPlayer()
    {
        return dealerIsPlayer;
    }

    /**
     * Starts a new hand and resets the table state.
     */
    public void startHand()
    {
        if (handInProgress)
        {
            return;
        }

        deck.reset();
        communityCards.clear();
        bets.clear();

        street = Street.PREFLOP;
        handInProgress = true;

        dealerIsPlayer = !dealerIsPlayer;
    }

    /**
     * Ends the current hand.
     */
    public void endHand()
    {
        if (!handInProgress)
        {
            return;
        }

        handInProgress = false;
        dealerIsPlayer = !dealerIsPlayer;
    }

    /**
     * Records a bet made during the current hand.
     *
     * @param bet the bet to record
     */
    public void addBet(Bet bet)
    {
        if (handInProgress && bet != null)
        {
            bets.add(bet);
        }
    }

    /**
     * Adds a community card to the table.
     *
     * @param card the community card to add
     */
    public void addCommunityCard(Card card)
    {
        if (handInProgress && card != null
            && communityCards.size() < 5)
        {
            communityCards.add(card);
        }
    }

    /**
     * Changes the current betting round.
     *
     * @param newStreet the new betting round
     */
    public void setStreet(Street newStreet)
    {
        if (handInProgress && newStreet != null)
        {
            street = newStreet;
        }
    }
}

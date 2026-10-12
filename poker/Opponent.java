package poker;
import java.util.Random;
public class Opponent
{
    //~ Fields ................................................................
    private static final int STRONG = 2;
    private static final int MEDIUM = 1;
    private static final int WEAK = 0;
    
    private String name;
    private int chips;
    private int currentBet;
    private LinkedChain<Card> holeCards;
    private boolean folded;
    
    private LinkedChain<Card> communityCards;
    private Random random;
    
    //~ Constructors ..........................................................
    /**
     * creates an opponent
     * @param name the opponents name
     * @param chips starting chips
     */
    public Opponent(String name, int chips)
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
        this.communityCards = null;
        this.random = new Random();
    }
    //~Public  Methods ........................................................
    /**
     * gets the name
     * @return String the name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * gets the chips
     * @return int returns the chips
     */
    public int getChips()
    {
        return chips;
    }
    
    /**
     * get the current bet
     * @return int the number of the current bet
     */
    public int getCurrentBet()
    {
        return currentBet;
    }
    
    /**
     * gets the hole cards
     * @return returns the LinkedChain of holeCards
     */
    public LinkedChain<Card> getHoleCards()
    {
        return holeCards;
    }
    
    /**
     * returns if has folded or not
     * @return boolean if has folded or not
     */
    public boolean hasFolded()
    {
        return folded;
    }
    
    /**
     * receives the card
     */
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
    
    /**
     * places the bet
     */
    public void placeBet(int amount)
    {
        if (amount < 0 || amount > chips)
        {
            throw new IllegalArgumentException("Invalid bet " + amount + " with " + chips + " chips");
        }
        chips -= amount;
        currentBet += amount;
    }
    
    /**
     * determines the amount of win chips
     */
    public void winChips(int amount)
    {
        if (amount < 0)
        {
            throw new IllegalArgumentException("cannot win negative chips");
        }
        chips += amount;
    }
    
    /**
     * folds
     */
    public void fold()
    {
        folded = true;
    }
    
    /**
     * resets the current bet
     */
    public void resetCurrentBet()
    {
        currentBet = 0;
    }
    
    /**
     * full reset
     */
    public void reset()
    {
        holeCards.clear();
        currentBet = 0;
        folded = false;
    }
    
    /**
     * sets up the community cards
     */
    public void setCommunityCards(LinkedChain<Card> community)
    {
        this.communityCards = community;
    }
    
    /**
     * Determines what the opponent will do based on the current cards 
     * on the table
     * @return Action returns the action to be done
     */
    public Action chooseAction(int toCall, int minRaise)
    {
        int strength = evaluateStrength();
        if (toCall <= 0)
        {
            boolean canBet = chips >= minRaise;
            if (canBet && strength == STRONG)
            {
                return Action.BET;
            }
            if (canBet && strength == MEDIUM && random.nextInt(100) < 30)
            {
                return Action.BET;
            }
            if (canBet && strength == WEAK && random.nextInt(100) < 10)
            {
                return Action.BET;
            }
            return Action.CHECK;
        }
        
        boolean canRaise = chips > toCall + minRaise;
        if (strength == STRONG)
        {
            if (canRaise && random.nextInt(100) < 60)
            {
                return Action.RAISE;
            }
            return Action.CALL;
        }
        if (strength == MEDIUM)
        {
            return Action.CALL;
        }
        
        if (toCall * 10 <= chips)
        {
            return Action.CALL;
        }
        return Action.FOLD;
    }
    
    /**
     * chooses the amount to raise based on how strong of a hand
     * @return int the amount raised
     */
    public int chooseRaiseAmount(int minRaise)
    {
        int amount = minRaise;
        if (evaluateStrength() == STRONG && random.nextInt(100) < 40)
        {
            amount = minRaise * 2;
        }
        return Math.min(amount, chips);
    }
    
    /**
     * determines the strength of the hand
     * @return int returns the end strength
     */
    private int evaluateStrength()
    {
        if (communityCards != null && communityCards.size() >= 3)
        {
            Hand hand = new Hand(holeCards, communityCards);
            int category = hand.getRank().ordinal();
            if (category >= 3)
            {
                return STRONG;
            }
            if (category >= 1)
            {
                return MEDIUM;
            }
            return WEAK;
        }
        return preflopStrength();
    }
    
    /**
     * determines the strength of the hand before the flop
     * @return int returns the strength pre flop
     */
    private int preflopStrength()
    {
        if (holeCards.size() < 2)
        {
            return WEAK;
        }
        Card first = holeCards.get(0);
        Card second = holeCards.get(1);
        int a = highValue(first.getValue());
        int b = highValue(second.getValue());
        
        if (a == b)
        {
            return a >= 10 ? STRONG : MEDIUM;
        }
        if (a >= 12 && b >= 12)
        {
            return STRONG;
        }
        boolean suited = first.getSuit().equals(second.getSuit());
        if (Math.max(a, b) >= 11 || (suited && Math.abs(a - b) <= 2))
        {
            return MEDIUM;
        }
        return WEAK;
    }
    
    /**
     * determines high value for ace
     * @return int returns the best ace value
     */
    private int highValue(int value)
    {
        return value == 1 ? 14 : value;
    }

}

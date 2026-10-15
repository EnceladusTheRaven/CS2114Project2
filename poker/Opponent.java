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
    
    /**
     * handles the personality
     */
    public enum Personality
    {
        /** the smart decisions */
        SMART,
        /** bluffs often */
        BLUFF,
        /** makes bad decisions */
        DUMB
    }
    private Personality personality;
    //~ Constructors ..........................................................
    /**
     * creates opponent default to smart
     * if no personality
     * @param name the name
     * @param chips the amount of starting chips
     */
    public Opponent(String name, int chips)
    {
        this(name, chips, Personality.SMART);
    }
    /**
     * creates an opponent
     * @param name the opponents name
     * @param chips starting chips
     * @param personality personality of opponent
     */
    public Opponent(String name, int chips, Personality personality)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (chips < 0)
        {
            throw new IllegalArgumentException("Chips cannot be negative");
        }
        if (personality == null)
            throw new IllegalArgumentException("Personality cannot be null");
        this.name = name;
        this.chips = chips;
        this.currentBet = 0;
        this.holeCards = new LinkedChain<>();
        this.folded = false;
        this.communityCards = null;
        this.personality = personality;
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
     * gets the personality
     * @return personality returns the personality
     */
    public Personality getPersonality()
    {
        return personality;
    }
    
    /**
     * sets the personality
     * @param personality new personality
     */
    public void setPersonality(Personality personality)
    {
        if (personality == null)
        {
            throw new IllegalArgumentException("Personality cannot be null");
        }
        this.personality = personality;
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
     * @param c card that is received
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
     * @param amount amount that is bet
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
     * @param amount amount that is won
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
     * @param community the linked chain of community cards
     */
    public void setCommunityCards(LinkedChain<Card> community)
    {
        this.communityCards = community;
    }
    
    /**
     * chooses the action based on personality
     * @return Action the action that is returned
     * @param toCall the amount to call
     * @param minRaise the minimum amount raised
     */
    public Action chooseAction(int toCall, int minRaise)
    {
        switch (personality)
        {
            case BLUFF:
                return bluffAction(toCall, minRaise);
            case DUMB:
                return dumbAction(toCall, minRaise);
            default:
                return smartAction(toCall, minRaise);
        }
    }
    
    /**
     * Determines what the opponent will do based on the current cards 
     * on the table (SMART)
     * @return Action returns the action to be done
     * @param toCall the amount to call
     * @param minRaise the minimum amount to raise
     */
    public Action smartAction(int toCall, int minRaise)
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
     * Does a whole lot of bluffing for the action (BLUFF)
     * @return Action returns a bluffing action
     * @param toCall the amount to call
     * @param minRaise the minimum amount to raise
     */
    private Action bluffAction(int toCall, int minRaise)
    {
        if (toCall <= 0)
        {
            if (chips >= minRaise)
            {
                return Action.BET;
            }
            return Action.CHECK;
        }
        if (chips > toCall + minRaise)
        {
            return Action.RAISE;
        }
        return Action.CALL;
    }
    
    /**
     * returns a action that is a bad decision (DUMB)
     * @return Action returns a dumb action
     */
    private Action dumbAction(int toCall, int minRaise)
    {
        int strength = evaluateStrength();
        if (toCall <= 0)
        {
            boolean canBet = chips >= minRaise;
            if (canBet && strength == WEAK && random.nextInt(100) < 20)
            {
                return Action.BET;
            }
            return Action.CHECK;
        }
        if (strength == STRONG)
        {
            if (random.nextInt(100) < 60)
            {
                return Action.FOLD;
            }
            return Action.CALL;
        }
        if (strength == MEDIUM)
        {
            if (random.nextInt(100) < 40)
            {
                return Action.FOLD;
            }
            return Action.CALL;
        }
        if (random.nextInt(100) < 15)
        {
            return Action.FOLD;
        }
        return Action.CALL;
    }
    
    /**
     * chooses the amount to raise based on how strong of a hand
     * @return int the amount raised
     * @param minRaise the minimum amount to raise
     */
    public int chooseRaiseAmount(int minRaise)
    {
        int amount = minRaise;
        switch (personality)
        {
            case BLUFF:
                amount = minRaise * (random.nextInt(100) < 50 ? 3 : 2);
                break;
            case DUMB:
                amount = minRaise * (1 + random.nextInt(3));
                break;
            default:
                if (evaluateStrength() == STRONG && random.nextInt(100) < 40)
                {
                    amount = minRaise * 2;
                } 
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

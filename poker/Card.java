package poker;
// -------------------------------------------------------------------------
/**
 *  Write a one-sentence summary of your class here.
 *  Follow it with additional details about its purpose, what abstraction
 *  it represents, and how to use it.
 * 
 *  @author Robert Laing (rlaing4308)
 *  @version Sep 28, 2026
 */
public class Card
{
    //~ Fields ................................................................

    private final int value; // The card's value 1-13
    private final String suit; // The card's suit
    
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Create a new Card object.
     * @param value
     * @param suit
     */
    public Card(int value, String suit) {
        this.value = value;
        this.suit = suit;
    }
   
    //~Public  Methods ........................................................
    
    // ----------------------------------------------------------
    /**
     * Gets and returns the value of the Card.
     * @return The card's value
     */
    public int getValue() {
        return value;
    }
    
    // ----------------------------------------------------------
    /**
     * Gets the suit of the card
     * @return The card's suit
     */
    public String getSuit() {
        return suit;
    }
    
    // ----------------------------------------------------------
    /**
     * Determines if a card is equal
     * @return Boolean indicating equality
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == this.getClass()) {
            Card other = (Card) obj;
            return value == other.getValue() && suit.equals(other.getSuit());
        }
        return false;
    }
}

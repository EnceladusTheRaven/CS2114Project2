package poker;
import java.util.Random;
public class Deck
{
  //~ Fields ................................................................
    private LinkedChain<Card> cards;
    private Random random;
    private static final String[] SUITS = {"hearts", "diamonds", "clubs", "spades"};
    private static final int RANKS_PER_SUIT = 13;
    
    //~ Constructors ..........................................................
    /***
     * creates a deck
     */
    public Deck()
    {
        cards = new LinkedChain<>();
        random = new Random();
    }
    //~Public  Methods ........................................................
    /**
     * shuffles the deck
     */
    public void shuffle()
    {
        int n = cards.size();
        Card[] temp = new Card[n];
        for (int i = 0; i < n; i++)
        {
            temp[i] = cards.removeFirst();
        }
        for (int i = n - 1; i > 0; i--)
        {
            int j = random.nextInt(i + 1);
            Card swap = temp[i];
            temp[i] = temp[j];
            temp[j] = swap;
        }
        for (int i = 0; i < n; i++)
        {
            cards.add(temp[i]);
        }
    }
    
    /**
     * deals a card, returns null if no card left
     */
    public Card deal()
    {
        if (cards.isEmpty())
        {
            return null;
        }
        return cards.removeFirst();
    }
    
    /**
     * returns how many cards are left
     */
    public int cardsLeft()
    {
        return cards.size();
    }
    
    public void reset()
    {
        cards.clear();
        buildDeck();
        shuffle();
    }
    
    private void buildDeck()
    {
        for (String suit : SUITS)
        {
            for (int rank = 1; rank <= RANKS_PER_SUIT; rank++)
            {
                cards.add(new Card(rank, suit));
            }
        }
    }
}

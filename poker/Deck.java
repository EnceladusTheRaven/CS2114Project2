package poker;
public class Deck
{
  //~ Fields ................................................................
    public LinkedChain<Card> cards;
    
    //~ Constructors ..........................................................
    public Deck()
    {
        
    }
    //~Public  Methods ........................................................
    public void shuffle()
    {
        for (int i = 0; i < cards.size() - 1; i++)
        {
            int randomCardIndex = Math.floor(Math.random(0, cards.size()));
            Card currentCard = cards.peek();
            //need a retrieving a value at a index method for LinkedChain
        }
    }
    public Card deal()
    {
        return cards.remove();
    }
    public int cardsLeft()
    {
        
    }
    public void reset()
    {
        
    }
}

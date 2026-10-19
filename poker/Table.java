package poker;

import java.util.Random;
import java.util.Scanner;

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
    private int handsPlayed;
    private LinkedChain<RoundResult> history;

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
        this.handsPlayed = 0;
        this.history = new LinkedChain<>();
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
     * Returns the results of every finished hand, oldest first.
     *
     * @return the hand history
     */
    public LinkedChain<RoundResult> getHistory()
    {
        return history;
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
     * Starts a new hand: reshuffles the deck and clears the board, the bets
     * and both sides' cards.
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
        player.reset();
        opponent.reset();
        opponent.setCommunityCards(communityCards);

        street = Street.PREFLOP;
        handInProgress = true;
    }


    /**
     * Ends the current hand, passes the dealer button to the other side, and
     * counts the hand.
     */
    public void endHand()
    {
        if (!handInProgress)
        {
            return;
        }

        handInProgress = false;
        dealerIsPlayer = !dealerIsPlayer;
        handsPlayed++;
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

    /**
     * Adds up every bet made this hand.
     *
     * @return the number of chips in the pot
     */
    public int potTotal()
    {
        int total = 0;
        for (int i = 0; i < bets.size(); i++)
        {
            total += bets.get(i).getAmount();
        }
        return total;
    }

    /**
     * Runs the game: plays hands until someone cannot afford the big blind or
     * the player chooses to leave, then prints a summary.
     *
     * @param input the same Scanner the Player reads from, so the program
     *              has only one reader of System.in
     */
    public void play(Scanner input)
    {
        System.out.println("Welcome to Practice Poker! Blinds are "
            + smallBlind + "/" + bigBlind + ".");

        boolean keepPlaying = true;
        while (keepPlaying)
        {
            if (player.getChips() < bigBlind)
            {
                System.out.println("\nYou can't afford the big blind. "
                    + "Game over.");
                break;
            }
            if (opponent.getChips() < bigBlind)
            {
                System.out.println("\nThe opponent can't afford the big "
                    + "blind. You win the game!");
                break;
            }

            playHand();
            keepPlaying = askPlayAgain(input);
        }

        printSummary();
    }

    //~ Private Methods .......................................................

    /**
     * Plays one full hand: blinds, dealing, up to four betting rounds, and
     * either a fold or a showdown.
     */
    private void playHand()
    {
        startHand();
        System.out.println("\n========== Hand " + (handsPlayed + 1)
            + " ==========");
        System.out.println((dealerIsPlayer ? "You are" : "The opponent is")
            + " the dealer and posts the small blind.");

        for (int i = 0; i < 2; i++)
        {
            player.receiveCard(deck.deal());
            opponent.receiveCard(deck.deal());
        }

        postBlinds();

        // Pre-flop the dealer acts first; after the flop the other side does.
        boolean someoneFolded = bettingRound(dealerIsPlayer);

        Street[] nextStreets = { Street.FLOP, Street.TURN, Street.RIVER };
        int[] cardsToDeal = { 3, 1, 1 };
        for (int s = 0; s < nextStreets.length && !someoneFolded; s++)
        {
            setStreet(nextStreets[s]);
            for (int c = 0; c < cardsToDeal[s]; c++)
            {
                addCommunityCard(deck.deal());
            }
            System.out.println("\n--- " + street + " --- Board: "
                + communityCards);

            player.resetCurrentBet();
            opponent.resetCurrentBet();
            someoneFolded = bettingRound(!dealerIsPlayer);
        }

        if (someoneFolded)
        {
            awardFoldedPot();
        }
        else
        {
            showdown();
        }

        endHand();
    }

    /**
     * Posts the small blind (dealer) and the big blind (the other side).
     */
    private void postBlinds()
    {
        putIn(dealerIsPlayer, Action.BET, smallBlind);
        putIn(!dealerIsPlayer, Action.BET, bigBlind);
        System.out.println("Blinds posted. Pot: " + potTotal());
    }

    /**
     * Runs one betting round. The two sides take turns until both have acted
     * and have put in the same amount this round, or until someone folds.
     *
     * @param playerFirst true if the player acts first this round
     * @return true if someone folded
     */
    private boolean bettingRound(boolean playerFirst)
    {
        boolean playerActed = false;
        boolean opponentActed = false;
        boolean playerTurn = playerFirst;
        int lastRaise = bigBlind;

        while (!(playerActed && opponentActed
            && player.getCurrentBet() == opponent.getCurrentBet()))
        {
            int myBet = playerTurn ? player.getCurrentBet()
                : opponent.getCurrentBet();
            int theirBet = playerTurn ? opponent.getCurrentBet()
                : player.getCurrentBet();
            int myChips = playerTurn ? player.getChips() : opponent.getChips();
            int theirChips = playerTurn ? opponent.getChips()
                : player.getChips();
            int toCall = theirBet - myBet;

            // With no chips left there is nothing to decide.
            if (myChips == 0)
            {
                if (toCall == 0)
                {
                    break;
                }
                // Can't happen: raises are capped so a call always fits.
                throw new IllegalStateException("Owes chips with none left");
            }

            // Cap raises at what the other side can still call.
            int maxRaise = Math.min(myChips - toCall, theirChips);
            int minRaise = Math.min(lastRaise, maxRaise);

            if (playerTurn)
            {
                printStatus(toCall);
            }
            Action action = askForLegalAction(playerTurn, toCall, minRaise,
                maxRaise);

            if (action == Action.FOLD)
            {
                if (playerTurn)
                {
                    player.fold();
                }
                else
                {
                    opponent.fold();
                }
                System.out.println(verb(playerTurn, "fold", "folds") + ".");
                return true;
            }
            else if (action == Action.CHECK)
            {
                System.out.println(verb(playerTurn, "check", "checks") + ".");
            }
            else if (action == Action.CALL)
            {
                putIn(playerTurn, Action.CALL, toCall);
                System.out.println(verb(playerTurn, "call", "calls") + " "
                    + toCall + ".");
            }
            else
            {
                int raise = playerTurn
                    ? player.chooseRaiseAmount(minRaise)
                    : opponent.chooseRaiseAmount(minRaise);
                if (raise > maxRaise)
                {
                    if (playerTurn)
                    {
                        System.out.println("Raise capped at " + maxRaise
                            + ", the most the opponent can call.");
                    }
                    raise = maxRaise;
                }
                raise = Math.max(raise, minRaise);
                putIn(playerTurn, action, toCall + raise);
                lastRaise = raise;
                System.out.println((action == Action.BET
                    ? verb(playerTurn, "bet", "bets")
                    : verb(playerTurn, "raise by", "raises by")) + " "
                    + raise + ".");

                // A bet or raise means the other side must act again.
                if (playerTurn)
                {
                    opponentActed = false;
                }
                else
                {
                    playerActed = false;
                }
            }

            if (playerTurn)
            {
                playerActed = true;
            }
            else
            {
                opponentActed = true;
            }
            playerTurn = !playerTurn;
        }
        return false;
    }

    /**
     * Asks the player or opponent for an action that is legal right now.
     * The player is asked again until they pick a legal one. If the computer
     * picks an illegal one, it checks or calls instead, so it can never get
     * stuck repeating the same choice.
     *
     * @param playerTurn true if it is the player's turn
     * @param toCall     chips needed to match the other side
     * @param minRaise   the smallest raise allowed
     * @param maxRaise   the largest raise allowed (0 means no raise is possible)
     * @return a legal action
     */
    private Action askForLegalAction(boolean playerTurn, int toCall,
        int minRaise, int maxRaise)
    {
        while (true)
        {
            Action action = playerTurn
                ? player.chooseAction(toCall, minRaise)
                : opponent.chooseAction(toCall, minRaise);

            if (isLegal(action, toCall, maxRaise))
            {
                return action;
            }
            if (!playerTurn)
            {
                return toCall == 0 ? Action.CHECK : Action.CALL;
            }
            if ((action == Action.BET || action == Action.RAISE)
                && maxRaise == 0)
            {
                System.out.println("You can't raise: the opponent has no "
                    + "chips left to call it.");
            }
            else if (toCall == 0)
            {
                System.out.println("No bet to match. Choose check, bet, "
                    + "or fold.");
            }
            else
            {
                System.out.println("There's a bet of " + toCall
                    + ". Choose call, raise, or fold.");
            }
        }
    }

    /**
     * Checks whether an action is allowed in the current situation.
     *
     * @param action   the action chosen
     * @param toCall   chips needed to match the other side
     * @param maxRaise the largest raise allowed
     * @return true if the action is legal
     */
    private boolean isLegal(Action action, int toCall, int maxRaise)
    {
        if (action == Action.FOLD)
        {
            return true;
        }
        if (toCall == 0)
        {
            return action == Action.CHECK
                || (action == Action.BET && maxRaise > 0);
        }
        return action == Action.CALL
            || (action == Action.RAISE && maxRaise > 0);
    }

    /**
     * Moves chips from the player or opponent into the pot and records the
     * bet.
     *
     * @param forPlayer true to take the chips from the player
     * @param action    the action that put the chips in
     * @param amount    how many chips go in
     */
    private void putIn(boolean forPlayer, Action action, int amount)
    {
        if (amount <= 0)
        {
            return;
        }
        if (forPlayer)
        {
            player.placeBet(amount);
            addBet(new Bet(player.getName(), action, amount, street));
        }
        else
        {
            opponent.placeBet(amount);
            addBet(new Bet(opponent.getName(), action, amount, street));
        }
    }

    /**
     * Gives the whole pot to whoever did not fold, and records the result.
     */
    private void awardFoldedPot()
    {
        int pot = potTotal();
        String winner;
        if (player.hasFolded())
        {
            opponent.winChips(pot);
            winner = opponent.getName();
            System.out.println(opponent.getName() + " wins " + pot + ".");
        }
        else
        {
            player.winChips(pot);
            winner = player.getName();
            System.out.println("You win " + pot + ".");
        }
        history.add(new RoundResult(handsPlayed + 1, winner, null, pot));
    }

    /**
     * Reveals both hands, compares them, pays the pot to the winner (or
     * splits it on a tie), and records the result.
     */
    private void showdown()
    {
        Hand playerHand = new Hand(player.getHoleCards(), communityCards);
        Hand opponentHand = new Hand(opponent.getHoleCards(), communityCards);
        int pot = potTotal();

        System.out.println("\n--- SHOWDOWN --- Board: " + communityCards);
        System.out.println("You:      " + player.getHoleCards() + "  "
            + playerHand);
        System.out.println(opponent.getName() + ": "
            + opponent.getHoleCards() + "  " + opponentHand);

        int result = playerHand.compareTo(opponentHand);
        String winner;
        HandRank winningHand;
        if (result > 0)
        {
            player.winChips(pot);
            winner = player.getName();
            winningHand = playerHand.getRank();
            System.out.println("You win " + pot + "!");
        }
        else if (result < 0)
        {
            opponent.winChips(pot);
            winner = opponent.getName();
            winningHand = opponentHand.getRank();
            System.out.println(opponent.getName() + " wins " + pot + ".");
        }
        else
        {
            // Split the pot; an odd chip goes to the player.
            int half = pot / 2;
            player.winChips(pot - half);
            opponent.winChips(half);
            System.out.println("Tie! The pot is split: " + (pot - half)
                + " to you, " + half + " to the opponent.");
            history.add(new RoundResult(handsPlayed + 1, playerHand.getRank(),
                pot));
            return;
        }
        history.add(new RoundResult(handsPlayed + 1, winner, winningHand,
            pot));
    }

    /**
     * Builds the start of an action message with the right verb form, such as
     * "You call" or "Opponent calls".
     *
     * @param playerTurn true if the player is acting
     * @param youForm    the verb after "You", such as "call"
     * @param otherForm  the verb after the opponent's name, such as "calls"
     * @return the subject and verb together
     */
    private String verb(boolean playerTurn, String youForm, String otherForm)
    {
        return playerTurn ? "You " + youForm
            : opponent.getName() + " " + otherForm;
    }

    /**
     * Shows the player their cards, what hand they currently have, the board,
     * the pot and the chip counts before they act.
     *
     * @param toCall chips the player needs to call
     */
    private void printStatus(int toCall)
    {
        System.out.println("\nYour cards: " + player.getHoleCards()
            + "   Board: " + communityCards);
        System.out.println("You have: " + describeCurrentHand());
        System.out.println("Pot: " + potTotal() + "   Your chips: "
            + player.getChips() + "   Opponent chips: "
            + opponent.getChips() + "   To call: " + toCall);
    }

    /**
     * Describes the player's best hand so far. Before the flop there are only
     * two cards, so the only possible hands are a pair or a high card.
     *
     * @return a readable hand name, such as "Two pair"
     */
    private String describeCurrentHand()
    {
        if (communityCards.size() < 3)
        {
            LinkedChain<Card> hole = player.getHoleCards();
            if (hole.get(0).getValue() == hole.get(1).getValue())
            {
                return "One pair";
            }
            return "High card";
        }
        Hand current = new Hand(player.getHoleCards(), communityCards);
        String name = current.getRank().name().replace('_', ' ').toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * Asks whether to play another hand. Accepts "play", "leave", or
     * "history" (which shows past hands and asks again). Poker commands are
     * rejected because no hand is in progress.
     *
     * @param input the Scanner to read from
     * @return true to play another hand, false to leave
     */
    private boolean askPlayAgain(Scanner input)
    {
        while (true)
        {
            System.out.println("\nType play, history, or leave:");
            if (!input.hasNextLine())
            {
                return false;
            }
            String answer = input.nextLine().trim().toLowerCase();
            if (answer.equals("play"))
            {
                return true;
            }
            if (answer.equals("leave"))
            {
                return false;
            }
            if (answer.equals("history"))
            {
                printHistory();
            }
            else
            {
                System.out.println(
                    "No hand in progress. Type play, history, or leave.");
            }
        }
    }

    /**
     * Prints hands played, final chips, net winnings or losses, the
     * opponent's personality, and the history of every hand.
     */
    private void printSummary()
    {
        int net = player.getChips() - player.getStartingChips();
        System.out.println("\n========== Summary ==========");
        System.out.println("Hands played: " + handsPlayed);
        System.out.println("Final chips:  " + player.getChips());
        System.out.println("Net result:   " + (net >= 0 ? "+" : "") + net);
        System.out.println("Opponent was: " + opponent.getPersonality());
        printHistory();
    }

    /**
     * Prints one line for every finished hand, oldest first.
     */
    private void printHistory()
    {
        System.out.println("\n---------- Hand history ----------");
        if (history.isEmpty())
        {
            System.out.println("No hands played yet.");
        }
        for (int i = 0; i < history.size(); i++)
        {
            System.out.println(history.get(i));
        }
    }

    /**
     * Starts a game of Practice Poker in the console. The player first picks
     * the opponent's personality, or "random" to keep it a secret until the
     * end of the game.
     *
     * @param args not used
     */
    public static void main(String[] args)
    {
        // One Scanner for the whole program, shared with the Player.
        Scanner input = new Scanner(System.in);

        Player player = new Player("You", 500, input);
        Opponent.Personality personality = askPersonality(input);
        Opponent opponent = new Opponent("Opponent", 500, personality);
        Table table = new Table(player, opponent, 5);

        table.play(input);
        input.close();
    }

    /**
     * Asks which personality the opponent should have. Accepts smart, bluff,
     * dumb or random (any capitalization) and asks again for anything else.
     * If the input runs out, the opponent is SMART.
     *
     * @param input the Scanner to read from
     * @return the chosen personality
     */
    private static Opponent.Personality askPersonality(Scanner input)
    {
        while (true)
        {
            System.out.println("Choose your opponent: smart, bluff, dumb, "
                + "or random:");
            if (!input.hasNextLine())
            {
                return Opponent.Personality.SMART;
            }
            String answer = input.nextLine().trim().toUpperCase();

            if (answer.equals("RANDOM"))
            {
                Opponent.Personality[] all = Opponent.Personality.values();
                System.out.println("Your opponent's personality is a secret. "
                    + "It will be revealed at the end.");
                return all[new Random().nextInt(all.length)];
            }

            try
            {
                Opponent.Personality chosen =
                    Opponent.Personality.valueOf(answer);
                System.out.println("You chose a " + chosen + " opponent.");
                return chosen;
            }
            catch (IllegalArgumentException e)
            {
                System.out.println("That isn't a personality.");
            }
        }
    }
}
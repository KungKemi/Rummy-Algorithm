package algorithms;

import hand.Card;
import hand.CardType;
import hand.Deck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * An abstract class representing a stack-building algorithm.
 */
public abstract class Algorithm {
    private final static int JOKER_NUM = 14;
    protected final static int MIN_BOOK_CARDS = 3;
    protected final static int MIN_RUN_CARDS = 4;
    private final static int NO_JOKERS_LEFT = 0;
    private final static int JOKERS_LEFT = 1;

    /**
     * Maps card numbers to number of times it appears in a list of cards.
     * If a card number does not appear in the list, then it is not present in the map.
     * If suit != null, then only cards of the given suit are used to make the map.
     * Ignores jokers.
     *
     * @param cards the list of cards
     * @param suit the suit of the cards to appear in map
     * @return a map between card numbers and instances of occurrence, based on suit
     */
    public static Map<Integer, List<Card>> getCardFreqMap(List<Card> cards, CardType suit) {
        Map<Integer, List<Card>> cardMap = new TreeMap<>();
        for (Card card : cards) {
            int cardNum = card.getCardNum();
            CardType cardSuit = card.getCardType();
            if (cardNum != JOKER_NUM && (suit == null || cardSuit == suit)) {
                // cards.Card is not a joker and of correct suit (if not null)
                if (!cardMap.containsKey(cardNum)) {
                    // Map does not contain card num. Add to map.
                    cardMap.put(cardNum, new ArrayList<>(List.of(card)));
                } else {
                    // Map contains card num. Add card to list.
                    cardMap.get(cardNum).add(card);
                }
            }
        }
        return cardMap;
    }

    /**
     * Returns all jokers present in a given list of cards.
     *
     * @param cards the list of cards
     * @return the jokers in the given hand of cards
     */
    public static List<Card> getJokers(List<Card> cards) {
        List<Card> jokers = new ArrayList<>();
        for (Card card : cards) {
            if (card.getCardNum() == JOKER_NUM) {
                jokers.add(card);
            }
        }
        return jokers;
    }

    /**
     * Determine score of list of cards.
     *
     * @return the score of the current hand of card
     */
    public static int getScore(List<Card> cards) {
        int score = 0;
        for (Card card : cards) {
            score += card.getCardScore();
        }
        return score;
    }

    /**
     * Sorts card stacks based on size and score.
     * Stacks containing more cards precede stacks containing fewer cards.
     * If both stacks have the same number of cards, then stacks with a higher score
     * precede stacks with a lower score.
     *
     * @param stacks the stacks of cards to sort
     */
    public static void sortStacks(List<List<Card>> stacks) {
        stacks.sort((List<Card> stackOne, List<Card> stackTwo) -> {
            int stackOneSize = stackOne.size();
            int stackTwoSize = stackTwo.size();
            if (stackOneSize == stackTwoSize) {
                // Higher score stacks precede lower score stacks
                return -Integer.compare(getScore(stackOne), getScore(stackTwo));
            } else {
                // Higher card stacks precede lower card stacks
                return -Integer.compare(stackOneSize, stackTwoSize);
            }
        });
    }

    /**
     * Determines some optimal bases from the given list of cards, based on the number of runs and/or books
     * required. Tries to minimise the number of cards required to complete the given runs and/or books.
     *
     * @param cards the list of cards
     * @param numBooks the number of required books
     * @param numRuns the number of required runs
     * @return a map between the Books / Runs and their optimal bases
     */
    public static Map<String, List<List<Card>>> getOptimalBases(List<Card> cards, int numBooks, int numRuns) {
        Map<String, List<List<Card>>> optimalBases = new TreeMap<>();
        Integer numMissingCards = null;
        List<List<Card>> bookBases;
        List<List<Card>> runBases;
        if (numRuns == 0) {
            // Just need books
            bookBases = BookAlgorithm.getBookBases(cards, numBooks);
            optimalBases.put("Books", bookBases);
        } else if (numBooks == 0) {
            // Just need runs
            runBases = RunAlgorithm.getRunBases(cards, numRuns);
            optimalBases.put("Runs", runBases);
        } else {
            // Need both books and runs
            // Check to see if forming runs, or books, first is more optimal
            for (int i = 0; i < 2; i++) {
                List<Card> localCards = new ArrayList<>(cards);
                int localNumMissingCards = 0;
                Map<String, List<List<Card>>> localBases = new TreeMap<>();
                if (numMissingCards == null) {
                    // Build books
                    bookBases = BookAlgorithm.getBookBases(localCards, numBooks);
                    localBases.put("Books", bookBases);
                    localNumMissingCards += BookAlgorithm.numCardsMissing(localCards, bookBases, numBooks, false);
                    // Remove book bases from local cards
                    removeBases(localCards, bookBases);
                    // Build runs
                    runBases = RunAlgorithm.getRunBases(localCards, numRuns);
                    localBases.put("Runs", runBases);
                    localNumMissingCards += RunAlgorithm.numCardsMissing(localCards, runBases, numRuns, false);
                } else {
                    // Build runs
                    runBases = RunAlgorithm.getRunBases(localCards, numRuns);
                    localBases.put("Runs", runBases);
                    localNumMissingCards += RunAlgorithm.numCardsMissing(localCards, runBases, numRuns, false);
                    // Remove run bases from local cards
                    removeBases(localCards, runBases);
                    // Build books
                    bookBases = BookAlgorithm.getBookBases(localCards, numBooks);
                    localBases.put("Books", bookBases);
                    localNumMissingCards += BookAlgorithm.numCardsMissing(localCards, bookBases, numBooks, false);
                }
                // Update optimal bases
                if (numMissingCards == null) {
                    // First iteration
                    numMissingCards = localNumMissingCards;
                    optimalBases = localBases;
                } else if (localNumMissingCards < numMissingCards) {
                    // Second iteration is more optimal
                    numMissingCards = localNumMissingCards;
                    optimalBases = localBases;
                }
            }
        }
        // Distribute jokers (if needed)
        addJokersToBases(optimalBases, getJokers(cards));
        return optimalBases;
    }

    /**
     * Distributes jokers to any incomplete bases.
     * Terminates once all jokers are distributed, or all bases have been considered.
     * @param bases the map between the Books / Runs and their bases
     * @param jokers the list of jokers
     */
    public static void addJokersToBases(Map<String, List<List<Card>>> bases, List<Card> jokers) {
        for (Map.Entry<String, List<List<Card>>> entry : bases.entrySet()) {
            // Consider basis sets for books and runs
            String stackType = entry.getKey();
            List<List<Card>> stackBases = entry.getValue();
            for (List<Card> basis : stackBases) {
                // Consider basis for given stack
                if (stackType.equals("Runs")) {
                    // Basis for a run
                    if (addJokersHelper(basis, jokers, MIN_RUN_CARDS) == NO_JOKERS_LEFT) {
                        return; // No jokers remain
                    }
                } else {
                    // Basis for a book
                    if (addJokersHelper(basis, jokers, MIN_BOOK_CARDS) == NO_JOKERS_LEFT) {
                        return; // No jokers remain
                    }
                }
            }
        }
    }

    /**
     * A helper function which redistributes jokers to the given stack until either no more jokers
     * are left, or the stack is filled.
     *
     * @param basis the basis to be filled
     * @param jokers the list of jokers
     * @param minStackCards the minimum number of cards required to complete the stack
     * @return 0 if no jokers remain. 1 otherwise.
     */
    public static int addJokersHelper(List<Card> basis, List<Card> jokers, int minStackCards) {
        while (basis.size() < minStackCards) {
            if (jokers.isEmpty()) {
                return NO_JOKERS_LEFT; // No jokers remaining
            }
            // Add jokers to stack until it has min needed cards (or we run out of jokers)
            Card joker = jokers.get(0);
            basis.add(joker);
            jokers.remove(joker); // Remove joker now that we've used it
        }
        return JOKERS_LEFT; // Some jokers still remain
    }

    /**
     * Determines how many cards are missing to form the required number of books and run, given
     * a map between Books / Runs and their bases.
     *
     * @param bases the map between the Books / Runs and their bases
     * @param numBooks the number of required books
     * @param numRuns the number of required runs
     * @return the number of cards required to complete the required books / runs
     */
    public static int numCardsMissing(Map<String, List<List<Card>>> bases, int numBooks, int numRuns) {
        int numCardsNeeded = MIN_BOOK_CARDS * numBooks + MIN_RUN_CARDS * numRuns;
        int numBasesCards = 0;
        for (Map.Entry<String, List<List<Card>>> entry : bases.entrySet()) {
            // Consider basis sets for books and runs
            List<List<Card>> stackBases = entry.getValue();
            for (List<Card> basis : stackBases) {
                // Consider basis for given stack
                numBasesCards += basis.size();
            }
        }
        return Math.max(0, numCardsNeeded - numBasesCards);
    }

    /**
     * Remove cards from the given bases from the given list of cards.
     * @param cards the list of cards
     * @param bases the list of bases
     */
    public static void removeBases(List<Card> cards, List<List<Card>> bases) {
        for (List<Card> basis : bases) {
            for (Card card : basis) {
                cards.remove(card);
            }
        }
    }
}

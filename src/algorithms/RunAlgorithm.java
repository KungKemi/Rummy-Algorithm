package algorithms;

import hand.Card;
import hand.CardType;
import hand.Deck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A class representing an algorithm to build runs from a list of cards
 */
public class RunAlgorithm extends Algorithm {
    private final static int TRAILING_ACE = 14;
    private final static int MAX_CARDS_TO_SIDE = 3;

    /**
     * Determines the best way to form runs from the given cards,
     * based on the number of runs required.
     * Returns the best bases in a list (from best to worst) until
     * the required bases are met (or no more bases can be formed).
     *
     * @param cards the list of cards
     * @param numRuns the number of required runs
     * @return a list containing the run bases
     */
    public static List<List<Card>> getRunBases(List<Card> cards, int numRuns) {
        List<List<Card>> stacks = new ArrayList<>();
        // Consider each suit to find possible bases
        for (CardType suit : Card.getSuits()) {
            Map<Integer, List<Card>> cardMap = getCardFreqMap(cards, suit);
            // Search for bases, starting with four adjacent cards and ending with only 1
            for (int numAdjCards = MIN_RUN_CARDS; numAdjCards > 0; numAdjCards--) {
                stacks.addAll(adjCardsSearch(cardMap, numAdjCards));
            }
        }
        // Sort stacks for optimality
        sortStacks(stacks);
        // Extract stacks from the front of the list until we have the required number of
        // runs (or until no more can be extracted)
        List<List<Card>> bases = new ArrayList<>();
        for (int i = 0; i < Math.min(stacks.size(), numRuns); i++) {
            bases.add(stacks.get(i));
        }
        return bases;
    }
    /**
     * Returns a list of possible run bases containing the given number of adjacent cards.
     * Each time a basis is found, those cards are removed from the map before any other
     * bases are searched for.
     *
     * @param cardMap a map between card numbers and instances of occurrence
     * @param numAdjCards the number of adjacent cards to search for
     * @return a list of possible bases containing the required number of adjacent cards
     */
    public static List<List<Card>> adjCardsSearch(Map<Integer, List<Card>> cardMap, int numAdjCards) {
        List<List<Card>> bases = new ArrayList<>();
        boolean basisFound;
        // Keep searching through map entries until we can no longer find any valid bases
        while (true) {
            basisFound = false;
            for (Map.Entry<Integer, List<Card>> entry : cardMap.entrySet()) {
                List<Card> value = entry.getValue();
                if (entry.getValue().size() != 0) {
                    // We can still use some cards
                    List<Card> adjCards = getAdjCardsList(cardMap, entry.getKey());
                    if (adjCards.size() == numAdjCards) {
                        // Found a valid basis
                        basisFound = true;
                        bases.add(adjCards);
                        removeCardsFromMap(cardMap, adjCards);
                    }
                }
            }
            if (!basisFound) {
                // Failed to find any more bases. Time to leave.
                return bases;
            }
        }
    }

    /**
     * Gets a list of adjacent cards from a given map to the given card.
     * A card is considered adjacent if it is within 3 positions of the given card.
     * All returned list contain the maximal number of adjacent cards.
     *
     * @param cardMap a map between card numbers and instances of occurrence
     * @param cardNum the card number around which to search for adjacent cards
     * @return a maximal list of adjacent cards
     */
    public static List<Card> getAdjCardsList(Map<Integer, List<Card>> cardMap, int cardNum) {
        List<Card> maxAdjCards = new ArrayList<>();
        List<Card> adjCards;
        // Let us check cards within three spaces either side of card to find the
        // number of adjacent cards.
        for (int i = -MAX_CARDS_TO_SIDE; i <= 0; i++) {
            adjCards = new ArrayList<>();
            // Check all cards which are 3 spaces to right of current card
            for (int j = 0; j < MIN_RUN_CARDS; j++) {
                int currentCardNum = cardNum + i + j;
                if (currentCardNum == TRAILING_ACE) {
                    // This is an ace to the right
                    currentCardNum = 1;
                }
                if (cardMap.containsKey(currentCardNum)) {
                    // cards.Card map contains (or contained) card
                    List<Card> cardsList = cardMap.get(currentCardNum);
                    if (cardsList.size() != 0) {
                        // There are still some of said card remaining. Add to list
                        adjCards.add(cardsList.get(0));
                    }
                }
            }
            if (maxAdjCards.size() < adjCards.size()) {
                // More adjacent cards found. Update global list.
                maxAdjCards = new ArrayList<>(adjCards);
            }
        }
        return maxAdjCards;
    }

    /**
     * Determine how many more cards are needed to form the required number of runs
     * given the current hand of cards and bases. Choice to include jokers or not.
     *
     * @param cards the hand of cards
     * @param bases the run bases
     * @param numRuns the number of runs required
     * @param jokers true if jokers should be included. False otherwise.
     * @return the number of cards missing to form desired runs
     */
    public static int numCardsMissing(List<Card> cards, List<List<Card>> bases, int numRuns, boolean jokers) {
        int numJokers = 0;
        if (jokers) {
            numJokers += getJokers(cards).size();
        }
        int numBasesCards = 0;
        // Count how many cards are used to form bases
        for (List<Card> basis : bases) {
            numBasesCards += basis.size();
        }
        int numCardsMissing = MIN_RUN_CARDS * numRuns - numBasesCards - numJokers;
        return Math.max(0, numCardsMissing);
    }

    /**
     * Removes the given list of cards from the map.
     *
     * @param cardMap a map between card numbers and said cards
     * @param cards the list of cards to remove
     */
    public static void removeCardsFromMap(Map<Integer, List<Card>> cardMap, List<Card> cards) {
        for (Card card : cards) {
            // Remove card from map
            cardMap.get(card.getCardNum()).remove(card);
        }
    }
}
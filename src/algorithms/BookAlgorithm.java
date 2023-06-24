package algorithms;

import hand.Card;
import hand.Deck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A class representing an algorithm to build books from a list of cards
 */
public class BookAlgorithm extends Algorithm {

    /**
     * Determines the best way to form books from the given cards,
     * based on the number of books required.
     * Returns the best bases in a list (from best to worst) until
     * the required bases are met (or no more bases can be formed).
     *
     * @param cards the list of cards
     * @param numBooks the number of required books
     * @return a list containing the book bases
     */
    public static List<List<Card>> getBookBases(List<Card> cards, int numBooks) {
        List<List<Card>> stacks = new ArrayList<>();
        Map<Integer, List<Card>> cardMap = getCardFreqMap(cards, null);
        for (Map.Entry<Integer, List<Card>> entry : cardMap.entrySet()) {
            List<Card> value = entry.getValue();
            // Determine how many books can be formed with given card number
            int availableBooks = value.size() / MIN_BOOK_CARDS;
            int i;
            for (i = 0; i < MIN_BOOK_CARDS * availableBooks; i += MIN_BOOK_CARDS) {
                // Remove complete books from map and add to stacks
                stacks.add(new ArrayList<>(value.subList(i, i + MIN_BOOK_CARDS)));
            }
            // Add remaining cards to stacks (may contain 0, 1 or 2 cards)
            stacks.add(new ArrayList<>(value.subList(i, value.size())));

        }
        // Sort stacks for optimality
        sortStacks(stacks);
        // Extract stacks from the front of the list until we have the required number of
        // books (or until no more can be extracted)
        List<List<Card>> bases = new ArrayList<>();
        for (int i = 0; i < Math.min(stacks.size(), numBooks); i++) {
            bases.add(stacks.get(i));
        }
        return bases;
    }

    /**
     * Determine how many more cards are needed to form the required number of books
     * given the current hand of cards and bases. Choice to include jokers or not.
     *
     * @param cards the hand of cards
     * @param bases the book bases
     * @param numBooks the number of books required
     * @param jokers true if jokers should be included. False otherwise.
     * @return the number of cards missing to form desired books
     */
    public static int numCardsMissing(List<Card> cards, List<List<Card>> bases, int numBooks, boolean jokers) {
        int numJokers = 0;
        if (jokers) {
            numJokers += getJokers(cards).size();
        }
        int numBasesCards = 0;
        // Count how many cards are used to form bases
        for (List<Card> basis : bases) {
            numBasesCards += basis.size();
        }
        int numCardsMissing = MIN_BOOK_CARDS * numBooks - numBasesCards - numJokers;
        return Math.max(0, numCardsMissing);
    }
}

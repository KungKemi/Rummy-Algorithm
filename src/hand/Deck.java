package hand;

import util.IllegalCardException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a deck of playing cards containing 54 cards: 13 from each suit,
 * and two JOKERS.
 */
public class Deck {

    /**
     * Creates and returns a non-shuffled deck of playing cards.
     * In the order SPADES, CLUBS, HEARTS, DIAMONDS, BLACK JOKER, RED JOKER, where
     * for each suit we have ACE, ONE, TWO, ..., KING.
     *
     * @return a non-shuffled deck of playing cards.
     */
    public static List<Card> notShuffled() {
        List<Card> deck = new ArrayList<>();
        try {
            for (CardType type : CardType.values()) {
                if (type == CardType.BLACK_JOKER) {
                    deck.add(new Card(14, type));
                } else if (type == CardType.RED_JOKER) {
                    deck.add(new Card(14, type));
                } else {
                    for (int cardNum = 1; cardNum < 14; cardNum++) {
                        deck.add(new Card(cardNum, type));
                    }
                }
            }
        } catch (IllegalCardException e) {
            // Do nothing.
        }
        return deck;
    }

    /**
     * Creates and returns a shuffled deck of playing cards.
     * @return a shuffled deck of playing cards
     */
    public static List<Card> shuffled() {
        List<Card> deck = notShuffled();
        Collections.shuffle(deck);
        return deck;
    }

    /**
     * Creates and returns a shuffled hand of playing cards, containing
     * the given number of cards, chosen randomly from the given number
     * of decks.
     *
     * @param numCards the number of cards in the deck
     * @param numDecks the number of decks used
     * @return a shuffled hand of playing cards
     */
    public static List<Card> shuffledHand(int numCards, int numDecks) {
        List<Card> cards = new ArrayList<>();
        // Get shuffled decks from which to source cards
        for (int i = 0; i < numDecks; i++) {
            cards.addAll(Deck.shuffled());
        }
        Collections.shuffle(cards);
        List<Card> hand = new ArrayList<>();
        // Add cards from top of shuffled decks to hand
        for (int i = 0; i < Math.min(numCards, cards.size()); i++) {
            hand.add(cards.get(i));
        }
        return hand;
    }
}

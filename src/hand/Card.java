package hand;

import util.BadEncodingException;
import util.Encodable;
import util.IllegalCardException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A class representing a standard playing card instance.
 */
public class Card implements Encodable {

    private final int cardNum;
    private final CardType cardType;
    public final static Map<Integer, String> CARD_NAMES = Map.ofEntries(
            Map.entry(1, "ACE"), Map.entry(2, "TWO"), Map.entry(3, "THREE"),
            Map.entry(4, "FOUR"), Map.entry(5, "FIVE"), Map.entry(6, "SIX"),
            Map.entry(7, "SEVEN"), Map.entry(8, "EIGHT"), Map.entry(9, "NINE"),
            Map.entry(10, "TEN"), Map.entry(11, "JACK"), Map.entry(12, "QUEEN"),
            Map.entry(13, "KING"), Map.entry(14, "JOKER")
    );
    public final static Map<Integer, Integer> CARD_SCORES = Map.ofEntries(
            Map.entry(1, 15), Map.entry(2, 5), Map.entry(3, 5), Map.entry(4, 5),
            Map.entry(5, 5), Map.entry(6, 5), Map.entry(7, 5), Map.entry(8, 5),
            Map.entry(9, 5), Map.entry(10, 10), Map.entry(11, 10),
            Map.entry(12, 10), Map.entry(13, 10), Map.entry(14, 50)
    );

    /**
     * Instantiates a new playing card with the given card number and type.
     *
     * An IllegalCardException is thrown if the card number is not between 1 and 14
     * (inclusive), or a card number is not 14 when the card type is BLACK_JOKER or
     * RED_JOKER.
     *
     * @param cardNum     the card number
     * @param cardType    the card type
     * @throws IllegalCardException if not a valid card instance
     */
    public Card(int cardNum, CardType cardType) throws IllegalCardException {
        if (cardNum < 1 || cardNum > 14) {
            throw new IllegalCardException("Invalid card id: " + cardNum);
        } else if (cardNum == 14) {
            if (cardType != CardType.BLACK_JOKER && cardType != CardType.RED_JOKER) {
                throw new IllegalCardException("Card id is 14 but card type is: " + cardType);
            }
        } else {
            if (cardType == CardType.BLACK_JOKER || cardType == CardType.RED_JOKER) {
                throw new IllegalCardException("Card type is JOKER but card id is: " + cardNum);
            }
        }
        this.cardNum = cardNum;
        this.cardType = cardType;
    }

    /**
     * Returns the card number
     *
     * @return the card number
     */
    public int getCardNum() {
        return this.cardNum;
    }

    /**
     * Returns the card type
     *
     * @return the card type
     */
    public CardType getCardType() {
        return this.cardType;
    }

    /**
     * Determine whether the two card instances are equal.
     *
     * Two instances are only considered equal if they have the same card number,
     * and the same card type.
     *
     * @param o other object to check for equality.
     * @return true if two cards are equal. False otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Card) {
            Card otherCard = (Card) o;
            return this.cardNum == otherCard.getCardNum()
                    && this.cardType == otherCard.getCardType();
        }
        return false;
    }

    /**
     * Retrieve the human-readable representation of this card instance.
     *
     * @return the String representation of this card instance
     */
    @Override
    public String toString() {
        String cardName = Card.CARD_NAMES.get(this.cardNum);
        if (this.cardNum == 14) {
            if (this.cardType == CardType.BLACK_JOKER) {
                return cardName + " - " + "BLACK";
            } else {
                return cardName + " - " + "RED";
            }
        } else {
            return this.cardType + " - " + cardName;
        }
    }

    /**
     * Retrieve the machine-readable representation of this card instance, in the format
     * Card:cardNum:cardType
     *
     * @return the encoded card instance
     */
    public String encode() {
        return String.format("Card:%d:%s",
                this.cardNum,
                this.cardType);
    }

    /**
     * Read a playing card from its encoded representation and creates a new instance.
     *
     * If the encoding is not valid, or the constructor throws an exception, then
     * a BadEncodingException is thrown.
     *
     * @param string string representing the encoded playing card
     * @return decoded card instance
     * @throws BadEncodingException if the format of the given string is invalid
     */
    public static Card fromString(String string) throws BadEncodingException {
        int cardNum;
        CardType cardType;
        String[] inputArgs = string.split(":");
        if (inputArgs.length != 3) {
            throw new BadEncodingException("Unexpected number of arguments: "
                    + inputArgs.length);
        }
        if (!inputArgs[0].equals("Card")) {
            throw new BadEncodingException("Encoded string should start with 'Card'");
        }
        try {
            cardNum = Integer.parseInt(inputArgs[1]);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("Could not parse cardNum: " + inputArgs[1]);
        }
        try {
            cardType = CardType.valueOf(inputArgs[2]);
        } catch (IllegalArgumentException e) {
            throw new BadEncodingException("Could not parse cardType: " + inputArgs[2]);
        }
        try {
            return new Card(cardNum, cardType);
        } catch (IllegalCardException e) {
            throw new BadEncodingException("Illegal constructor arguments");
        }
    }

    public static String encodeString(String string) {
        String[] cardVars = string.split(" - ");
        int cardNum = -1;
        String cardType;
        if (cardVars[0].equals("JOKER")) {
            cardNum = 14;
            if (cardVars[1].equals("RED")) {
                cardType = "RED_JOKER";
            } else {
                cardType = "BLACK_JOKER";
            }
        } else {
            for (Map.Entry<Integer, String> entry : Card.CARD_NAMES.entrySet()) {
                if (cardVars[1].equals(entry.getValue())) {
                    cardNum = entry.getKey();
                    break;
                }
            }
            cardType = cardVars[0];
        }
        return String.format("Card:%d:%s", cardNum, cardType);
    }

    public static List<CardType> getSuits() {
        return Arrays.asList(CardType.DIAMOND, CardType.HEART, CardType.CLUB, CardType.SPADE);
    }
}

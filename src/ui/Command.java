package ui;

import algorithms.Algorithm;
import hand.Card;
import hand.Deck;
import util.BadEncodingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Command {
    public final static int DEFAULT_NUM_DECKS = 2;
    public final static int INVALID_INT = -1;
    public final static int DEFAULT_HAND_SIZE = 11;
    public final static String INVALID_CARD_MSG = "Error: %s is not a valid card name\nAn example of a valid name is 'Card:1:SPADE'\n";
    private List<Card> hand;
    private int numDecks;
    private int numBooks;
    private int numRuns;

    public Command() {
        this.hand = new ArrayList<>();
        this.numDecks = DEFAULT_NUM_DECKS;
        this.numBooks = 0;
        this.numRuns = 0;
    }

    public void readCommand(String line) {
        String[] splitArgs = line.split(" ", 2);
        String command = splitArgs[0];
        switch (command) {
            case "help":
                help();
                break;
            case "view":
                int numCards = this.hand.size();
                if (numCards == 0) {
                    System.out.println("empty");
                } else {
                    for (int i = 1; i <= this.hand.size(); i++) {
                        System.out.printf("%d. %s\n", i, this.hand.get(i - 1));
                    }
                }
                System.out.printf("\nNum books: %d\nNum runs: %d\n", this.numBooks, this.numRuns);
                break;
            case "stacks":
                setNumStacks(line);
                break;
            case "decks":
                setNumDecks(line);
                break;
            case "add":
                addCards(line);
                break;
            case "new":
                createNew(line);
                break;
            case "build":
                buildBases(line);
                break;
            case "score":
                System.out.println(Algorithm.getScore(this.hand));
                break;
            case "reset":
                this.hand = new ArrayList<>();
                break;
            case "exit":
                System.exit(0);
                break;
            default:
                System.out.println("Error: unknown command");
        }
    }

    public static void help() {
        Map<String, String> help = Map.ofEntries(
                Map.entry("view", "look at current cards in hand"),
                Map.entry("stacks [num books] [num runs]", "set the number of books and runs"),
                Map.entry("decks [num]", "specify number of decks to draw cards from (2 by default)"),
                Map.entry("add [card name] [num]", "add new card(s) to hand (1 by default)"),
                Map.entry("new [num]", "instantiates a new hand of randomly drawn cards\n\t\t(11 by default)"),
                Map.entry("build", "determine the best bases for building books and/or runs,\n\t\tand how many more cards are needed."),
                Map.entry("score", "determine your current score"),
                Map.entry("reset", "resets the player's hand"),
                Map.entry("exit", "terminates the program")
        );
        System.out.println("List of valid commands:");
        for (Map.Entry<String, String> entry : help.entrySet()) {
            System.out.printf("\t%s\n\t\t%s\n", entry.getKey(), entry.getValue());
        }
    }

    public static int parseInt(String token) {
        try {
            // Parse integer
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            System.out.println("Error: expecting an integer");
        }
        return INVALID_INT;
    }

    public static int parsePosInt(String token) {
        int num = parseInt(token);
        if (num > 0) {
            return num;
        } else {
            System.out.println("Error: must be a positive integer");
        }
        return INVALID_INT;
    }

    public void setNumStacks(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length > 2) {
            // At least two arguments
            // Parse number of books and runs
            int numBooks = parseInt(tokens[1]);
            int numRuns = parseInt(tokens[2]);
            if (numBooks >= 0 && numRuns >= 0) {
                this.numBooks = numBooks;
                this.numRuns = numRuns;
            } else {
                System.out.println("Error: arguments must both be non-negative");
            }
        } else {
            System.out.println("Error: expecting at least two arguments");
        }
    }

    public void setNumDecks(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length == 1) {
            this.numDecks = DEFAULT_NUM_DECKS;
        } else {
            // At least one argument
            // Parse number of decks
            int num = parsePosInt(tokens[1]);
            if (num != INVALID_INT) {
                this.numDecks = num;
            }
        }
    }

    public void addCards(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length == 1) {
            System.out.println("Error: expecting at least one argument");
        } else {
            // At least one argument
            try {
                // Parse card name
                Card card = Card.fromString(tokens[1]);
                int num = 1;
                if (tokens.length > 2) {
                    // At least two arguments
                    // Parse number of cards
                    num = parsePosInt(tokens[2]);
                }
                if (num != INVALID_INT) {
                    for (int i = 0; i < num; i++) {
                        hand.add(card);
                    }
                }
            } catch (BadEncodingException e) {
                System.out.printf(INVALID_CARD_MSG, tokens[1]);
            }
        }
    }

    public void createNew(String input) {
        String[] tokens = input.split(" ");
        int handSize = DEFAULT_HAND_SIZE;
        if (tokens.length > 1) {
            // At least one argument
            // Parse number of cards
            handSize = parsePosInt(tokens[1]);
        }
        if (handSize != INVALID_INT) {
            this.hand = Deck.shuffledHand(handSize, this.numDecks);
        }
    }

    public void buildBases(String input) {
        String[] tokens = input.split(" ");
        Map<String, List<List<Card>>> bases = Algorithm.getOptimalBases(
                this.hand, this.numBooks, this.numRuns);
        for (Map.Entry<String, List<List<Card>>> entry : bases.entrySet()) {
            int numPrinted = 0;
            StringBuilder label = new StringBuilder(entry.getKey());
            // Remove trailing 's'
            label.setLength(label.length() - 1);
            for (List<Card> stack : entry.getValue()) {
                // Print out bases
                numPrinted++;
                System.out.printf("%s %d: %s\n", label, numPrinted, stack);
            }
        }
        int numCardsMissing = Algorithm.numCardsMissing(bases, this.numBooks, this.numRuns);
        System.out.printf("\nNumber of cards missing: %d\n", numCardsMissing);
    }
}

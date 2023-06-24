import hand.*;
import util.*;

import java.io.*;
import java.util.*;

/**
 * The main class which handles interaction with the user.
 */
public class Main {
    public final static int DEFAULT_NUM_DECKS = 2;
    public final static int DEFAULT_HAND_SIZE = 11;
    private static List<Card> hand = new ArrayList<>();
    private static int numDecks = DEFAULT_NUM_DECKS;

    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String line;
            prompt();
            while ((line = br.readLine()) != null) {
                readCommand(line);
                prompt();
            }
        } catch (IOException e) {
            System.out.println("An IO Exception was thrown");
        }
    }

    public static void prompt() {
        System.out.print("> ");
    }

    public static void setNumDecks(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length == 1) {
            numDecks = DEFAULT_NUM_DECKS;
        } else {
            // At least one argument
            try {
                // Parse number of decks
                int num = Integer.parseInt(tokens[1]);
                if (num > 0) {
                    numDecks = num;
                } else {
                    System.out.println("Error: number of decks must be positive");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: expecting an integer");
            }
        }
    }
    public static void addCards(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length == 1) {
            System.out.println("Error: expecting at least one argument");
        } else {
            // At least one argument
            try {
                // Parse card name
                Card card = Card.fromString(tokens[1]);
                if (tokens.length == 2) {
                    // Add only one card
                    hand.add(card);

                } else {
                    // At least two arguments
                    try {
                        // Parse number of cards
                        int num = Integer.parseInt(tokens[2]);
                        if (num > 0) {
                            for (int i = 0; i < num; i++) {
                                hand.add(card);
                            }
                        } else {
                            System.out.println("Error: number of cards must be positive");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: expecting an integer");
                    }
                }
            } catch (BadEncodingException e) {
                System.out.printf("Error: %s is not a valid card name\n", tokens[1]);
            }
        }
    }

    public static void createNew(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length == 1) {
            hand = Deck.shuffledHand(DEFAULT_HAND_SIZE, numDecks);
        } else {
            // At least one argument
            try {
                // Parse number of cards
                int num = Integer.parseInt(tokens[1]);
                if (num > 0) {
                    hand = Deck.shuffledHand(num, numDecks);
                } else {
                    System.out.println("Error: number of cards must be positive");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: expecting an integer");
            }
        }
    }

    public static void buildBases(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length < 3) {
            System.out.println("Error: expecting at least two arguments");
        } else {
            // At least two arguments
        }
    }

    public static void help() {
        String[] help = {
                "List of valid commands:",
                "\tview\n\t\tlook at current cards in hand",
                "\tdecks [num]\n\t\tspecify number of decks to draw cards from (2 by default).",
                "\tadd [card name] [num]\n\t\tadd new card(s) to hand (1 by default).",
                "\tnew [num]\n\t\tinstantiates a new hand of randomly drawn cards\n\t\t(11 by default).",
                "\tbuild [num books] [num runs]\n\t\tdetermine the best bases for building books and/or runs,\n\t\tand how many more cards are needed.",
                "\treset\n\t\tresets the player's hand",
                "\texit\n\t\texits the program"
        };
        for (String line : help) {
            System.out.println(line);
        }
    }
    public static void readCommand(String line) {
        String[] splitArgs = line.split(" ", 2);
        String command = splitArgs[0];
        switch (command) {
            case "help":
                help();
                break;
            case "view":
                int numCards = hand.size();
                if (numCards == 0) {
                    System.out.println("empty");
                } else {
                    for (int i = 1; i <= hand.size(); i++) {
                        System.out.printf("%d. %s\n", i, hand.get(i - 1));
                    }
                }
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
                break;
            case "reset":
                hand = new ArrayList<>();
                break;
            case "exit":
                System.exit(0);
                break;
            default:
                System.out.println("Error: unknown command");
        }
    }
}



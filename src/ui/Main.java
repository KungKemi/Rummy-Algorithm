package ui;

import java.io.*;

/**
 * The main class which handles interaction with the user.
 */
public class Main {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String line;
            Command command = new Command();
            prompt();
            while ((line = br.readLine()) != null) {
                command.readCommand(line);
                prompt();
            }
        } catch (IOException e) {
            System.out.println("An IO Exception was thrown");
        }
    }

    public static void prompt() {
        System.out.print("> ");
    }
}



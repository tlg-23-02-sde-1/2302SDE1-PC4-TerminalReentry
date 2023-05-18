package com.team4.terminal_reentry.applications;


import com.team4.terminal_reentry.setup.Room;
import com.team4.terminal_reentry.setup.Scenario;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Scanner;

public class Application {

    private final Scanner scanner = new Scanner(System.in);
    private final TextParser textParser = new TextParser();

    public void run() {
        titleScreen();
        if(newGame()) {
            basicInfo();
            instructions();
            Map<String, Room> map = setUpMap();
            Room currentRoom = map.get("Harmony");

            do {
                displayScreen(currentRoom);
                // controller.display room info
                // String input = scanner.nextLine()
                // controller.process(textparser.handleInput(input));
                //
            }
            while (!promptForCommand().toLowerCase().equals("quit"));//textParser.handleInput(scanner.nextLine())[0]);
        }
    }

    private String promptForCommand() {
        System.out.println("Enter your command: ");
        return scanner.nextLine();
    }

    private void displayScreen(Room currentRoom) {
        Console.clear();
        displayISS();
        System.out.println("\nYou are currently in the " + currentRoom.getName() + " module.");
        System.out.println("Description of the module: You " + currentRoom.getDescription());
        System.out.println("Possible directions you can go:");
        currentRoom.getExits().forEach((key, value) -> System.out.println(key + ": " + value));
    }

    private void displayISS() {
        Console.clear();
        try {
            String path = "src/main/resources/map.txt";
            // read the entire file as a string
            String contents = Files.readString(Path.of(path));
            System.out.println(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String,Room> setUpMap() {
        Scenario scenario = null;
        try {
            scenario = new Scenario();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return scenario.getMap();
    }

    private boolean newGame() {
        Console.clear();
        System.out.println("New Game --> [Y,N] : ");
        String answer = scanner.nextLine();
        boolean valid = false;
        while(!valid) {
            if(answer.toLowerCase().equals("y")) {
                valid = true;
            }
            else if(answer.toLowerCase().equals("n")) {
                break;
            }
            else {
                System.out.println("Invalid Input. Please enter y or n: ");
                answer = scanner.nextLine();
            }
        }
        return valid;
    }

    private void instructions() {
        Console.clear();
        try {
            String path = "src/main/resources/instructions.txt";
            // read the entire file as a string
            String contents = Files.readString(Path.of(path));
            System.out.println(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
        enterToContinue();
    }

    private void basicInfo() {
        Console.clear();
        try {
            String path = "src/main/resources/basicInfo.txt";
            // read the entire file as a string
            String contents = Files.readString(Path.of(path));
            System.out.println(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
        enterToContinue();
    }

    private void titleScreen() {
        Console.clear();
        try {
            String path = "src/main/resources/titleScreen.txt";
            // read the entire file as a string
            String contents = Files.readString(Path.of(path));
            System.out.println(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
        enterToContinue();
    }

    private void enterToContinue() {
        System.out.println("                Press enter to continue...");
        // Wait for the user to press enter
        scanner.nextLine();
    }
}
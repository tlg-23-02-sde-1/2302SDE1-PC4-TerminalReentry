package com.team4.terminal_reentry.applications;


import com.team4.terminal_reentry.setup.Player;
import com.team4.terminal_reentry.setup.Room;
import com.team4.terminal_reentry.setup.Scenario;
import jdk.jshell.spi.SPIResolutionException;

import javax.naming.ldap.Control;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.Scanner;

public class Application {

    private final Scanner scanner = new Scanner(System.in);
    private final TextParser textParser = new TextParser();

    private String resourcePath = "src/main/resources/";

    private List<String> winCondition = new ArrayList<>();


    public void run() {
        titleScreen();
        if(newGame()) {
            basicInfo();
            instructions();
            Map<String, Room> map = setUpMap();
            Room currentRoom = map.get("Harmony");
            Player player = new Player(currentRoom);
            Controller controller = new Controller(map,player,winCondition);
            boolean quit = false;
            do {
                displayScreen(player.getCurrentRoom());
                String command = promptForCommand();
                String[] response = textParser.handleInput(command);
                if(response[0].equals("200")) {
                    quit = controller.execute(response);
                }
                enterToContinue();
            }
            while (!quit);//textParser.handleInput(scanner.nextLine())[0]);
        }
    }

    private String promptForCommand() {
        System.out.println("Enter your command: ");
        return scanner.nextLine();
    }

    private void displayScreen(Room currentRoom) {
        Console.clear();
        displayISS(currentRoom.getName());
        System.out.println("\nYou are currently in the " + currentRoom.getName() + " module.");
        System.out.println("You " + currentRoom.getDescription());
        System.out.println("Possible directions you can go:");
        currentRoom.getExits().forEach((key, value) -> System.out.println(key + ": " + value));
        if(!currentRoom.getInventory().isEmpty()) {
            System.out.println("You see the following items room: ");
            currentRoom.getInventory().forEach((item)-> System.out.println(item.getName()));
        }
        if(!currentRoom.getNpcs().isEmpty()) {
            System.out.println("You see the following people in the room: ");
            currentRoom.getNpcs().forEach((npc)-> System.out.println(npc.getName()));
        }
    }

    private void displayISS(String roomName) {
        try {
            String path = "/locations/" + roomName + ".txt";
            // read the entire file as a string
            String contents = readResource(path);
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
        assert scenario != null;
        winCondition = scenario.getWinCondition();
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
            // read the entire file as a string
            String contents = readResource("/instructions.txt");
            System.out.println(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
        enterToContinue();
    }

    private void basicInfo() {
        Console.clear();
        try {
            // read the entire file as a string
            String contents = readResource("/basicInfo.txt");
            System.out.println(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
        enterToContinue();
    }

    private void titleScreen() {
        Console.clear();
        try {
            // read the entire file as a string
            String titleScreen = readResource("/titleScreen.txt");
            System.out.println(titleScreen);
        } catch (IOException e) {
            e.printStackTrace();
        }
        enterToContinue();
    }


    private static String readResource(String path) throws IOException {
        try (InputStream is = Application.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void enterToContinue() {
        System.out.println("                Press enter to continue...");
        // Wait for the user to press enter
        scanner.nextLine();
    }
}
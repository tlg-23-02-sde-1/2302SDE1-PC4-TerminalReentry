package com.team4.terminal_reentry.applications;

import java.util.*;

public class TextParser {
    private final Map<String, String> commands;

    public TextParser() {
        commands= new HashMap<>();
        commands.put("Look", "Look around");
        commands.put("go", "Move in the specified direction.");
        commands.put("take", "Pick up an item.");
        commands.put("use", "Use an item in your inventory.");
        commands.put("inventory", "Check your inventory.");
        commands.put("quit", "Quit the game.");
        commands.put("help", "show the commands");

    }

    public void handleInput(String input) {
        if (input.equalsIgnoreCase("help")) {
            displayHelp();
        } else {
            System.out.println("Invalid command: " + commands);
        }
    }

    private void displayHelp() {
        System.out.println("List of commands: ");
        for (Map.Entry<String,String> entry : commands.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }

}
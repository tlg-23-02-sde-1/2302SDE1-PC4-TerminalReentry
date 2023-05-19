package com.team4.terminal_reentry.applications;

import java.util.*;

public class TextParser {
    private final Map<String, String> commands;
    private final Map<String, String> synonyms;

    public TextParser() {
        commands= new HashMap<>();
        commands.put("look", "Look at object"); //prints room.description & room.inventory
        commands.put("go", "Move in the specified direction.");
        commands.put("take", "Pick up an item.");
        commands.put("use", "Use an item in your inventory.");
        commands.put("inventory", "Check your inventory.");
        commands.put("quit", "Quit the game.");
        commands.put("help", "show the commands");
        commands.put("inspect", "Get more details about something.");
        commands.put("talk", "talk to characters");
        commands.put("accuse","accuse the murderer");

        synonyms= new HashMap<>();
        synonyms.put("move", "go");
        synonyms.put("grab", "take");
        synonyms.put("view", "look");
        synonyms.put("check inventory", "inventory");
        synonyms.put("examine", "look");
        synonyms.put("apply", "use");
        synonyms.put("get", "take");
        synonyms.put("help!", "help");
        synonyms.put("help?", "help");
        synonyms.put("check", "look");
        synonyms.put("observe", "inspect");
    }

    public String[] handleInput(String input) {
        input = reformatInput(input);
        Scanner inputStream = new Scanner(input);
        String[] result = {null, "", ""};
        if(!inputStream.hasNext()) {
            result[0] = "You need to provide input.";
        } else {
            String command = inputStream.next();
            if (commands.containsKey(command)) {
                result[1] = command;
            } else {
                result[0] = input + " is not valid because " + command + " is not a valid command.";
            }
        }
        while(inputStream.hasNext()) {
            if (inputStream.hasNext()) {
                result[2] = result[2] + " " + inputStream.next();
                result[2] = result[2].trim();
            } else {
                result[2] = null;
            }
        }
        if ("help".equalsIgnoreCase(result[1])) {
            displayHelp();
        }

        result[0] = result[0] == null ? "200" : result[0];

        return result;
    }

    private String reformatInput(String input) {
        String fixedInput = input.replace(" at ", " ")
                .replace(" the ", " ").replace(" to "," ").toLowerCase();
        for(Map.Entry<String, String> entry : synonyms.entrySet()) {
            fixedInput= fixedInput.replace(entry.getKey(), entry.getValue());
        }
        return fixedInput;
    }

    private void displayHelp() {
        System.out.println("List of commands: ");
        for (Map.Entry<String,String> entry : commands.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }

}
package com.team4.terminal_reentry.applications;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class TextParser {

    private Map<String, String> commands;
    private List<Map.Entry<String,String>> sortedSynonyms;
    private final String INDENT = "\t\t";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BOLD = "\u001B[1m";
    private final Scanner scanner = new Scanner(System.in);

    public TextParser() {

        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String,String>>(){}.getType();

        try {
            InputStream commandsStream = getClass().getClassLoader().getResourceAsStream("commands.json");
            InputStream synonymsStream = getClass().getClassLoader().getResourceAsStream("synonyms.json");

            if (commandsStream == null || synonymsStream == null) {
                throw new FileNotFoundException("Required resources not found");
            }

            commands = gson.fromJson(new InputStreamReader(commandsStream), mapType);
            Map<String, String> synonyms = gson.fromJson(new InputStreamReader(synonymsStream), mapType);

            sortedSynonyms = synonyms.entrySet().stream()
                    .sorted(Comparator.comparing((Map.Entry<String, String> e) -> e.getKey().split("\\s+").length).reversed())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] handleInput(String input) {
        input = reformatInput(input);
        Scanner inputStream = new Scanner(input);
        String[] result = {null, "", ""};
        if(!inputStream.hasNext()) {
            result[0] = INDENT + "You need to provide input.";
            enterToContinue();
        } else {
            String command = inputStream.next();
            if (commands.containsKey(command)) {
                result[1] = command;
            } else {
                result[0] = INDENT + input + " is not valid because " + command + " is not a valid command. Type 'help' for more information";
                System.out.println(result[0]);
                enterToContinue();
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
            enterToContinue();
        }
        if("restore".equalsIgnoreCase(result[1])) {
            result[0] = "restore";
        }

        result[0] = result[0] == null ? "200" : result[0];

        return result;
    }

    private String reformatInput(String input) {
        String fixedInput = input.strip().replaceAll("\\b(?:at|the|to)\\b\\s+", " ").toLowerCase();
        for(Map.Entry<String, String> entry : sortedSynonyms) {
            fixedInput= fixedInput.replace(entry.getKey(), entry.getValue());
        }
        return fixedInput;
    }

    private void displayHelp() {
        System.out.println(ANSI_BOLD + INDENT + "List of commands: \n" + ANSI_RESET);
        for (Map.Entry<String,String> entry : commands.entrySet()) {
            System.out.println(INDENT + ANSI_BOLD + entry.getKey() + ANSI_RESET + " - " + entry.getValue());
        }
    }

    private void enterToContinue() {
        System.out.print(INDENT + "Press enter to continue...");
        // Wait for the user to press enter
        scanner.nextLine();
    }
}
package com.team4.terminal_reentry.applications;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Application {

    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        titleScreen();
        basicInfo();
        instructions();
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
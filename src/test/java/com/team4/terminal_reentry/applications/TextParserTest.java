package com.team4.terminal_reentry.applications;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class TextParserTest {
    TextParser input = null;
    private final Scanner scanner = new Scanner(System.in);

    @BeforeEach
    void setUp() {
        input = new TextParser();
    }

    @Test
    void handleInput_shouldReturn200_look_object_whenGivenValidLookCommand() {
        String inputText = "Look at the keyBoard";
        String[] actual = input.handleInput(inputText);
        String[] expected = {"200", "look", "keyboard"};
        assertArrayEquals(expected, actual);
    }

    @Test
    void handleInput_shouldReturn200_go_direction_whenGivenValidGoCommand() {
        String inputText = "move to the left";
        String[] actual = input.handleInput(inputText);
        String[] expected = {"200", "go", "left"};
        assertArrayEquals(expected, actual);
    }

    @Test
    void handleInput_shouldReturn200_take_object_whenGivenValidTakeCommand() {
        String inputText = "get the Cookie";
        String[] actual = input.handleInput(inputText);
        String[] expected = {"200", "take", "cookie"};
        assertArrayEquals(expected, actual);
    }

    @Test
    void handleInput_shouldReturn200_use_object_whenGivenValidUseCommand() {
        String inputText = "Use the blacklight";
        String[] actual = input.handleInput(inputText);
        String[] expected = {"200", "use", "blacklight"};
        assertArrayEquals(expected, actual);
    }

    @Test
    void handleInput_shouldReturn200_inventory_null_whenGivenValidInventoryCommand() {
        String inputText = "inventory";
        String[] actual = input.handleInput(inputText);
        String[] expected = {"200", "inventory", null};
        assertArrayEquals(expected, actual);
    }

    @Test
    void handleInput_shouldReturn200_help_null_whenGivenValidHelpCommand() {
        String inputText = "HELP";
        String[] actual = input.handleInput(inputText);
        String[] expected = {"200", "help", null};
        assertArrayEquals(expected, actual);
    }

    @Test
    void handleInput_shouldReturn200_quit_null_whenGivenValidQuitCommand() {
        String inputText = "quit";
        String[] actual = input.handleInput(inputText);
        String[] expected = {"200", "quit", null};
        assertArrayEquals(expected, actual);
    }

    @Test
    void handleInput_shouldReturn40X_help_null_whenGivenValidHelpCommand() {
        String inputText = "HELP! me out";
        String[] actual = input.handleInput(inputText);
        String[] expected = {"200", "help", null};
        assertArrayEquals(expected, actual);
    }
}
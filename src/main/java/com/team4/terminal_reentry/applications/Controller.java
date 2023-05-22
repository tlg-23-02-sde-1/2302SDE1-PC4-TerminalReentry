package com.team4.terminal_reentry.applications;

import com.team4.terminal_reentry.items.Item;
import com.team4.terminal_reentry.items.Weapon;
import com.team4.terminal_reentry.setup.NPC;
import com.team4.terminal_reentry.setup.Player;
import com.team4.terminal_reentry.setup.Room;

import javax.swing.*;
import java.util.*;

class Controller {
    //get, go, look, quit, help
    public static final String INDENT = "\t\t";
    private final Map<String, Room> map;
    private final Player player;
    private List<String> winCondition = new ArrayList<>();
    Scanner scanner = new Scanner(System.in);

    public Controller(Map<String, Room> map, Player player, List<String> winCondition) {
        this.map = map;
        this.player = player;
        this.winCondition = winCondition;
    }

    public boolean execute(String[] commands) {
        boolean isQuit = false;
        String verb = commands[1];
        String noun = commands[2];
        switch (verb) {
            case "accuse":
                System.out.println("List of possible suspects: ");
                List<String> accusation = new ArrayList<>();
                int index = 1;
                for(Room room: map.values()) {
                    for(NPC npc: room.getNpcs()) {
                        System.out.println(index + ". " + npc.getName());
                        index++;
                    }
                }
                System.out.println("Please type who you want to accuse: ");
                accusation.add(scanner.nextLine());

                index = 1;
                System.out.println("List of possible murder weapons: ");
                for(Room room: map.values()) {
                    for(Item item: room.getInventory()) {
                        if(item instanceof Weapon) {
                            System.out.println(index + ". " + item.getName());
                            index++;
                        }
                    }
                }
                System.out.println("Please type the murder weapon: ");
                accusation.add(scanner.nextLine());

                index = 1;
                System.out.println("List of possible locations: ");
                for(Room room: map.values()) {
                    System.out.println(index + ". " + room.getName());
                    index++;
                }
                System.out.println("Please type location of the murder: ");
                accusation.add(scanner.nextLine());

                if(accusation.containsAll(winCondition)) {
                    System.out.println("You got it right!");
                    isQuit = true;
                }
                else {
                    System.out.println("That was wrong! Try again");
                }
                break;
            case "inventory":
                System.out.println(player.showInventory());
            case "look":
                player.getCurrentRoom().getInventory().forEach((item)->{
                    if(item.getName().equals(noun)) {
                        System.out.println(item.getDescription());
                    }
                });
                player.getInventory().forEach((item)->{
                    if(item.getName().equals(noun)) {
                        System.out.println(item.getDescription());
                    }
                });
                break;
            case "inspect":
                player.getCurrentRoom().getInventory().forEach((item)->{
                    if(item.getName().equals(noun)) {
                        System.out.println(item.getData());
                    }
                });
                player.getInventory().forEach((item)->{
                    if(item.getName().equals(noun)) {
                        System.out.println(item.getData());
                    }
                });
                break;
            case "go":
                Room current = player.getCurrentRoom();
                player.getCurrentRoom().getExits().forEach((key, value) -> {
                    if (key.equals(noun)) {
                        player.setCurrentRoom(map.get(value));
                    }
                });
                if (current.equals(player.getCurrentRoom())) {
                    System.out.println("You can't go that way dummy!");
                }
                break;
            case "take":
                Item item = null;
                for(int i = 0; i < player.getCurrentRoom().getInventory().size(); i++) {
                    Item value = player.getCurrentRoom().getInventory().get(i);
                    if(value.getName().equals(noun)) {
                        player.addItem(value);
                        item = value;
                    }
                }
                player.getCurrentRoom().removeItem(item);
                if (item == null) {
                    System.out.println("There is no " + noun + " in the room.");
                }
                break;
            case "talk":
                String ANSI_RESET = "\u001B[0m";
                String ANSI_YELLOW = "\u001B[33m";
                player.getCurrentRoom().getNpcs().forEach((npc)->{
                    if(npc.getName().equalsIgnoreCase(noun) || npc.getFirstName().equalsIgnoreCase(noun) ||
                            npc.getLastName().equalsIgnoreCase(noun)){
                        System.out.println(INDENT  + "What would you like to ask " + ANSI_YELLOW + npc.getName()
                                + ANSI_RESET +" (Enter a number 1-4): ");
                        System.out.println(INDENT  + "1. Where were you?");
                        System.out.println(INDENT  + "2. What were you doing at the time of the victim's murder?");
                        System.out.println(INDENT  + "3. What do you think of the victim?");
                        System.out.println(INDENT  + "4. What else can you share?");
                        System.out.println(INDENT  + "5. Stop talking to " + npc.getName());
                        String question;
                        do {
                            System.out.print(INDENT  + "Enter: ");
                            question = scanner.nextLine();
                            switch(question) {
                                case "1":
                                    System.out.println(INDENT  + npc.getName() + " says: " + "\"I was at " +
                                            npc.getAnswers().get("locationAtTimeOfMurder") + "\"");
                                    break;
                                case "2":
                                    System.out.println(INDENT  + npc.getName() + " says: " + "\"I was " +
                                            npc.getAnswers().get("activityAtTimeOfMurder") + "\"");
                                    break;
                                case "3":
                                    System.out.println(INDENT  + npc.getName() + " says: " + "\"I think he " +
                                            npc.getAnswers().get("opinionOfVictim") + "\"");
                                    break;
                                case "4":
                                    System.out.println(INDENT  + npc.getName() + " says: " + "\"" +
                                            npc.getAnswers().get("otherTestimony") + "\"");
                                    break;
                                case "5":
                                    question = "quit";
                                    break;
                                default:
                                    System.out.println(INDENT  + "That is not a valid option!");
                                    break;
                            }
                        } while (!question.equals("quit"));
                    }
                });
                break;
            case "quit":
                isQuit = true;
                break;
        }
        return isQuit;
    }
}
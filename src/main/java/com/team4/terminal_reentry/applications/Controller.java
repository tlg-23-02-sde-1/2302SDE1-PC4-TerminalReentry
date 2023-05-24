package com.team4.terminal_reentry.applications;

import com.google.gson.Gson;
import com.team4.terminal_reentry.items.Item;
import com.team4.terminal_reentry.items.Weapon;
import com.team4.terminal_reentry.setup.NPC;
import com.team4.terminal_reentry.setup.Player;
import com.team4.terminal_reentry.setup.Room;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

class Controller {
    public static final String INDENT = "\t\t";
    private final Map<String, Room> map;
    private final Player player;
    private List<String> winCondition = new ArrayList<>();
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31M";
    private static final String ANSI_GREEN = "\u001B[32m";
    Scanner scanner = new Scanner(System.in);
    private final MidiPlayer midiPlayer;
    //TODO: fix soundFX
    private final SoundFx soundFx = new SoundFx();

    public Controller(Map<String, Room> map, Player player, List<String> winCondition, MidiPlayer midiPlayer) {
        this.map = map;
        this.player = player;
        this.winCondition = winCondition;
        this.midiPlayer = midiPlayer;
    }
    private void enterToContinue() {
        System.out.print(INDENT + "Press enter to continue...");
        // Wait for the user to press enter
        scanner.nextLine();
    }

    public boolean execute(String[] commands) {
        boolean isQuit = false;
        String verb = commands[1];
        String noun = commands[2];
        switch (verb) {
            case "accuse":
                System.out.println(INDENT + "List of possible suspects: ");
                List<String> accusation = new ArrayList<>();
                int index = 1;
                for(Room room: map.values()) {
                    for(NPC npc: room.getNpcs()) {
                        System.out.println(INDENT + index + ". " + npc.getName());
                        index++;
                    }
                }
                System.out.print(INDENT + "Please type who you want to accuse: ");
                accusation.add(scanner.nextLine());

                index = 1;
                System.out.println(INDENT + "List of possible murder weapons: ");
                for(Room room: map.values()) {
                    for(Item item: room.getInventory()) {
                        if(item instanceof Weapon) {
                            System.out.println(INDENT + index + ". " + item.getName());
                            index++;
                        }
                    }
                }
                System.out.print(INDENT + "Please type the murder weapon: ");
                accusation.add(scanner.nextLine());

                index = 1;
                System.out.println(INDENT + "List of possible locations: ");
                for(Room room: map.values()) {
                    System.out.println(INDENT + index + ". " + room.getName());
                    index++;
                }
                System.out.print(INDENT + "Please type location of the murder: ");
                accusation.add(scanner.nextLine());

                if (!accusation.stream().map(String::toLowerCase)
                        .allMatch(winCondition.stream()
                                .map(String::toLowerCase)
                                .collect(Collectors.toList())::contains)) {
                    System.out.println(INDENT + "That was wrong! Try again");
                } else {
                    System.out.println(INDENT + "You got it right!");
                    isQuit = true;
                }
                enterToContinue();
                break;
            case "inventory":
                System.out.println(INDENT + player.showInventory());
                soundFx.getItem1();
                break;
            case "look":
                player.getCurrentRoom().getInventory().forEach((item)->{
                    if(item.getName().equalsIgnoreCase(noun)) {
                        System.out.println(INDENT + item.getDescription());
                    }
                });
                player.getInventory().forEach((item)->{
                    if(item.getName().equalsIgnoreCase(noun)) {
                        System.out.println(INDENT + item.getDescription());
                    }
                });
                soundFx.getItem2();
                enterToContinue();
                break;
            case "inspect":
                player.getCurrentRoom().getInventory().forEach((item)->{
                    if(item.getName().equalsIgnoreCase(noun)) {
                        System.out.println(INDENT + item.getData());
                    }
                });
                player.getInventory().forEach((item)->{
                    if(item.getName().equalsIgnoreCase(noun)) {
                        System.out.println(INDENT + item.getData());
                        player.inspectedItem(item.getName(), item.getData());
                    }
                });
                soundFx.playTest();
                enterToContinue();
                break;
            case "go":
                Room current = player.getCurrentRoom();
                player.getCurrentRoom().getExits().forEach((key, value) -> {
                    if (key.equals(noun)) {
                        player.setCurrentRoom(map.get(value));
                        player.visitRoom(map.get(value).getName());
                    }
                });
                if (current.equals(player.getCurrentRoom())) {
                    System.out.println(INDENT + "You can't go that way dummy!");
                    enterToContinue();
                }
                break;
            case "take":
                Item item = null;
                for(int i = 0; i < player.getCurrentRoom().getInventory().size(); i++) {
                    Item value = player.getCurrentRoom().getInventory().get(i);
                    if(value.getName().equalsIgnoreCase(noun)) {
                        player.addItem(value);
                        item = value;
                        soundFx.pickupItem();
                    }
                }
                player.getCurrentRoom().removeItem(item);
                if (item == null) {
                    System.out.println(INDENT + "There is no " + noun + " in the room.");
                    enterToContinue();
                }
                break;
            case "talk":

                player.getCurrentRoom().getNpcs().forEach((npc)->{
                    if(npc.getName().equalsIgnoreCase(noun) || npc.getFirstName().equalsIgnoreCase(noun) ||
                            npc.getLastName().equalsIgnoreCase(noun)){
                        player.metNpc(npc.getName());
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
            case "music":
                if("off".equalsIgnoreCase(noun)){
                    midiPlayer.mute();
                }
                else if("on".equalsIgnoreCase(noun)) {
                    midiPlayer.playMusicThread(1);
                }
                else if("up".equalsIgnoreCase(noun)) {
                    midiPlayer.volumeUp();
                }
                else if("down".equalsIgnoreCase(noun)) {
                    midiPlayer.volumeDown();
                } else if("mute".equalsIgnoreCase(noun)) {
                    midiPlayer.mute();
                }
                enterToContinue();
                break;
            case "quit":
                isQuit = true;
                midiPlayer.stop();
                soundFx.killAll();
                break;
            case "logbook":
                String ANSI_RED = "\u001B[31m";
                System.out.println(INDENT + "NPCs Met:");
                for(String npc : player.getNpcMet()) {
                    System.out.println(INDENT + ANSI_YELLOW + npc + ANSI_RESET);
                }
                System.out.println(INDENT + "Items inspected with its data: ");
                for(Map.Entry<String,String> entry : player.getInspectedItem().entrySet()) {
                    System.out.println(INDENT + "Item: " + ANSI_RED + entry.getKey() + ANSI_RESET + "   Data: " + entry.getValue());
                }
                System.out.println(INDENT + "Rooms visited: ");
                for(String room : player.getRoomsVisited()) {
                    System.out.println(INDENT + ANSI_GREEN + room + ANSI_RESET);
                }
                enterToContinue();
                break;
            case "save":
                saveGame();
                enterToContinue();
                break;
        }
        return isQuit;
    }
    private void saveGame() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println(INDENT + "Please enter a name for your save file");
            String userInput = scanner.nextLine();

            String saveFileName;
            if(userInput.isEmpty()) {
                saveFileName = "saved1.json";
            } else {
                saveFileName = userInput + ".json";
            }

            Map<String, Object> gameData = new HashMap<>();

            gameData.put("currentLocation", player.getCurrentRoom());
            gameData.put("inventory", player.getInventory());
            gameData.put("npcMet", player.getNpcMet());
            gameData.put("inspectedItem", player.getInspectedItem());
            gameData.put("roomsVisited", player.getRoomsVisited());
            gameData.put("map",map);

            Gson gson = new Gson();
            String jsonData = gson.toJson(gameData);

            FileWriter writer = new FileWriter(saveFileName);
            writer.write(jsonData);
            writer.close();

            System.out.println(INDENT + "Game saved successfully");
            enterToContinue();
        } catch (IOException e) {
            System.out.println(INDENT + "Failed to save the game");

        }

    }
}
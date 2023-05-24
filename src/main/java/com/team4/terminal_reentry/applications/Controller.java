package com.team4.terminal_reentry.applications;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.team4.terminal_reentry.items.Item;
import com.team4.terminal_reentry.items.Weapon;
import com.team4.terminal_reentry.setup.NPC;
import com.team4.terminal_reentry.setup.Player;
import com.team4.terminal_reentry.setup.Room;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

class Controller {
    public static final String INDENT = "\t\t";
    private final Map<String, Room> map;
    private final Player player;
    private final List<String> winCondition;
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31M";
    private static final String ANSI_GREEN = "\u001B[32m";
    private final Scanner scanner = new Scanner(System.in);
    private final MidiPlayer midiPlayer;
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
                isQuit = accuse();
                break;
            case "look":
                look(verb, noun);
                break;
            case "inspect":
                inspect(verb, noun);
                break;
            case "go":
                go(noun);
                break;
            case "take":
                take(verb, noun);
                break;
            case "talk":
                talk(noun);
                break;
            case "music":
                music(noun);
                break;
            case "soundfx":
                soundfx(noun);
                break;
            case "quit":
                isQuit = true;
                midiPlayer.stop();
                soundFx.killAll();
                break;
            case "logbook":
                logbook();
                break;
            case "save":
                saveGame();
                enterToContinue();
                break;
        }
        return isQuit;
    }

    private void logbook() {
        System.out.println(INDENT + "NPCs Met:");
        for (String npc : player.getNpcMet()) {
            System.out.println(INDENT + ANSI_YELLOW + npc + ANSI_RESET);
        }
        System.out.println(INDENT + "Items inspected with its data: ");
        for (Map.Entry<String, String> entry : player.getInspectedItem().entrySet()) {
            System.out.println(INDENT + "Item: " + ANSI_RED + entry.getKey() + ANSI_RESET + "   Data: " + entry.getValue());
        }
        System.out.println(INDENT + "Rooms visited: ");
        for (String room : player.getRoomsVisited()) {
            System.out.println(INDENT + ANSI_GREEN + room + ANSI_RESET);
        }
        enterToContinue();
    }

    private void soundfx(String noun) {
        if ("off".equalsIgnoreCase(noun)) {
            soundFx.off();
        } else if ("on".equalsIgnoreCase(noun)) {
            soundFx.on();
        } else if ("up".equalsIgnoreCase(noun)) {
            soundFx.volumeUp();
        } else if ("down".equalsIgnoreCase(noun)) {
            soundFx.volumeDown();
        } else if ("mute".equalsIgnoreCase(noun)) {
            soundFx.off();
        }
    }

    private void music(String noun) {
        if ("off".equalsIgnoreCase(noun)) {
            midiPlayer.mute();
        } else if ("on".equalsIgnoreCase(noun)) {
            midiPlayer.playMusicThread(1);
        } else if ("up".equalsIgnoreCase(noun)) {
            midiPlayer.volumeUp();
        } else if ("down".equalsIgnoreCase(noun)) {
            midiPlayer.volumeDown();
        } else if ("mute".equalsIgnoreCase(noun)) {
            midiPlayer.mute();
        }
    }

    private void take(String verb, String noun) {
        Item item = null;
        for (int i = 0; i < player.getCurrentRoom().getInventory().size(); i++) {
            Item value = player.getCurrentRoom().getInventory().get(i);
            if (value.getName().equalsIgnoreCase(noun)) {
                player.addItem(value);
                item = value;
                soundFx.play(verb);
                break;
            }
        }
        player.getCurrentRoom().removeItem(item);
        if (item == null) {
            System.out.println(INDENT + "There is no " + noun + " in the room.");
            enterToContinue();
        }
    }

    private void go(String noun) {
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
    }

    private void inspect(String verb, String noun) {
        boolean found = player.getCurrentRoom().getInventory().stream()
                .anyMatch((Item item) -> hasMatch(noun, item))
                || player.getInventory().stream()
                .anyMatch((Item item) -> hasMatch(noun, item));
        if (!found) {
            System.out.println(INDENT + "You can't inspect " + noun);
        } else {
            soundFx.play(verb);
        }
        enterToContinue();
    }

    private void look(String verb, String noun) {
        boolean found = player.getCurrentRoom().getInventory().stream()
                .anyMatch((Item item) -> {
                    if (item.getName().equalsIgnoreCase(noun)) {
                        System.out.println(INDENT + item.getDescription());
                        return true;
                    }
                    return false;
                })
                || player.getInventory().stream()
                .anyMatch((Item item) -> {
                    if (item.getName().equalsIgnoreCase(noun)) {
                        System.out.println(INDENT + item.getDescription());
                        return true;
                    }
                    return false;
                });
        if (!found) {
            System.out.println(INDENT + "You can't look at " + noun);
        } else {
            soundFx.play(verb);
        }
        enterToContinue();
    }

    private boolean accuse() {
        boolean isQuit = false;
        //print suspects
        System.out.println(INDENT + "List of possible suspects: ");
        List<String> accusation = new ArrayList<>();
        int index = 1;
        for (Room room : map.values()) {
            for (NPC npc : room.getNpcs()) {
                System.out.println(INDENT + index + ". " + npc.getName());
                index++;
            }
        }
        //player guesses npc
        System.out.print(INDENT + "Please type who you want to accuse: ");
        accusation.add(scanner.nextLine());

        //print weapons
        index = 1;
        System.out.println(INDENT + "List of possible murder weapons: ");
        for (Room room : map.values()) {
            for (Item item : room.getInventory()) {
                if (item instanceof Weapon) {
                    System.out.println(INDENT + index + ". " + item.getName());
                    index++;
                }
            }
        }
        //player guesses murder weapon
        System.out.print(INDENT + "Please type the murder weapon: ");
        accusation.add(scanner.nextLine());

        //print rooms
        index = 1;
        System.out.println(INDENT + "List of possible locations: ");
        for (Room room : map.values()) {
            System.out.println(INDENT + index + ". " + room.getName());
            index++;
        }
        //player guesses room
        System.out.print(INDENT + "Please type location of the murder: ");
        accusation.add(scanner.nextLine());

        //check if player is correct
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
        return isQuit;
    }

    private void talk(String noun) {
        boolean found = player.getCurrentRoom().getNpcs().stream()
                .anyMatch((NPC npc) -> {
                    if (noun.equalsIgnoreCase(npc.getName())
                            || noun.equalsIgnoreCase(npc.getFirstName())
                            || noun.equalsIgnoreCase(npc.getLastName())) {
                        npcTalkMenu(npc);
                        player.metNpc(npc.getName());
                        return true;
                    }
                    return false;
                });
        if (!found) {
            System.out.println(INDENT + "You can't talk to " + noun);
            enterToContinue();
        }
    }

    private void npcTalkMenu(NPC npc) {
        Random random = new Random();
        int randomNumber;
        System.out.println(INDENT + "What would you like to ask " + ANSI_YELLOW + npc.getName()
                + ANSI_RESET + " (Enter a number 1-4): ");
        System.out.println(INDENT + "1. Where were you?");
        System.out.println(INDENT + "2. What were you doing at the time of the victim's murder?");
        System.out.println(INDENT + "3. What do you think of the victim?");
        System.out.println(INDENT + "4. What else can you share?");
        System.out.println(INDENT + "5. Stop talking to " + npc.getName());
        String question;
        do {
            randomNumber = random.nextInt(4) + 1;
            System.out.print(INDENT + "Enter: ");
            question = scanner.nextLine();
            if ("5".equals(question)) {
                question = "quit";
            } else if (randomNumber == 1) {
                randomNumber = random.nextInt(2) + 1;
                if (randomNumber == 1) {
                    System.out.println(INDENT + npc.getName() + " says: " + "\"I have to go to the bathroom\"");
                } else {
                    System.out.println(INDENT + npc.getName() + " says: " + "\"I can't handle this anymore\"");
                }
            } else {
                switch (question) {
                    case "1":
                        System.out.println(INDENT + npc.getName() + " says: " + "\"I was at " +
                                npc.getAnswers().get("locationAtTimeOfMurder") + "\"");
                        break;
                    case "2":
                        System.out.println(INDENT + npc.getName() + " says: " + "\"I was " +
                                npc.getAnswers().get("activityAtTimeOfMurder") + "\"");
                        break;
                    case "3":
                        System.out.println(INDENT + npc.getName() + " says: " + "\"I think he " +
                                npc.getAnswers().get("opinionOfVictim") + "\"");
                        break;
                    case "4":
                        System.out.println(INDENT + npc.getName() + " says: " + "\"" +
                                npc.getAnswers().get("otherTestimony") + "\"");
                        break;
                    default:
                        System.out.println(INDENT + question + " is not a valid option!");
                        break;
                }
            }
        } while (!question.equals("quit"));
    }

    private boolean hasMatch(String noun, Item item) {
        if (item.getName().equalsIgnoreCase(noun)) {
            System.out.println(INDENT + item.getData());
            player.inspectedItem(item.getName(), item.getData());
            return true;
        }
        return false;
    }

    private void saveGame() {
        try {
            // default save file name
            String saveFileName = "saved1.json";

            Map<String, Object> gameData = new HashMap<>();

            gameData.put("currentLocation", player.getCurrentRoom());
            gameData.put("inventory", player.getInventory());
            gameData.put("npcMet", player.getNpcMet());
            gameData.put("inspectedItem", player.getInspectedItem());
            gameData.put("roomsVisited", player.getRoomsVisited());
            gameData.put("map", map);

            Type typeListW = new TypeToken<List<String>>() {
            }.getType();

            Gson gson = new Gson();
            JsonArray winConditionArray = gson.toJsonTree(winCondition, typeListW).getAsJsonArray();
            gameData.put("winCondition", winConditionArray);

            String jsonData = gson.toJson(gameData);

            FileWriter writer = new FileWriter(saveFileName);
            writer.write(jsonData);
            writer.close();

            System.out.println(INDENT + "Game saved successfully");
        } catch (IOException e) {
            System.out.println(INDENT + "Failed to save the game");

        }
    }
}
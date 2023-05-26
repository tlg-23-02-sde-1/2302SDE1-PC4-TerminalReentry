package com.team4.terminal_reentry.applications;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.team4.terminal_reentry.items.ISS;
import com.team4.terminal_reentry.items.Item;
import com.team4.terminal_reentry.setup.TxtFormat;
import com.team4.terminal_reentry.setup.Player;
import com.team4.terminal_reentry.setup.Resource;
import com.team4.terminal_reentry.setup.Room;
import com.team4.terminal_reentry.setup.Scenario;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import static com.team4.terminal_reentry.setup.TxtFormat.INDENT;
import static com.team4.terminal_reentry.setup.TxtFormat.MAX_WIDTH;

public class Application {


    private final Scanner scanner = new Scanner(System.in);
    private final TextParser textParser = new TextParser();

    private List<String> winCondition = new ArrayList<>();
    private final MidiPlayer midiPlayer;
    private final ISS iss = new ISS();

    public Application() {
        Music source = new Music();
        midiPlayer = new MidiPlayer(source.getMidiBytes());
    }

    public void run() {
        midiPlayer.playMusicThread(1);
        Map<String, Room> map = null;
        Player player = null;
        Room currentRoom;
        titleScreen();
        Boolean newOrRestore = newGame();
        boolean quit = false;
        if (newOrRestore == null) {
            midiPlayer.stop();
            quit = true;
        }
        else if(newOrRestore) {
            basicInfo();
            instructions();
            map = setUpMap();
            currentRoom = map.get("Harmony");
            player = new Player(currentRoom);
        }
        else {
            JsonObject jObject = readInSavedFile("saved1.json");
            player = loadSavedPlayer(jObject);
            map = loadSavedMap(jObject.get("map").getAsJsonObject());
        }
        Controller controller = new Controller(map, player, winCondition, midiPlayer);
        while (!quit) {
            displayScreen(player.getCurrentRoom(), player);
            String command = promptForCommand();
            String[] response = textParser.handleInput(command);
            if (response[0].equals("200")) {
                quit = controller.execute(response);
            }
            else if(response[0].equals("restore")) {
                if (warnBeforeRestore()) {
                    JsonObject jObject = readInSavedFile("saved1.json");
                    player = loadSavedPlayer(jObject);
                    map = loadSavedMap(jObject.get("map").getAsJsonObject());
                    controller = new Controller(map, player, winCondition, midiPlayer);
                }
            }
        }
    }

    private boolean warnBeforeRestore() {
        boolean response = false;
        System.out.print("\n\n" + INDENT + "Would you like to restore a saved game? " +
                "\n" + INDENT + "You will lose all unsaved progress  --> [Y,N]");
        String answer = scanner.nextLine();
        boolean valid = false;
        while (!valid) {
            if (answer.equalsIgnoreCase("y")) {
                valid = true;
                response = true;
            } else if (answer.equalsIgnoreCase("n")) {
                break;
            } else {
                System.out.println(INDENT + "Invalid Input. Please enter y or n: ");
                answer = scanner.nextLine();
            }
        }
        return response;
    }


    private JsonObject readInSavedFile(String path) {
        JsonObject jObject = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            String fileContent = sb.toString();
            jObject = JsonParser.parseString(fileContent).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jObject;
    }

    private Player loadSavedPlayer(JsonObject jsonObject) {
        //JSON object converter helpers.
        Gson gson = new Gson();
        Type typeMapSS = new TypeToken<Map<String, String>>() {
        }.getType();
        Type typeSetS = new TypeToken<Set<String>>() {
        }.getType();
        Type typeListI = new TypeToken<List<Item>>() {
        }.getType();
        Type typeRoom = new TypeToken<Room>() {
        }.getType();
        Type typeListW = new TypeToken<List<String>>() {}.getType();

        //initialize the Map<String,String> for inspectedItem
        JsonObject items = jsonObject.get("inspectedItem").getAsJsonObject();
        Map<String, String> inspectedItem = gson.fromJson(items, typeMapSS);

        //initialize the Set<String> for roomsVisited
        JsonArray rooms = jsonObject.get("roomsVisited").getAsJsonArray();
        Set<String> roomsVisited = gson.fromJson(rooms, typeSetS);

        //initialize the Set<String> for npcMet
        JsonArray npcs = jsonObject.get("npcMet").getAsJsonArray();
        Set<String> npcMet = gson.fromJson(npcs, typeSetS);

        //initialize the List<String> for inventory
        JsonArray jinvent = jsonObject.get("inventory").getAsJsonArray();
        List<Item> inventory = gson.fromJson(jinvent, typeListI);

        //initialize currentLocation Room
        JsonObject currentRoom = jsonObject.get("currentLocation").getAsJsonObject();
        Room loadedCurrentRoom = gson.fromJson(currentRoom, typeRoom);

        JsonArray jWinCondition = jsonObject.get("winCondition").getAsJsonArray();
        winCondition = gson.fromJson(jWinCondition,typeListW);

        int moveCount = jsonObject.get("moveCount").getAsInt();

        return new Player(loadedCurrentRoom, inventory, inspectedItem, npcMet, roomsVisited, moveCount);
    }

    private Map<String, Room> loadSavedMap(JsonObject mapData) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Room>>() {
        }.getType();
        return gson.fromJson(mapData, type);
    }

    private String promptForCommand() {
        System.out.print("\n\n" + INDENT + "Enter your command: ");
        return scanner.nextLine();
    }

    private void displayScreen(Room currentRoom, Player player) {
        String ANSI_RESET = "\u001B[0m";
        String ANSI_RED = "\u001B[31m";
        String ANSI_GREEN = "\u001B[32m";
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_WHITE = "\u001B[37m";

        Console.clear();

        displayISS(currentRoom.getName(), player);
        System.out.println("\n" + TxtFormat.wordWrap("You are currently in the " + ANSI_GREEN + currentRoom.getName() + ANSI_RESET + " module."));
        System.out.println(TxtFormat.wordWrap("You " + currentRoom.getDescription()));
        System.out.println(TxtFormat.wordWrap("================================================================="));
        System.out.println(TxtFormat.wordWrap("Possible directions you can go:"));
        currentRoom.getExits().forEach((key, value) -> System.out.println(TxtFormat.wordWrap(ANSI_WHITE +
                key + ANSI_RESET + ": " + ANSI_GREEN + value + ANSI_RESET)));
        if (!currentRoom.getInventory().isEmpty()) {
            System.out.println(TxtFormat.wordWrap("You see the following items room: "));
            currentRoom.getInventory().forEach((item) -> System.out.println(TxtFormat.wordWrap(ANSI_RED +
                    item.getName() + ANSI_RESET)));
        }
        if (!currentRoom.getNpcs().isEmpty()) {
            System.out.println(TxtFormat.wordWrap("You see the following people in the room: "));
            currentRoom.getNpcs().forEach((npc) -> System.out.println(TxtFormat.wordWrap(ANSI_YELLOW +
                    npc.getName() + ANSI_RESET)));
        }
        if (!player.getInventory().isEmpty()) {
            System.out.println(TxtFormat.wordWrap("Your inventory: "));
            player.getInventory().forEach((item) -> System.out.println(TxtFormat.wordWrap(ANSI_RED +
                    item.getName() + ANSI_RESET)));
        }
    }

    private void displayISS(String roomName, Player player) {

        String[] issLines = iss.getHiddenMap(roomName.toLowerCase(), player.getRoomsVisited());
        System.out.println("\n");
        for(String line : issLines) {
            System.out.println(INDENT + line);
        }
    }

    private Map<String, Room> setUpMap() {
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

    private Boolean newGame() {
        Console.clear();
        System.out.println("\n\n" + INDENT + "For a new game enter new game, to restore a game enter restore game, to quit enter quit.");
        System.out.print(INDENT + "New Game or Restore Game --> ");
        String answer = scanner.nextLine();
        Boolean valid = false;
        while (Boolean.FALSE.equals(valid)) {
            if ("new game".equalsIgnoreCase(answer)) {
                valid = true;
            } else if ("restore game".equalsIgnoreCase(answer)) {
                break; }
            else if ("quit".equalsIgnoreCase(answer)) {
                valid = null;
                break;
            } else {
                System.out.print(INDENT + "Invalid Input. Please enter New Game, Restore Game or Quit: ");
                answer = scanner.nextLine();
            }
        }
        return valid;
    }

    private void instructions() {
        Console.clear();
        try {
            // read the entire file as a string
            String contents = Resource.read("/instructions.txt");
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
            String contents = Resource.read("/basicInfo.txt");
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
            String titleScreen = Resource.read("/titleScreen.txt");
            System.out.println(titleScreen);
        } catch (IOException e) {
            e.printStackTrace();
        }
        enterToContinue();
    }


    private void enterToContinue() {
        System.out.print(INDENT + "Press enter to continue...");
        // Wait for the user to press enter
        scanner.nextLine();
    }
}

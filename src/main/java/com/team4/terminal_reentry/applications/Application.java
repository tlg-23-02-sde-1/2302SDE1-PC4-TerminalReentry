package com.team4.terminal_reentry.applications;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.team4.terminal_reentry.items.Item;
import com.team4.terminal_reentry.setup.Player;
import com.team4.terminal_reentry.setup.Room;
import com.team4.terminal_reentry.setup.Scenario;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Application {

    public static final String INDENT = "\t\t";
    private final Scanner scanner = new Scanner(System.in);
    private final TextParser textParser = new TextParser();

    private String resourcePath = "src/main/resources/";

    private List<String> winCondition = new ArrayList<>();
    private Thread musicThread;
    private Music source;
    private MidiPlayer midiPlayer;

    public Application() {
        source = new Music();
        midiPlayer = new MidiPlayer(source.getMidiBytes());
    }

    public void run() {
        midiPlayer.playMusicThread(1);
        Map<String, Room> map = null;
        Player player = null;
        Room currentRoom;
        titleScreen();
        if (newGame()) {
            basicInfo();
            instructions();
            map = setUpMap();
            currentRoom = map.get("Harmony");
            player = new Player(currentRoom);
        }
        else if(savedGame()) {
            JsonObject jObject = readInSavedFile("saved1.json");
            player = loadSavedPlayer(jObject);
            map = loadSavedMap(jObject.get("map").getAsJsonObject());
        }
        else {
            stopMusic();
            midiPlayer.stop();
            return;
        }
        Controller controller = new Controller(map, player, winCondition, midiPlayer);
        boolean quit = false;
        do {
            displayScreen(player.getCurrentRoom(), player);
            String command = promptForCommand();
            String[] response = textParser.handleInput(command);
            if (response[0].equals("200")) {
                quit = controller.execute(response);
            }
        }
        while (!quit);
        stopMusic();
    }

    private void stopMusic() {
        if (musicThread != null && musicThread.isAlive()) {
            midiPlayer.stop();
            try {
                musicThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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

        return new Player(loadedCurrentRoom, inventory, inspectedItem, npcMet, roomsVisited);
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

        displayISS(currentRoom.getName());
        System.out.println("\n\t\tYou are currently in the " + ANSI_GREEN + currentRoom.getName() + ANSI_RESET + " module.");
        System.out.println(INDENT + "You " + currentRoom.getDescription());
        System.out.println(INDENT + "===================================================================================");
        System.out.println(INDENT + "Possible directions you can go:");
        currentRoom.getExits().forEach((key, value) -> System.out.println(INDENT + ANSI_WHITE + key + ANSI_RESET + ": "
                + ANSI_GREEN + value + ANSI_RESET));
        if (!currentRoom.getInventory().isEmpty()) {
            System.out.println(INDENT + "You see the following items room: ");
            currentRoom.getInventory().forEach((item) -> System.out.println(INDENT + ANSI_RED + item.getName() + ANSI_RESET));
        }
        if (!currentRoom.getNpcs().isEmpty()) {
            System.out.println(INDENT + "You see the following people in the room: ");
            currentRoom.getNpcs().forEach((npc) -> System.out.println(INDENT + ANSI_YELLOW + npc.getName() + ANSI_RESET));
        }
        if (!player.getInventory().isEmpty()) {
            System.out.println(INDENT + "Your inventory: ");
            player.getInventory().forEach((item) -> System.out.println(INDENT + ANSI_RED + item.getName() + ANSI_RESET));
        }
    }

    private void displayISS(String roomName) {
        String ANSI_GOLD = "\u001B[38;5;220m";
        String ANSI_RESET = "\u001B[0m";

        try {
            String path = "/locations/" + roomName + ".txt";
            // read the entire file as a string
            String contents = readResource(path);
            contents = INDENT + contents;
            contents = contents.replaceAll("\n", "\n" + INDENT);
            contents = contents.replace("\u00A7", ANSI_GOLD + "\u00A7" + ANSI_RESET);
            contents = "\n\n" + contents;
            System.out.println(contents);
        } catch (IOException e) {
            e.printStackTrace();
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

    private boolean newGame() {
        Console.clear();
        System.out.print("\n\n" + INDENT + "New Game --> [Y,N] : ");
        String answer = scanner.nextLine();
        boolean valid = false;
        while (!valid) {
            if (answer.toLowerCase().equals("y")) {
                valid = true;
            } else if (answer.toLowerCase().equals("n")) {
                break;
            } else {
                System.out.println(INDENT + "Invalid Input. Please enter y or n: ");
                answer = scanner.nextLine();
            }
        }
        return valid;
    }

    private boolean savedGame() {
        System.out.print("\n\n" + INDENT + "Load saved game --> [Y,N]: ");
        String answer = scanner.nextLine();
        boolean valid = false;
        while (!valid) {
            if (answer.toLowerCase().equals("y")) {
                valid = true;
            } else if (answer.toLowerCase().equals("n")) {
                break;
            } else {
                System.out.println(INDENT + "Invalid Input. Please enter y or n: ");
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
        System.out.print(INDENT + "Press enter to continue...");
        // Wait for the user to press enter
        scanner.nextLine();
    }
}
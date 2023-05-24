package com.team4.terminal_reentry.applications;


import com.google.gson.reflect.TypeToken;
import com.team4.terminal_reentry.items.Evidence;
import com.team4.terminal_reentry.items.Item;
import com.team4.terminal_reentry.items.Weapon;
import com.team4.terminal_reentry.setup.NPC;
import com.team4.terminal_reentry.setup.Player;
import com.team4.terminal_reentry.setup.Room;
import com.team4.terminal_reentry.setup.Scenario;
import com.google.gson.*;

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
        midiPlayer.playMusic(1);


        titleScreen();
        if (newGame()) {
            basicInfo();
            instructions();
            Map<String, Room> map = setUpMap();
            Room currentRoom = map.get("Harmony");
            Player player = new Player(currentRoom);
            Controller controller = new Controller(map, player, winCondition, midiPlayer);
            boolean quit = false;
            do {
                displayScreen(player.getCurrentRoom(), player);
                String command = promptForCommand();
                String[] response = textParser.handleInput(command);
                if (response[0].equals("200")) {
                    quit = controller.execute(response);
                }
//                enterToContinue();
            }
            while (!quit);//textParser.handleInput(scanner.nextLine())[0]);
            if (musicThread != null && musicThread.isAlive()) {
                midiPlayer.stop();
                try {
                    musicThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (savedGame()) {
            try (BufferedReader reader = new BufferedReader(new FileReader("saved1.json"))) {
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                String fileContent = sb.toString();
                JsonObject jObject;
                jObject = JsonParser.parseString(fileContent).getAsJsonObject();
                List<Object> mapAndPlayer = setUpSavedGame(jObject);
                Player player = (Player) mapAndPlayer.get(1);
                Map<String, Room> map = loadSavedMap(jObject.get("map").getAsJsonObject());
                Controller controller = new Controller(map,player,winCondition,midiPlayer);
                boolean quit = false;
                do {
                    displayScreen(player.getCurrentRoom(), player);
                    String command = promptForCommand();
                    String[] response = textParser.handleInput(command);
                    if (response[0].equals("200")) {
                        quit = controller.execute(response);
                    }
//                enterToContinue();
                }
                while (!quit);//textParser.handleInput(scanner.nextLine())[0]);
                if (musicThread != null && musicThread.isAlive()) {
                    midiPlayer.stop();
                    try {
                        musicThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Object> setUpSavedGame(JsonObject jsonObject) {
        //List to be returned
        List<Object> initGame = new ArrayList<>();

        //initialize the Set<String> for roomsVisited
        Set<String> roomsVisited = new HashSet<>();
        JsonArray rooms = jsonObject.get("roomsVisited").getAsJsonArray();
        for(JsonElement j:rooms) {
            String room = j.toString();
            roomsVisited.add(room);
        }

        //initialize the Set<String> for npcMet
        Set<String> npcMet = new HashSet<>();
        JsonArray npcs = jsonObject.get("npcMet").getAsJsonArray();
        for(JsonElement j:npcs) {
            String npc = j.toString();
            npcMet.add(npc);
        }

        //initialize the List<String> for inventory
        List<Item> inventory = new ArrayList<>();
        JsonArray jinvent = jsonObject.get("inventory").getAsJsonArray();
        List<String> clues = new ArrayList<>(Arrays.asList("Postit Note","Email", "Log File","Diary"));
        for(JsonElement j: jinvent) {
            Item item;
            JsonObject jObject = j.getAsJsonObject();
            String itemName = jObject.get("name").toString().replace("\"","");
            String itemDescription = jObject.get("description").toString().replace("\"","");
            boolean isAssociated = jObject.get("isEvidence").getAsBoolean();
            String data = jObject.get("data").toString().replace("\"","");
            String secret = jObject.get("secret").toString().replace("\"","");
            if (clues.contains(itemName)) {
                //init evidence
                item = new Evidence(itemName,itemDescription,isAssociated,data,secret);
            }
            else {
                //init weapon
                item = new Weapon(itemName,itemDescription,isAssociated,data,secret);
            }
            inventory.add(item);
        }

        //json object to map convert helpers
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {}.getType();

        //initialize currentLocation Room
        JsonObject currentRoom = jsonObject.get("currentLocation").getAsJsonObject();
        String roomName = currentRoom.get("name").toString().replace("\"","");
        String description = currentRoom.get("description").toString().replace("\"","");
        JsonObject directions = currentRoom.get("exits").getAsJsonObject();
        Map<String,String> exits = new HashMap<>();
        exits = gson.fromJson(directions,type);
        JsonArray jRoomInventory = currentRoom.get("inventory").getAsJsonArray();
        List<Item> roomInventory = new ArrayList<>();
        for(JsonElement j: jRoomInventory) {
            Item item;
            JsonObject jObject = j.getAsJsonObject();
            String itemName = jObject.get("name").toString().replace("\"","");
            String itemDescription = jObject.get("description").toString().replace("\"","");
            boolean isAssociated = jObject.get("isEvidence").getAsBoolean();
            String data = jObject.get("data").toString().replace("\"","");
            String secret = jObject.get("secret").toString().replace("\"","");
            if (clues.contains(itemName)) {
                //init evidence
                item = new Evidence(itemName,itemDescription,isAssociated,data,secret);
            }
            else {
                //init weapon
                item = new Weapon(itemName,itemDescription,isAssociated,data,secret);
            }
            roomInventory.add(item);
        }
        JsonArray room_Npcs = currentRoom.get("characters").getAsJsonArray();
        List<NPC> roomNpcs = new ArrayList<>();
        for(JsonElement npc: room_Npcs) {
            if(npc.isJsonObject()) {
                JsonObject jnpc = npc.getAsJsonObject();
                String firstName = jnpc.get("firstName").toString().replace("\"","");
                String lastName = jnpc.get("lastName").toString().replace("\"","");
                String nationality = jnpc.get("nationality").toString().replace("\"","");
                String pronoun = jnpc.get("pronoun").toString().replace("\"","");
                boolean isMurderer = jnpc.get("isMurderer").getAsBoolean();
                JsonObject jAnswers = jnpc.get("answers").getAsJsonObject();
                Map<String,String> answers = new HashMap<>();
                answers = gson.fromJson(jAnswers,type);
                NPC newNPC = new NPC(firstName,lastName,nationality,pronoun,isMurderer,answers);
                roomNpcs.add(newNPC);
            }
        }

        Room loadedCurrentRoom = new Room(roomName,description,roomInventory,roomNpcs,exits);

        //initialize the Map<String,String> for inspectedItem
        Map<String,String> inspectedItem = new HashMap<>();
        JsonObject items = jsonObject.get("inspectedItem").getAsJsonObject();
        inspectedItem = gson.fromJson(items, type);

        Map<String, Room> map = loadSavedMap(jsonObject.get("map").getAsJsonObject());
        Player player = new Player(loadedCurrentRoom,inventory, inspectedItem,npcMet,roomsVisited);
        initGame.add(map);
        initGame.add(player);
        return initGame;
    }

    private Map<String,Room> loadSavedMap(JsonObject mapData) {

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Room>>() {}.getType();
        //THIS IS ALL I NEEDED TO DO HOLY...
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
        System.out.print(INDENT + "New Game --> [Y,N] : ");
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
        System.out.print(INDENT + "Load saved game --> [Y,N]: ");
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
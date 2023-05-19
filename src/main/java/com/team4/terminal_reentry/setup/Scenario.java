package com.team4.terminal_reentry.setup;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.*;
import com.team4.terminal_reentry.applications.Application;
import com.team4.terminal_reentry.items.Item;
import com.team4.terminal_reentry.items.Weapon;

public class Scenario {
    private final Map<String, Room> map;
    private final List<String> winCondition;
    private final String resourcePath;

    public Scenario() throws FileNotFoundException {
        this.map = new HashMap<>();
        this.winCondition = null;
        this.resourcePath = "/";
        try {
            setUp();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setUp() throws IOException {

        //read in weapons
        String weaponsJson = resourcePath + "weapons.json";
        List<Item> weapons = getWeapons(loadJson(weaponsJson));

        // TODO: load in data items (notes, emails, logs, etc.)
        String clueItemsJson = resourcePath + "clueItems.json";
        List<Item> clues = getClues(loadJson(clueItemsJson));

        //read in npcs
        String npcsJson = resourcePath + "npcs.json";
        List<NPC> npcs = getNPCs(loadJson(npcsJson));

        //load map
        String mapJson = resourcePath + "map.json";
        try {
            setMap(loadJson(mapJson), weapons, npcs);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Item> getClues(JsonArray loadJson) {
        return null;
    }

    private List<Item> getWeapons(JsonArray weaponsData) {
        List<Item> weapons = new ArrayList<>();
        Random rand = new Random();
        int murderWeapon = rand.nextInt(weaponsData.size());
        for(int i = 0; i < weaponsData.size(); i++) {
            JsonObject weapon = weaponsData.get(i).getAsJsonObject();
            weapons.add(new Weapon(
                    weapon.get("title").toString().replace("\"",""),
                    weapon.get("description").toString().replace("\"",""),
                    i == murderWeapon,
                    weapon.get("data").toString().replace("\"",""),
                    weapon.get("secretData").toString().replace("\"","")
            ));
        }
        return weapons;
    }

    private List<NPC> getNPCs(JsonArray npcData) {
        List<NPC> npcs = new ArrayList<>();
        Random rand = new Random();
        int murderer = rand.nextInt(npcData.size());
        String[] answers = {"locationAtTimeOfMurder", "activityAtTimeOfMurder", "opinionOfVictim", "otherTestimony"};
        for(int i = 0; i < npcData.size(); i++) {
            Map<String, String> dialogue = new HashMap<>();
            JsonObject npc = npcData.get(i).getAsJsonObject();
            for(String answer: answers) {
                dialogue.put(answer, npc.get(answer).toString().replace("\"",""));
            }
            npcs.add(new NPC(
                    npc.get("name").toString().replace("\"",""),
                    npc.get("nationality").toString().replace("\"",""),
                    npc.get("pronoun").toString().replace("\"",""),
                    i == murderer,
                    dialogue
            ));
        }
        return npcs;
    }

    private JsonArray loadJson(String filePath) throws IOException {
        String contents = readResource(filePath);
        return (JsonArray) JsonParser.parseString(contents);
    }


    private void setItems(JsonElement itemJson) {
        //TODO: implement initializing Item
    }

    private int[] getRandomPlacement(int sourceSize, int destSize) {
        Random rand = new Random();
        int[] placementArray = new int[sourceSize];
        for(int i = 0; i < sourceSize; i++) {
            placementArray[i] = rand.nextInt(destSize);
        }
        return placementArray;
    }


    private void setMap(JsonElement mapJson, List<Item> weapons, List<NPC> npcs) {
        JsonArray issJson = mapJson.getAsJsonArray();
        int[] itemPlacementNumbers = getRandomPlacement(weapons.size(), issJson.size());
        int[] npcPlacement = getRandomPlacement(npcs.size(), issJson.size());
        for (int i = 0; i < issJson.size(); i++) {
            List<Item> items = new ArrayList<>();
            List<NPC> npcInRoom = new ArrayList<>();
            for (int j = 0; j < weapons.size(); j++) {
                if(itemPlacementNumbers[j] == i) {
                    items.add(weapons.get(j));
                }
            }
            for (int j = 0; j < npcs.size(); j++) {
                if(npcPlacement[j] == i) {
                    npcInRoom.add(npcs.get(j));
                }
            }
            Map<String, String> exits = new HashMap<>();

            JsonObject room = issJson.get(i).getAsJsonObject();
            JsonObject exitsJson = room.get("exits").getAsJsonObject();
            String[] directions = {"left", "right", "up", "down"};
            for (String direction: directions) {
                if(exitsJson.get(direction) != null) {
                    exits.put(direction, exitsJson.get(direction).toString().replace("\"",""));
                }
            }
            String roomName = room.get("name").toString().replace("\"","");
            map.put(roomName, new Room(
                    roomName,
                    room.get("description").toString().replace("\"",""),
                    items,
                    npcInRoom,
                    exits
            ));
        }

    }

    public Map<String, Room> getMap() {
        return map;
    }

    public List<String> getWinCondition() {
        return winCondition;
    }

    private static String readResource(String path) throws IOException {
        try (InputStream is = Scenario.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
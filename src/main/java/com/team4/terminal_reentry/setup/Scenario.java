package com.team4.terminal_reentry.setup;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.*;
import com.team4.terminal_reentry.items.Item;
import com.team4.terminal_reentry.items.Weapon;

class Scenario {
    private Map<String, Room> map;
    private List<String> winCondition;

    Scenario() {
        this.map = new HashMap<>();
        this.winCondition = null;
//        setUp();
    }

    private void setUp() throws FileNotFoundException {

        //read in weapons
        String weaponsJson = "./src/main/resources/weapons.json";
        List<Item> weapons = getWeapons(loadJson(weaponsJson));

        // TODO: load in data items (notes, emails, logs, etc.)
        String clueItemsJson = "./src/main/resources/clueItems.json";
        List<Item> clues = getClues(loadJson(clueItemsJson));

        //read in npcs
        String npcsJson = "./src/main/resources/npcs.json";
        List<NPC> npcs = getNPCs(loadJson(npcsJson));

        //load map
        String mapJson = "./src/main/resources/map.json";
        try {
            setMap(loadJson(mapJson), weapons, npcs);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    private List<Item> getClues(JsonArray loadJson) {
    }

    private List<Item> getWeapons(JsonArray weaponsData) {
        List<Item> weapons = new ArrayList<>();
        Random rand = new Random();
        int murderWeapon = rand.nextInt(weaponsData.size());
        for(int i = 0; i < weaponsData.size(); i++) {
            JsonObject weapon = weaponsData.get(i).getAsJsonObject();
            weapons.add(new Weapon(
                    weapon.get("title").toString(),
                    weapon.get("description").toString(),
                    i == murderWeapon,
                    weapon.get("data").toString(),
                    weapon.get("secretData").toString()
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
                dialogue.put(answer, npc.get(answer).toString());
            }
            npcs.add(new NPC(
                    npc.get("name").toString(),
                    npc.get("nationality").toString(),
                    npc.get("pronoun").toString(),
                    i == murderer,
                    dialogue
            ));
        }
        return npcs;
    }

    private JsonArray loadJson(String filePath) throws FileNotFoundException {
        Reader reader = new FileReader(filePath);
        return (JsonArray) JsonParser.parseReader(reader);
    }

    private void setNpcs(JsonElement npcsJson) {
        //TODO: implement initializing NPCs
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

<<<<<<< Updated upstream
<<<<<<< Updated upstream
    public void setMap(JsonElement mapJson, List<Weapon> weapons, List<NPC> npcs) {
//        Random rand = new Random();
=======
    private void setMap(JsonElement mapJson, List<Weapon> weapons, List<NPC> npcs) {
>>>>>>> Stashed changes
=======
    private void setMap(JsonElement mapJson, List<Weapon> weapons, List<NPC> npcs) {
>>>>>>> Stashed changes
        JsonArray issJson = mapJson.getAsJsonArray();
//        int[] itemPlacementNumbers = new int[weapons.size()];
//        for (int i = 0; i < itemPlacementNumbers.length; i++) {
//            itemPlacementNumbers[i] = rand.nextInt(issJson.size());
//        }
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
                    exits.put(direction, exitsJson.get(direction).toString());
                }
            }
            String roomName = room.get("name").toString();
            map.put(roomName, new Room(
                    roomName,
                    room.get("description").toString(),
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
}
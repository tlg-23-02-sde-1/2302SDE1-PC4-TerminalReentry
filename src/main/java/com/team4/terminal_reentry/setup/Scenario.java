package com.team4.terminal_reentry.setup;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.*;
import com.team4.terminal_reentry.applications.Application;
import com.team4.terminal_reentry.items.Evidence;
import com.team4.terminal_reentry.items.Item;
import com.team4.terminal_reentry.items.Weapon;

import javax.swing.*;

public class Scenario {
    private final Map<String, Room> map;
    private final List<String> winCondition;
    private final String resourcePath;
    private String murderer;
    private String murdererFirstName;
    private String murderWeapon;
    private String sceneOfCrime;

    public Scenario() throws FileNotFoundException {
        this.map = new HashMap<>();

        this.winCondition = new ArrayList<>();
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

        //read in npcs
        String npcsJson = resourcePath + "npcs.json";
        List<NPC> npcs = getNPCs(loadJson(npcsJson));

        // TODO: load in data items (notes, emails, logs, etc.)
        String clueItemsJson = resourcePath + "clueItems.json";
        List<Item> clues = getClues(loadJson(clueItemsJson));

        List<Item> weaponsAndClues = Stream.concat(weapons.stream(), clues.stream()).collect(Collectors.toList());

        //load map
        String mapJson = resourcePath + "map.json";
        try {
            setMap(loadJson(mapJson), weaponsAndClues, npcs);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        //set win condition
        setWinCondition(npcs);
    }

    private void setWinCondition(List<NPC> npcs) {

        for(int i = 0; i < npcs.size(); i++) {
//        for(NPC npc:npcs) {
            boolean killer = false;
            String scapegoat = "Frank";
            if(npcs.get(i).isMurderer()){
                killer = true;
                scapegoat = npcs.get(npcs.size() - i).getName();
            }
            npcs.get(i).getAnswers().put("otherTestimony", npcs.get(i).getAnswers()
                    .get("otherTestimony")
                    .replace("<murderer>",killer ? scapegoat : murdererFirstName)
                    .replace("<sceneOfCrime>",sceneOfCrime));
        }

        winCondition.add(murderer);       //add murderer
        winCondition.add(murderWeapon);  //add murder weapon
        winCondition.add(sceneOfCrime); //add location
    }

    private List<Item> getClues(JsonArray cluesData) {
        List<Item> clues = new ArrayList<>();
        Random rand = new Random();
        int murderClue = rand.nextInt(cluesData.size());
        for (int i = 0; i < cluesData.size(); i++) {
            JsonObject clue = cluesData.get(i).getAsJsonObject();
            clues.add(new Evidence(
                    clue.get("title").toString().replace("\"", ""),
                    clue.get("description").toString().replace("\"", ""),
                    i == murderClue,
                    clue.get("data").toString().replace("\"", "").replace("<murderer>", murdererFirstName),
                    clue.get("secretData").toString().replace("\"", "").replace("<murderer>", murdererFirstName)
            ));
        }
        return clues;
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
            if( i == murderWeapon) {
                this.murderWeapon = weapon.get("title").toString().replace("\"","");
            }
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
                    npc.get("firstName").toString().replace("\"",""),
                    npc.get("lastName").toString().replace("\"", ""),
                    npc.get("nationality").toString().replace("\"",""),
                    npc.get("pronoun").toString().replace("\"",""),
                    i == murderer,
                    dialogue
            ));
            if (i == murderer) {
                this.murderer = npcs.get(i).getName();
                this.murdererFirstName = npcs.get(i).getFirstName();
            }
        }
        return npcs;
    }

    private JsonArray loadJson(String filePath) throws IOException {
        String contents = readResource(filePath);
        return (JsonArray) JsonParser.parseString(contents);
    }


    private int[] getRandomPlacement(int sourceSize, int destSize) {
        Random rand = new Random();
        int[] placementArray = new int[sourceSize];
        for(int i = 0; i < sourceSize; i++) {
            placementArray[i] = rand.nextInt(destSize);
        }
        return placementArray;
    }


    private void setMap(JsonElement mapJson, List<Item> weaponsAndClues, List<NPC> npcs) {
        JsonArray issJson = mapJson.getAsJsonArray();
        Random rand = new Random();
        int crimeScene = rand.nextInt(issJson.size());
        int[] itemPlacementNumbers = getRandomPlacement(weaponsAndClues.size(), issJson.size());
        int[] npcPlacement = getRandomPlacement(npcs.size(), issJson.size());
        for (int i = 0; i < issJson.size(); i++) {
            List<Item> items = new ArrayList<>();
            List<NPC> npcInRoom = new ArrayList<>();
            for (int j = 0; j < weaponsAndClues.size(); j++) {
                if(itemPlacementNumbers[j] == i) {
                    items.add(weaponsAndClues.get(j));
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
            if(i == crimeScene) {
                this.sceneOfCrime = roomName;
            }
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
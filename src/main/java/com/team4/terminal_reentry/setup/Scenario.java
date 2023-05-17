package com.team4.terminal_reentry.setup;

import java.io.*;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

class Scenario {
    private Map<String, Room> map;
    private List<String> winCondition;

    Scenario() {
        setUp();
    }

    private void setUp() {
        //read map
        String filePath = "";
        try (Reader reader = new FileReader(filePath)) {
            JsonElement mapJson = JsonParser.parseReader(reader);
            //set up the map
            setMap(mapJson);
        }
        catch (IOException e) {
            System.out.println("Couldn't load map");
        }

        //read in items
        filePath = "";
        try (Reader reader = new FileReader(filePath)) {
            JsonElement itemJson = JsonParser.parseReader(reader);
            //set up the items in the map
            setItems(itemJson);
        }
        catch (IOException e) {
            System.out.println("Couldn't load map");
        }

        //read in npcs
        filePath = "";
        try (Reader reader = new FileReader(filePath)) {
            JsonElement npcsJson = JsonParser.parseReader(reader);
            //set up npcs in the map
            setNpcs(npcsJson);
        }
        catch (IOException e) {
            System.out.println("Couldn't load map");
        }
    }

    private void setNpcs(JsonElement npcsJson) {
        //TODO: implement initializing NPCs
    }

    private void setItems(JsonElement itemJson) {
        //TODO: implement initializing Item
    }

    private void setMap(JsonElement mapJson) {
        //TODO: implement initializing Rooms
    }

    public Map<String, Room> getMap() {
        return map;
    }

    public List<String> getWinCondition() {
        return winCondition;
    }
}
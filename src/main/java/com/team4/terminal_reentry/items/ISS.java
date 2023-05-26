package com.team4.terminal_reentry.items;

import com.team4.terminal_reentry.setup.Resource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;


public class ISS {

    private final String ANSI_GOLD = "\u001B[38;5;220m";
    private final String ANSI_RESET = "\u001B[0m";

    private final String[] baseMap;
    private String[] playerMap;
    private final String path = "/MapInfo/map.txt";
    private final Map<String, int[]> playerLocations;
    private final Map<String, Map<String, Integer>> moduleBounds;
    private final String player = ANSI_GOLD + "\u00A7" + ANSI_RESET;

    public ISS() {
        baseMap = getBaseMap(path);
        playerMap = getBaseMap(path);
        playerLocations = loadPlayerLocations("MapInfo/ModulePlayerLocations.json");
        moduleBounds = loadModuleBounds("MapInfo/ModuleBounds.json");
    }

    private Map<String, Map<String, Integer>> loadModuleBounds(String path) {
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, Map<String, Integer>>>(){}.getType();
        Map<String, Map<String, Integer>> moduleBounds = new HashMap<>();
        InputStream locationsStream = getClass().getClassLoader().getResourceAsStream(path);
        if (locationsStream == null) {
            try {
                throw new FileNotFoundException("Required resources not found");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        moduleBounds = gson.fromJson(new InputStreamReader(locationsStream), mapType);
        return moduleBounds;
    }

    private Map<String, int[]> loadPlayerLocations(String path) {
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String,int[]>>(){}.getType();
        Map<String, int[]> playerLocations = new HashMap<>();
        InputStream locationsStream = getClass().getClassLoader().getResourceAsStream(path);
        if (locationsStream == null) {
            try {
                throw new FileNotFoundException("Required resources not found");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        playerLocations = gson.fromJson(new InputStreamReader(locationsStream), mapType);
        return playerLocations;
    }

    private String[] getBaseMap(String path) {
        String contents;
        try {
            contents = Resource.read(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] baseMap = contents.split("\n");
        return baseMap;
    }

    public String[] getIssMap() {
        return baseMap;
    }

    public String[] getMap(String currentRoom) {
        playerMap = getBaseMap(path);
        int row = playerLocations.get(currentRoom)[0];
        int column = playerLocations.get(currentRoom)[1];
            String temp = playerMap[row];
            playerMap[row] = temp.substring(0, column) + player + temp.substring(column + 1);
        return playerMap;
    }

    public String[] getHiddenMap(String currentRoom, Set<String> visitedRooms) {
        playerMap = coverMap(currentRoom, visitedRooms);
        int row = playerLocations.get(currentRoom)[0];
        int column = playerLocations.get(currentRoom)[1];
        String temp = playerMap[row];
        playerMap[row] = temp.substring(0, column) + player + temp.substring(column + 1);
        return playerMap;
    }
    public String[] coverMap(String currentRoom, Set<String> visitedRooms) {
        String[] coveredMap = getBaseMap(path);

        for(int i = 1; i < coveredMap.length - 1; i++){
            List<String> hittingRooms = visibleRows(i, visitedRooms);
            if(!hittingRooms.isEmpty()) {
                char[] line = coveredMap[i].toCharArray();
                for (int j = 1; j < line.length - 1; j++) {
                    if (visibleColumns(j, hittingRooms)) {
                    } else {
                        line[j] = ' ';
                    }
                }
                coveredMap[i] = new String(line);
            } else {
                coveredMap[i] = "*                                                               *";
            }
        }
        return coveredMap;
    }

    private List<String> visibleRows(int i, Set<String> visitedRooms) {
        List<String> hittingRooms = new ArrayList<>();
        for(String module: visitedRooms){
            Map<String, Integer> visibleMod = moduleBounds.get(module.toLowerCase());
            if((i >= visibleMod.get("top") && i <= visibleMod.get("bottom"))){
                hittingRooms.add(module);
            }
        }
        return hittingRooms;
    }

    private boolean visibleColumns(int j, List<String> hittingRooms) {
        boolean result = false;
        for(String module: hittingRooms){
            Map<String, Integer> visibleMod = moduleBounds.get(module.toLowerCase());
            if((j >= visibleMod.get("left") && j <= visibleMod.get("right"))){
                result = true;
            }
        }
        return result;
    }
}
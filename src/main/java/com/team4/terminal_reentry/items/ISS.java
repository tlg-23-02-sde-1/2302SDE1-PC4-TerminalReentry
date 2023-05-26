package com.team4.terminal_reentry.items;

import com.team4.terminal_reentry.setup.Resource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ISS {

    private final String ANSI_GOLD = "\u001B[38;5;220m";
    private final String ANSI_RESET = "\u001B[0m";

    private final String[] baseMap;
    private String[] playerMap;
    private final String path = "/MapInfo/map.txt";
    private final Map<String, int[]> playerLocations;
    private final String player = ANSI_GOLD + "§" + ANSI_RESET;

    public ISS() {
        baseMap = getBaseMap(path);
        playerMap = getBaseMap(path);
        playerLocations = loadPlayerLocations("MapInfo/ModulePlayerLocations.json");
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

//    contents =contents.replaceAll("\n","\n"+INDENT);
//    contents =contents.replace("\u00A7",ANSI_GOLD +"\u00A7"+ANSI_RESET);
//    contents ="\n\n"+contents;
}
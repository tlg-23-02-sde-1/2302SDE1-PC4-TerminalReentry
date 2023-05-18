package com.team4.terminal_reentry.setup;

import com.team4.terminal_reentry.items.Weapon;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioTest {
    static Scenario game = null;
    static List<Weapon> weapons = null;
    static List<NPC> npcs = null;

    @BeforeAll
    public static void initialize() {
        game = new Scenario();
        String weaponsJson = "./src/main/resources/weapons.json";
        String npcsJson = "./src/main/resources/npcs.json";
        try {
            weapons = game.getWeapons(game.loadJson(weaponsJson));
            npcs = game.getNPCs(game.loadJson(npcsJson));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getWeapons() {

        for (Weapon weapon: weapons
             ) {
            System.out.println(weapon.getSecret(true));
        }
    }

    @Test
    void setMap_shouldInitializeGameMap() {
        String mapJson = "./src/main/resources/map.json";
        try {
            game.setMap(game.loadJson(mapJson), weapons, npcs);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        for(Map.Entry<String, Room> entry : game.getMap().entrySet()) {
            Room room = entry.getValue();
            System.out.println(room.toString());
        }
    }
}
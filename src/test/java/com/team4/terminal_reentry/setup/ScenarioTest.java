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
        try {
            game = new Scenario();
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

        for(Map.Entry<String, Room> entry : game.getMap().entrySet()) {
            Room room = entry.getValue();
            System.out.println(room.toString());
        }
    }
}
package com.team4.terminal_reentry.items;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ISSTest {
ISS iss;
    @BeforeEach
    void setUp() {
        iss = new ISS();
    }

    @Test
    void getIssMap() {
        String[] map = iss.getIssMap();
        String line4 = "*  ╚═╗  ╔═╝ ╔══════════╗╚══╗ ╔═╝ ╔══════════╗  ╔═╝  ╚════════╗  *";
        assertEquals(line4, map[4]);
    }

    @Test
    void getMap_shouldReturnMapWithCharacter_harmony() {
        String line6 = "*  ]   \u001B[38;5;220m§\u001B[0m               ╔╗       ╔╗          ╔═╗              ╔  *";
        String[] map = iss.getMap("harmony");
        assertEquals(line6, map[7]);
    }

    @Test
    void getMap_shouldReturnMapWithCharacter_Columbus() {
        String line2 = "*  ║  \u001B[38;5;220m§\u001B[0m   ║             ╔══╝ ╚═╗               ╚═╗  ╔╝          *";
        String[] map = iss.getMap("columbus");
        assertEquals(line2, map[3]);
    }

    @Test
    void getMap_shouldReturnMapWithCharacter_unity() {
        String line6 = "*  ]                   ╔╗   \u001B[38;5;220m§\u001B[0m   ╔╗          ╔═╗              ╔  *";
        String[] map = iss.getMap("unity");
        assertEquals(line6, map[7]);
        System.out.println(iss.getIssMap()[7]);
    }



    @Test
    void getHiddenMap() {
        Set<String> visitedRooms = new HashSet<>();//"zarya", "unity", "tranquility", "zvezda"
        visitedRooms.add("zarya");
        visitedRooms.add("unity");
        visitedRooms.add("tranquility");
        visitedRooms.add("zvezda");
        String[] map = iss.getHiddenMap("zvezda", visitedRooms);
        for(String line: map){
            System.out.println(line);
        }
    }
}
package com.team4.terminal_reentry.items;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    }
}
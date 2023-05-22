package com.team4.terminal_reentry.applications;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MusicTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void midiBytes_shouldGetSomething() {
        Music tunes = new Music();
        String url = "https://www.vgmusic.com/music/console/nintendo/nes/zeldaund.mid";
        assertEquals(8015, tunes.setMidiBytes(url));
    }

    @AfterEach
    void tearDown() {
    }
}
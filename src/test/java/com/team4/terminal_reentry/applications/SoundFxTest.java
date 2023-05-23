package com.team4.terminal_reentry.applications;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SoundFxTest {
    SoundFx soundFx;

    @BeforeEach
    void setUp() {
        soundFx = new SoundFx();
    }

    @Test
    void getItem() {
        soundFx.getItem1();
    }

    @Test
    void testURL() {
        String url = "https://www.vgmusic.com/music/console/nintendo/nes/KnightMan.mid";
        soundFx = new SoundFx(url);
        soundFx.playTest();
    }
}
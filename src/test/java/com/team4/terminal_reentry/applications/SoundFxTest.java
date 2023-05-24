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
        soundFx = new SoundFx();
        soundFx.playTest();
    }
}
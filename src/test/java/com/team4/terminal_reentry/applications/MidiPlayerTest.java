package com.team4.terminal_reentry.applications;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MidiPlayerTest {
    Music tunes;

    @BeforeEach
    void setUp() {
        tunes = new Music("https://www.vgmusic.com/music/console/nintendo/nes/smb-1up.mid");
    }

    @Test
    void playMusic() {
//        MidiPlayer myMusic = new MidiPlayer(tunes.getMidiBytes());
//        myMusic.playMusic(0);
    }
}
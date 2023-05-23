package com.team4.terminal_reentry.applications;

import javax.sound.midi.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

class MidiPlayer {
    private byte[] midiBytes = null;
    private Sequencer sequencer;

    public MidiPlayer(byte[] midiBytes) {
        this.midiBytes = midiBytes;
    }

    public void playMusic() {
        // Create a sequencer and open it
        // Create a sequence from the MIDI bytes
        // Set the sequence in the sequencer
        // Set loop count
        // Start playing the MIDI music
        // Wait until the sequencer finishes playing
        Thread musicThread = new Thread(() -> {
            // Create a sequencer and open it
            try {
                sequencer = MidiSystem.getSequencer();
            } catch (MidiUnavailableException e) {
                throw new RuntimeException(e);
            }
            try {
                sequencer.open();
            } catch (MidiUnavailableException e) {
                throw new RuntimeException(e);
            }

            // Create a sequence from the MIDI bytes
            Sequence sequence = null;
            try {
                sequence = MidiSystem.getSequence(new ByteArrayInputStream(midiBytes));
            } catch (InvalidMidiDataException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Set the sequence in the sequencer
            try {
                sequencer.setSequence(sequence);
            } catch (InvalidMidiDataException e) {
                throw new RuntimeException(e);
            }

            // Set loop count
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);

            // Start playing the MIDI music
            sequencer.start();

            // Wait until the sequencer finishes playing
            while (sequencer.isRunning()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        musicThread.start();

        // Close the sequencer
//        sequencer.stop();
//        sequencer.close();
    }

    public void stop() {
        if (sequencer != null && sequencer.isRunning()) {
            sequencer.stop();
            sequencer.close();
            sequencer = null;
        }
    }
}
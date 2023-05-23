package com.team4.terminal_reentry.applications;

import javax.sound.midi.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

class MidiPlayer {
    private byte[] midiBytes = null;
    private Sequencer sequencer;
    private MidiChannel midiChannel;
    private Transmitter transmitter;
    private Receiver receiver;
    private int volume = 50;

    public MidiPlayer(byte[] midiBytes) {
        this.midiBytes = midiBytes;
    }

    public void playMusic(int loop) {
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

                sequencer.open();
            } catch (MidiUnavailableException e) {
                throw new RuntimeException(e);
            }

            // Create a sequence from the MIDI bytes
            Sequence sequence = null;
            try {
                sequence = MidiSystem.getSequence(new ByteArrayInputStream(midiBytes));
                // Set the sequence in the sequencer
                sequencer.setSequence(sequence);
            } catch (InvalidMidiDataException | IOException e) {
                throw new RuntimeException(e);
            }

            // Set loop count
            sequencer.setLoopCount(loop > 0 ? Sequencer.LOOP_CONTINUOUSLY : 0);

            // Start playing the MIDI music
            sequencer.start();

            // Wait until the sequencer finishes playing
            while (sequencer != null && sequencer.isRunning()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        musicThread.start();
    }



    public void stop() {
        if (sequencer != null && sequencer.isRunning()) {
            sequencer.stop();
            sequencer.close();
            sequencer = null;
        }
    }

    public void mute() {
        sequencer.stop();
    }

    public void start() {
        sequencer.start();
    }

    public void volumeUp() {
        volume = volume > 108 ? volume + 20 : 127;
        midiChannel.controlChange(7, volume);
    }

    public void volumeDown() {
        volume = volume >= 20 ? volume - 20 : 0;
        midiChannel.controlChange(7, volume);
    }
}
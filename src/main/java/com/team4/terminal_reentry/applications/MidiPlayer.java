package com.team4.terminal_reentry.applications;

import javax.sound.midi.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

class MidiPlayer {
    byte[] midiBytes = null;
    Sequencer sequencer;

    public MidiPlayer(byte[] midiBytes) {
        this.midiBytes = midiBytes;
    }

    public void playMusic() {
        try {
            // Create a sequencer and open it
            sequencer = MidiSystem.getSequencer();
            sequencer.open();

            // Create a sequence from the MIDI bytes
            Sequence sequence = MidiSystem.getSequence(new ByteArrayInputStream(midiBytes));

            // Set the sequence in the sequencer
            sequencer.setSequence(sequence);

            // Set loop count
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);

            // Start playing the MIDI music
            sequencer.start();

            // Wait until the sequencer finishes playing
            while(sequencer.isRunning()) {
                Thread.sleep(100);
            }

            // Close the sequencer
            sequencer.stop();
            sequencer.close();
        } catch (MidiUnavailableException e) {
            throw new RuntimeException(e);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (sequencer != null && sequencer.isRunning()) {
            sequencer.stop();
            sequencer.close();
            sequencer = null;
        }
    }
}
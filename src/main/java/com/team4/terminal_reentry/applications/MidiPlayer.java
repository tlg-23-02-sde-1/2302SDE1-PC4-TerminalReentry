package com.team4.terminal_reentry.applications;

import javax.sound.midi.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

class MidiPlayer {
    private byte[] midiBytes = null;
    private Sequencer sequencer;
    private Sequence sequence;
    private int volume = 50;

    public MidiPlayer(byte[] midiBytes) {
        this.midiBytes = midiBytes;
    }

    public void playMusicThread(int loop) {
        Thread musicThread = new Thread(() -> {
            playMusic(loop, 0.25);
        });

        musicThread.start();
    }

    public void playMusic(int loop, double volumeFactor) {
        try {
            sequencer = MidiSystem.getSequencer();

            sequencer.open();
        } catch (MidiUnavailableException e) {
            throw new RuntimeException(e);
        }

        // Create a sequence from the MIDI bytes
        try {
            sequence = MidiSystem.getSequence(new ByteArrayInputStream(midiBytes));
            // Set the sequence in the sequencer
            sequencer.setSequence(sequence);
        } catch (InvalidMidiDataException | IOException e) {
            throw new RuntimeException(e);
        }

        // Set loop count
        sequencer.setLoopCount(loop > 0 ? Sequencer.LOOP_CONTINUOUSLY : 0);

        modifyVolume(volumeFactor);
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
        try{
            sequencer.stop();
            sequencer.close();
        } catch (NullPointerException | IllegalStateException e){}
    }




    public void stop() {
        if (sequencer != null && sequencer.isRunning()) {
            sequencer.stop();
            sequencer.close();
            sequencer = null;
        }
    }

    public void mute() {
        stop();
    }

    public void start(int loopCount, double volumeFactor) {
        playMusic(loopCount, volumeFactor);
    }

    public void volumeUp() {
        modifyVolume(2.0);
    }

    public void volumeDown() {
        modifyVolume(0.5);
    }

    private void modifyVolume(double factor) {
        // Retrieve the MIDI tracks from the sequence
        Track[] tracks = sequence.getTracks();

        // Adjust the volume for each MIDI event in each track
        for (Track track : tracks) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();

                if (message instanceof ShortMessage) {
                    ShortMessage shortMessage = (ShortMessage) message;
                    int command = shortMessage.getCommand();

                    // Adjust the volume by modifying the velocity value
                    if (command == ShortMessage.NOTE_ON || command == ShortMessage.NOTE_OFF) {
                        int originalVelocity = shortMessage.getData2();
                        int modifiedVelocity = modifiedVelocity(originalVelocity, factor);
                        // e.g., originalVelocity * volumeFactor

                        try {
                            shortMessage.setMessage(command, shortMessage.getChannel(),
                                    shortMessage.getData1(), modifiedVelocity);
                        } catch (InvalidMidiDataException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        try {
            sequencer.setSequence(sequence);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
        sequencer.stop();
        sequencer.start();
    }

    private int modifiedVelocity(int originalVelocity, double factor) {
        final int MAX_VELOCITY = 127;
        double scaledVelocity;

        if(factor > 1) {
            scaledVelocity = Math.min(originalVelocity * factor, MAX_VELOCITY);
        } else {
            scaledVelocity = Math.max(originalVelocity * factor, 1);
        }

        return (int) Math.round(scaledVelocity);
    }
}
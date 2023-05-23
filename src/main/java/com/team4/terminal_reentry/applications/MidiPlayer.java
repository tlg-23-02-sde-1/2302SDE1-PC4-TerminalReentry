package com.team4.terminal_reentry.applications;

import javax.sound.midi.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

class MidiPlayer {
    private byte[] midiBytes = null;
    private Sequencer sequencer;
    private MidiChannel midiChannel;
    private Synthesizer synthesizer;
    private Transmitter transmitter;
    private Receiver receiver;
    private int volume = 50;

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
                synthesizer = MidiSystem.getSynthesizer();
                sequencer = MidiSystem.getSequencer();

                synthesizer.open();
                sequencer.open();
            } catch (MidiUnavailableException e) {
                throw new RuntimeException(e);
            }

            // Create a sequence from the MIDI bytes
            Sequence sequence = null;
            try {
                sequence = MidiSystem.getSequence(new ByteArrayInputStream(midiBytes));
                transmitter = sequencer.getTransmitter();
                receiver = synthesizer.getReceiver();
                transmitter.setReceiver(receiver);
                // Set the sequence in the sequencer
                sequencer.setSequence(sequence);
            } catch (InvalidMidiDataException | MidiUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }

            // Set loop count
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);

            // Start playing the MIDI music
            sequencer.start();

            midiChannel = synthesizer.getChannels()[0];

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
            synthesizer.close();
        }
    }

    public void mute() {
        sequencer.stop();
//        for (MidiChannel channel : synthesizer.getChannels()) {
//            channel.allSoundOff();
//            channel.setMute(true);
////            for (int i = 0; i < 128; i++) {
////                channel.controlChange(i, 0);
////            }
//        }

//        midiChannel.allSoundOff();
    }

    public void start() {
        sequencer.start();
    }

    public void volumeUp() {
        volume = volume > 108 ? volume + 20 : 127;
        System.out.println(midiChannel.getController(7));
        midiChannel.controlChange(7, volume);
        System.out.println(midiChannel.getController(7));
    }

    public void volumeDown() {
        volume = volume >= 20 ? volume - 20 : 0;
        System.out.println(midiChannel.getController(7));
        midiChannel.controlChange(7, volume);
        System.out.println(midiChannel.getController(7));
    }
}
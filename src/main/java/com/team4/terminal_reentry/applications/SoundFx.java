package com.team4.terminal_reentry.applications;

class SoundFx {
    MidiPlayer getItem1;
    MidiPlayer getItem2;
    MidiPlayer pickupItem;
    MidiPlayer test;
    /*
     * Discard pile:
     * http://theolddub.com/TraditionalMidis/nemlop.mid
     * http://theolddub.com/TraditionalMidis/dzanom.mid
     * https://www.vgmusic.com/music/console/nintendo/nes/10-Yard_Fight-Kick_Off.mid
     */

    public SoundFx() {
        getItem1 = new MidiPlayer(new
                Music("https://www.vgmusic.com/music/console/nintendo/nes/Zelda1recorder.mid").getMidiBytes());
        getItem2 = new MidiPlayer(new
                Music("https://www.vgmusic.com/music/console/nintendo/nes/z1secret.mid")
                .getMidiBytes());
        test = new MidiPlayer(new
                Music("https://www.vgmusic.com/music/console/nintendo/nes/st2dead.mid").getMidiBytes());
        pickupItem = new MidiPlayer(new
                Music("https://www.vgmusic.com/music/console/nintendo/nes/smb-1up.mid").getMidiBytes());
    }

    public void getItem1() {
        getItem1.playMusic(0);
    }

    public void getItem2() {
        getItem2.playMusic(0);
    }

    public void playTest() {
        test.playMusic(0);
    }
    public void pickupItem() {
        pickupItem.playMusic(0);
    }

    public void killAll() {
        getItem1.stop();
        getItem2.stop();
        test.stop();
        pickupItem.stop();
    }
}
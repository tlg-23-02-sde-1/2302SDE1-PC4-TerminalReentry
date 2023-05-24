package com.team4.terminal_reentry.applications;

import java.util.HashMap;
import java.util.Map;

class SoundFx {
    MidiPlayer getItem1;
    MidiPlayer getItem2;
    MidiPlayer pickupItem;
    MidiPlayer test;
    Map<String, MidiPlayer> soundFx = new HashMap<>();
    /*
     * Discard pile:
     * http://theolddub.com/TraditionalMidis/nemlop.mid
     * http://theolddub.com/TraditionalMidis/dzanom.mid
     * https://www.vgmusic.com/music/console/nintendo/nes/10-Yard_Fight-Kick_Off.mid
     */

    public SoundFx() {
        soundFx.put("inventory", new MidiPlayer(new
                Music("https://www.vgmusic.com/music/console/nintendo/nes/Zelda1recorder.mid").getMidiBytes()));
        soundFx.put("look", new MidiPlayer(new
                Music("https://www.vgmusic.com/music/console/nintendo/nes/z1secret.mid")
                .getMidiBytes()));
        soundFx.put("inspect", new MidiPlayer(new
                Music("https://www.vgmusic.com/music/console/nintendo/nes/st2dead.mid").getMidiBytes()));
        soundFx.put("take", new MidiPlayer(new
                Music("https://www.vgmusic.com/music/console/nintendo/nes/smb-1up.mid").getMidiBytes()));
    }

    public void play(String key){
        //soundFx.get(key).playMusic(0, 1.0);
    }



    public void killAll() {
        getItem1.stop();
        getItem2.stop();
        test.stop();
        pickupItem.stop();
    }
}
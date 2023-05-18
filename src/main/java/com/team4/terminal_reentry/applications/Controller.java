package com.team4.terminal_reentry.applications;

import com.team4.terminal_reentry.setup.Player;
import com.team4.terminal_reentry.setup.Room;

import java.util.Map;

class Controller {
    //get, go, look, quit, help
    private Map<String, Room> map;
    private Player player;

    public Controller(Map<String, Room> map, Player player) {
        this.map = map;
        this.player = player;
    }

    public void execute(String[] commands) {
        String verb = commands[1];
        String noun = commands[2];
        if(verb.equals("go")) {
            player.getCurrentRoom().getExits().forEach((key, value)->{
                if(key.equals(noun)) {
                    player.setCurrentRoom(map.get(value));
                }
            });
        }
        else if(verb.equals("get")) {
            player.getCurrentRoom().getInventory().forEach((value)-> {
                if(value.getName().equals(noun)) {
                    player.addItem(value);
                    player.getCurrentRoom().removeItem(value);
                }
            });
        }
    }
}
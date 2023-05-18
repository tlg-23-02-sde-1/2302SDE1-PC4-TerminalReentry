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

    public boolean execute(String[] commands) {
        boolean isQuit = false;
        String verb = commands[1];
        String noun = commands[2];
        switch (verb) {
            case "inventory":
                System.out.println(player.showInventory());
            case "look":
                player.getCurrentRoom().getInventory().forEach((item)->{
                    if(item.getName().equals(noun)) {
                        System.out.println(item.getDescription());
                    }
                });
            case "go":
                player.getCurrentRoom().getExits().forEach((key, value) -> {
                    if (key.equals(noun)) {
                        player.setCurrentRoom(map.get(value));
                    }
                });
                break;
            case "get":
                player.getCurrentRoom().getInventory().forEach((value) -> {
                    if (value.getName().equals(noun)) {
                        player.addItem(value);
                        player.getCurrentRoom().removeItem(value);
                    }
                });
                break;
            case "quit":
                isQuit = true;
                break;
        }
        return isQuit;
    }
}
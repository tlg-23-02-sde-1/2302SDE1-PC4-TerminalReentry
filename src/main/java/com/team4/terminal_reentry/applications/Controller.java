package com.team4.terminal_reentry.applications;

import com.team4.terminal_reentry.items.Item;
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
                player.getInventory().forEach((item)->{
                    if(item.getName().equals(noun)) {
                        System.out.println(item.getDescription());
                    }
                });
                break;
            case "inspect":
                player.getCurrentRoom().getInventory().forEach((item)->{
                    if(item.getName().equals(noun)) {
                        System.out.println(item.getData());
                    }
                });
                player.getInventory().forEach((item)->{
                    if(item.getName().equals(noun)) {
                        System.out.println(item.getData());
                    }
                });
                break;
            case "go":
                Room current = player.getCurrentRoom();
                player.getCurrentRoom().getExits().forEach((key, value) -> {
                    if (key.equals(noun)) {
                        player.setCurrentRoom(map.get(value));
                    }
                });
                if (current.equals(player.getCurrentRoom())) {
                    System.out.println("You can't go that way dummy!");
                }
                break;
            case "take":
                Item item = null;
                for(int i = 0; i < player.getCurrentRoom().getInventory().size(); i++) {
                    Item value = player.getCurrentRoom().getInventory().get(i);
                    if(value.getName().equals(noun)) {
                        player.addItem(value);
                        item = value;
                    }
                }
                player.getCurrentRoom().removeItem(item);
                if (item == null) {
                    System.out.println("There is no " + noun + " in the room.");
                }
                break;
            case "talk":

            case "quit":
                isQuit = true;
                break;
        }
        return isQuit;
    }
}
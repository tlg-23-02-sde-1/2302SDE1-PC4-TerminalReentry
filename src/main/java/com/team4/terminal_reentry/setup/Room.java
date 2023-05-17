package com.team4.terminal_reentry.setup;

import com.team4.terminal_reentry.items.Item;

import java.util.List;
import java.util.Map;

public class Room {
    private String name;
    private List<Item> inventory;
    private List<NPC> characters;
    private Map<String,Room> directions;

    Room(String name, List<Item> items, List<NPC> npcs, Map<String, Room> directions) {
        this.name = name;
        for (Item item: items) {
            setInventory(item);
        }
        for(NPC npc: npcs) {
            setNpcs(npc);
        }
        setDirections(directions);
    }

    public void setInventory(Item item) {
        inventory.add(item);
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void setNpcs(NPC npc) {
        characters.add(npc);
    }

    public List<NPC> getNpcs() {
        return characters;
    }

    public void setDirections(Map<String, Room> directions) {
        this.directions = directions;
    }

    public Map<String, Room> getDirections() {
        return directions;
    }
}
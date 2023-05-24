package com.team4.terminal_reentry.setup;

import com.team4.terminal_reentry.items.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Room {
    private final String name;
    private final String description;
    private Map<String,String> exits;
    private final List<Item> inventory = new ArrayList<>();
    private final List<NPC> characters = new ArrayList<>();

    public Room(String name, String description, List<Item> items, List<NPC> npcs, Map<String, String> exits) {
        this.name = name;
        this.description = description;
        for (Item item: items) {
            setInventory(item);
        }
        for(NPC npc: npcs) {
            setNpcs(npc);
        }
        setExits(exits);
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setInventory(Item item) {
        inventory.add(item);
    }

    public List<Item> getInventory() {
        return inventory;
    }

    //TODO: implement this
    public void removeItem(Item item) {
        inventory.remove(item);
    }

    public void setNpcs(NPC npc) {
        characters.add(npc);
    }

    public List<NPC> getNpcs() {
        return characters;
    }

    public void setExits(Map<String, String> exits) {
        this.exits = exits;
    }

    public Map<String, String> getExits(){
        return exits;
    }

    public String inventoryToString() {
        StringBuilder inventoryString = new StringBuilder();
        for(Item item : inventory) {
            inventoryString.append(item.getName() + "\n");
        }
        return inventoryString.toString();
    }
    public String charactersToString() {
        StringBuilder charactersString = new StringBuilder();
        for(NPC person : characters) {
            charactersString.append(person.getName());
        }
        return charactersString.toString();
    }

    @Override
    public String toString() {
        return "Room{" +
                "name='" + name + "\'\n" +
                ", description='" + description + "\'\n" +
                ", exits=" + exits + "\n" +
                ", inventory=" + inventoryToString() + "\n" +
                ", characters=" + charactersToString() + "\n" +
                '}';
    }
}
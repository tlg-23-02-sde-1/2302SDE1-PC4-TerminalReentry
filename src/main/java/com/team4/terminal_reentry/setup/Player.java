package com.team4.terminal_reentry.setup;

import com.team4.terminal_reentry.items.Item;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name= "Jessy";
    private Room currentRoom;
    private List<Item> inventory = new ArrayList<>();

    public Player(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void addItem(Item item) {
        this.inventory.add(item);
    }

    public String showInventory() {
        StringBuilder itemList = new StringBuilder();
        for (Item item: inventory) {
            itemList.append(item.getName() + "\n");
        }
        return itemList.toString();
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }
}
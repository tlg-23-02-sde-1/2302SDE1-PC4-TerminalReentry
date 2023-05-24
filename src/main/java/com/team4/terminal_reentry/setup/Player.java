package com.team4.terminal_reentry.setup;

import com.team4.terminal_reentry.items.Item;

import java.util.*;

public class Player {
    private String name= "Jessy";
    private Room currentRoom;
    private List<Item> inventory = new ArrayList<>();
    private Set<String> npcMet = new HashSet<>();
    private Map<String,String> inspectedItem = new HashMap<>();
    private Set<String> roomsVisited = new HashSet<>();

    public Player(Room currentRoom) {
        this.currentRoom = currentRoom;
        roomsVisited.add(currentRoom.getName());
    }

    public Player(Room currentRoom, List<Item> inventory, Map<String,String> inspectedItem, Set<String> npcMet, Set<String> roomsVisited) {
        this.currentRoom = currentRoom;
        this.inventory = inventory;
        this.inspectedItem = inspectedItem;
        this.npcMet = npcMet;
        this.roomsVisited = roomsVisited;
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

    public void metNpc (String npcName) {
        this.npcMet.add(npcName);
    }

    public void inspectedItem(String itemName, String itemData) {
        inspectedItem.put(itemName,itemData);
    }

    public void visitRoom(String roomName) {
        this.roomsVisited.add(roomName);
    }

    public Set<String> getNpcMet() {
        return this.npcMet;
    }

    public Set<String> getRoomsVisited() {
        return this.roomsVisited;
    }

    public Map<String, String> getInspectedItem() {
        return inspectedItem;
    }
}
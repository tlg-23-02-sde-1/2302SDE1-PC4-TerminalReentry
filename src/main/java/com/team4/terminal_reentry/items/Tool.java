package com.team4.terminal_reentry.items;

public class Tool extends Item{

    boolean onOff = false;

    Tool(String name, String description, boolean associated) {
        super(name, description, associated);
    }

    public void use(boolean on) {
        onOff = on;
    }
}
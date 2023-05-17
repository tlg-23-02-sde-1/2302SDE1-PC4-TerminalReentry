package com.team4.terminal_reentry.items;

public class Item {
    private String name;
    private String description;
    private boolean associated;

    Item(String name, String description, boolean associated) {
        setName(name);
        setDescription(description);
        setAssociated(associated);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAssociated(boolean associated) {
        this.associated = associated;
    }

    public boolean getAssociated() {
        return associated;
    }
}
package com.team4.terminal_reentry.items;

public class Item {
    private String name;
    private String description;
    private boolean isEvidence;

    Item(String name, String description, boolean isEvidence) {
        setName(name);
        setDescription(description);
        setEvidence(isEvidence);
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

    public void setEvidence(boolean evidence) {
        this.isEvidence = evidence;
    }

    public boolean getEvidence() {
        return isEvidence;
    }
}
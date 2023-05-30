package com.team4.terminal_reentry.items;

public class Item {
    private String name;
    private String description;
    String data;
    String secret;

    private boolean isEvidence;

    Item(String name, String description, boolean isEvidence) {
        setName(name);
        setDescription(description);
        setEvidence(isEvidence);
    }

    public String getData() {
        return data;
    }

    public String getSecret() {
        return secret;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void setEvidence(boolean evidence) {
        this.isEvidence = evidence;
    }
}
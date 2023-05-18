package com.team4.terminal_reentry.items;

public class Weapon extends Item{

    private String data;
    private String secretData;

    public Weapon(String name, String description, boolean associated, String data, String secretData) {
        super(name, description, associated);
        setSecretData(associated ? secretData : "No new information.");
        setData(data);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setSecretData(String secretData) {
        this.secretData = secretData;
    }

    public String getSecret(boolean gotBlackLight) {
        return secretData;
    }
}
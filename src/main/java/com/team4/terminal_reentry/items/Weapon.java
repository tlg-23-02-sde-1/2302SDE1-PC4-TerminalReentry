package com.team4.terminal_reentry.items;

public class Weapon extends Item{


    public Weapon(String name, String description, boolean associated, String data, String secretData) {
        super(name, description, associated);
        setSecretData(associated ? secretData : "No new information.");
        setData(data);
    }

    public String getData() {
        return data;
    }

    private void setData(String data) {
        this.data = data;
    }

    private void setSecretData(String secretData) {
        this.secret = secretData;
    }

    public String getSecret(boolean gotBlackLight) {
        return secret;
    }
}
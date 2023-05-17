package com.team4.terminal_reentry.items;

class Weapon extends Item{

    private String data;
    private String secret;

    Weapon(String name, String description, boolean associated, String data, String secret) {
        super(name, description, associated);
        setSecret(secret);
        setData(data);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSecret(boolean gotBlackLight) {
        return secret;
    }
}
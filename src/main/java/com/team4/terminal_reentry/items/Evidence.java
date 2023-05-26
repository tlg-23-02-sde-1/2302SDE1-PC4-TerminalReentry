package com.team4.terminal_reentry.items;

public class Evidence extends Item{


    public Evidence(String name, String description, boolean associated, String data, String secret) {
        super(name, description, associated);
        setData(data);
        setSecret(associated ? secret : "There is nothing out of the ordinary in this " + name + ".");
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
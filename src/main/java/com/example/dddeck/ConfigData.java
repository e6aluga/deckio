package com.example.dddeck;

public class ConfigData {
    private String name;
    private String saveLocationPC;
    private String saveLocationSteamDeck;
    private String gameName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSaveLocationPC() {
        return saveLocationPC;
    }

    public void setSaveLocationPC(String saveLocationPC) {
        this.saveLocationPC = saveLocationPC;
    }

    public String getSaveLocationSteamDeck() {
        return saveLocationSteamDeck;
    }

    public void setSaveLocationSteamDeck(String saveLocationSteamDeck) {
        this.saveLocationSteamDeck = saveLocationSteamDeck;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getData() {
        return "Name: " + name + ", Game: " + gameName + ", PC Location: " + saveLocationPC + ", SteamDeck Location: " + saveLocationSteamDeck;
    }
}
package com.example.dddeck;

public class DeckData {
    private String deckIp;
    private String deckUser;
    private String deckPassword;
    private String deckPort;

    // Геттеры и сеттеры
    public String getIp() {
        return deckIp;
    }

    public void setIp(String deckIp) {
        this.deckIp = deckIp;
    }

    public String getUser() {
        return deckUser;
    }

    public void setUser(String deckUser) {
        this.deckUser = deckUser;
    }

    public String getPassword() {
        return deckPassword;
    }

    public void setPassword(String deckPassword) {
        this.deckPassword = deckPassword;
    }

    public String getPort() {
        return deckPort;
    }

    public void setPort(String deckPort) {
        this.deckPort = deckPort;
    }

    public String getData() {
        return "DeckIp: " + deckIp + ", DeckUser: " + deckUser + ", DeckPassword: " + deckPassword + ", DeckPort " + deckPort;
    }
}

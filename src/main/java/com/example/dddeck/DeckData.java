package com.example.dddeck;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DeckData {
    private String deckIp;
    private String deckUser;
    private String deckPassword;
    private String deckPort;

    public DeckData(String deckIp, String deckUser, String deckPassword, String deckPort){
        this.deckIp = deckIp;
        this.deckUser = deckUser;
        this.deckPassword = deckPassword;
        this.deckPort = deckPort;
    }

    public void loadSteamDeckSettings(){
        System.out.println(App.timestamp() + "loadSteamDeckSettings()");
        Gson gson = new Gson();
        String filepath = "settings.json";
        Map<String, String> map = null;

        try (FileReader fileReader = new FileReader(filepath)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            map = gson.fromJson(fileReader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(App.timestamp() + "Steam Deck Settings: " + map);

        if (map != null) {
            this.deckIp = map.get("deckIp");
            this.deckUser = map.get("deckUser");
            this.deckPassword = map.get("deckPassword");
            this.deckPort = map.get("deckPort");
        }
    }

    public DeckData(){
    }

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

package com.bunnuvon.mediumcore;

import org.json.simple.JSONObject;

public class PlayerObject {
    public String uuid;
    public String name;
    public int deaths;
    public int hearts;

    public PlayerObject(String uuid, String name, int deaths, int hearts) {
        this.uuid = uuid;
        this.name = name;
        this.deaths = deaths;
        this.hearts = hearts;
    }

    public JSONObject export() {
        JSONObject object = new JSONObject();

        object.put("uuid", this.uuid);
        object.put("name", this.name);
        object.put("deaths", this.deaths);
        object.put("hearts", this.hearts);

        return object;
    }
}
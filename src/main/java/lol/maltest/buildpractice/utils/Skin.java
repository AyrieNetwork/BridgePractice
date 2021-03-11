package lol.maltest.buildpractice.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class Skin {
    String uuid;

    String name;

    String value;

    String signatur;

    public Skin(String uuid) {
        this.uuid = uuid;
        load();
    }

    private void load() {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + this.uuid + "?unsigned=false");
            URLConnection uc = url.openConnection();
            uc.setUseCaches(false);
            uc.setDefaultUseCaches(false);
            uc.addRequestProperty("User-Agent", "Mozilla/5.0");
            uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
            uc.addRequestProperty("Pragma", "no-cache");
            String json = (new Scanner(uc.getInputStream(), "UTF-8")).useDelimiter("\\A").next();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONArray properties = (JSONArray)((JSONObject)obj).get("properties");
            for (int i = 0; i < properties.size(); i++) {
                try {
                    JSONObject property = (JSONObject)properties.get(i);
                    String name = (String)property.get("name");
                    String value = (String)property.get("value");
                    String signature = property.containsKey("signature") ? (String)property.get("signature") : null;
                    this.name = name;
                    this.value = value;
                    this.signatur = signature;
                } catch (Exception exception) {}
            }
        } catch (Exception exception) {}
    }

    public String getSkinValue() {
        return this.value;
    }

    public String getSkinName() {
        return this.name;
    }

    public String getSkinSignatur() {
        return this.signatur;
    }
}

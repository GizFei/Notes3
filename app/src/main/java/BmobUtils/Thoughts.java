package BmobUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobObject;

public class Thoughts extends BmobObject {

    private List<JSONObject> thoughts;
    private String username;

    public List<JSONObject> getThoughts() {
        return thoughts;
    }

    public void setThoughts(List<JSONObject> thoughts) {
        this.thoughts = thoughts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

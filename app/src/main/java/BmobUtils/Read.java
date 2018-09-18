package BmobUtils;

import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobObject;

public class Read extends BmobObject {
    private List<JSONObject> readlist;
    private String username;

    public List<JSONObject> getReadlist() {
        return readlist;
    }

    public void setReadlist(List<JSONObject> readlist) {
        this.readlist = readlist;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

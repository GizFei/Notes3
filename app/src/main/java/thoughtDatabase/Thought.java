package thoughtDatabase;

import java.util.UUID;

public class Thought {

    public UUID mUUID;
    public String title;
    public String content;

    public Thought(){
        this.mUUID = UUID.randomUUID();
        title = "";
        content = "";
    }

    public Thought(String uuid){
        this.mUUID = UUID.fromString(uuid);
    }

    public UUID getUUID() {
        return mUUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

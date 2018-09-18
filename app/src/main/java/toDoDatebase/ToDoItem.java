package toDoDatebase;


import java.util.UUID;

public class ToDoItem {

    private String mThing;
    private UUID mUUID;
    //
    private boolean isIconVisible;

    public ToDoItem(){
        mUUID = UUID.randomUUID();
        mThing = "";
        isIconVisible = false;
    }

    public ToDoItem(String uuidString){
        mUUID = UUID.fromString(uuidString);
        isIconVisible = false;
    }

    public String getThing() {
        return mThing;
    }

    public void setThing(String thing) {
        mThing = thing;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public void setIconVisible(boolean visible){
        isIconVisible = visible;
    }

    public boolean isIconVisible() {
        return isIconVisible;
    }
}

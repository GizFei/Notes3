package BmobUtils;

import java.util.List;

import cn.bmob.v3.BmobObject;

public class ToDo extends BmobObject {
    private String username;
    private List<String> todolist;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getTodolist() {
        return todolist;
    }

    public void setTodolist(List<String> todolist) {
        this.todolist = todolist;
    }
}

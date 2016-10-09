package com.blogspot.rulesare.getthingdone_mydb;


public class ToDoItem implements java.io.Serializable {
    private long id;
    private String name;
    private boolean isComplete;
    private String action;

    public ToDoItem() {
        id = 0;
        name = "";
        isComplete = false;
        action = "";
    }

    public ToDoItem(long id) {
        this.id = id;
        name = "";
        isComplete = false;
        action = "";
    }

    public ToDoItem(long id, String name, boolean isComplete) {
        this.id = id;
        this.name = name;
        this.isComplete = isComplete;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean getIsComplete() {
        return isComplete;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }


}
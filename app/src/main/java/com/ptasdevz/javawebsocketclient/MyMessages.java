package com.ptasdevz.javawebsocketclient;

public class MyMessages {
    private AbstractElement mymessqge = new Message("10",10);

    @Override
    public String toString() {
        return "MyMessages{" +
                "mymessqge=" + mymessqge +
                '}';
    }
}

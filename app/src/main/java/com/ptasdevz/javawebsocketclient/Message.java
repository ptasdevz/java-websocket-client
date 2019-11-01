package com.ptasdevz.javawebsocketclient;

public class Message extends AbstractElement {
    private String msg;
    private int Id;

    public Message(String msg, int id) {
        this.msg = msg;
        Id = id;
    }

    public Message() { }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    @Override
    public String toString() {
        return "Message{" +
                "msg='" + msg + '\'' +
                ", Id=" + Id +
                '}';
    }
}

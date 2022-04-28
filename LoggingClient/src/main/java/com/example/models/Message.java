package com.example.models;

import java.io.Serializable;
import java.util.UUID;

public class Message implements  Serializable {
    private UUID id;
    private String from;
    private String body;
    private String command;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return "Name:" + from + "\n" + body;
    }
}

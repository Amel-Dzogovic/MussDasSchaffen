package com.example.models;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private UUID uuid;

    private String from;

    private String body;

    private String command;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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
        return "Name:"+from + "\n" + body;
    }
}

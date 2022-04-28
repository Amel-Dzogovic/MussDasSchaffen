package com.example.models;

import com.example.pattern.Observable;

import java.util.ArrayList;
import java.util.List;

public class MessageDistributor extends Observable {
    private static MessageDistributor instance = null;
    private List<Message> messages = new ArrayList<>();

    public static MessageDistributor getInstance() {
        if (instance == null) {
            instance = new MessageDistributor();
        }
        return instance;
    }

    private MessageDistributor() {

    }

    public void addMessage(Message message) {
        if (message == null)
            throw new IllegalArgumentException("No Message");

        if (messages.contains(message) == false) {
            this.messages.add(message);
            this.notifyAll(message);
        }
    }
}

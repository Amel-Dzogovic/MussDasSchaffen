package com.example.chatClient;

import com.example.models.Message;
import com.example.models.MessageDistributor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

public class SocketListener extends Thread {
    private static final String Disconnet = "quit";
    private UUID id;
    private Socket socket;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;

    public SocketListener(Socket socket1) {
        if (socket1 == null)
            throw new IllegalArgumentException("No Socket");
        this.socket = socket1;
        try {
            this.ois = new ObjectInputStream(socket1.getInputStream());
            this.oos = new ObjectOutputStream(socket1.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.setDaemon(true);
        this.id = UUID.randomUUID();
    }

    public void run() {
        boolean error = false;
        String command = "";

        while (!error && !command.equals("disconnected")) {
            try {
                Message message = (Message) this.ois.readObject();
                MessageDistributor.getInstance().addMessage(message);
                System.out.printf("%s\n", message.toString());
                command = message.getCommand();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }


        try {
            oos.close();
            oos = null;
            ois.close();
            ois = null;
            socket.close();
            socket = null;
        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }

    public void writeMessage(Message message) {
        if (message == null)
            throw new IllegalArgumentException("Wrong");

        if (this.oos != null) {
            try {
                message.setId(id);
                this.oos.writeObject(message);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


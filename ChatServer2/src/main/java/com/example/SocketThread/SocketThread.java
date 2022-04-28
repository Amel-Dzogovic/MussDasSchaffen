package com.example.SocketThread;

import com.example.models.Message;
import com.example.models.MessageDistributor;
import com.example.pattern.Observer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketThread extends Thread implements Observer {

    private static String QuitCommand = "quit";

    private static String Disconnected = "disconnected";

    private static String ServerInfo = "";

    private static ObjectOutputStream oos = null;

    private static ObjectInputStream ois = null;

    private Socket socket;

    public SocketThread(Socket socket1) {
        if (socket1 == null)
            throw new IllegalArgumentException();

        this.socket = socket1;

        try {
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDaemon(true);
    }

    @Override
    public void run() {
        boolean error = false;
        String command = "";

        while (!error && !command.equals(QuitCommand)) {
            try {
                Message clientMessage = (Message) ois.readObject();

                System.out.printf("%s\n" + clientMessage.toString());
                command = clientMessage.getCommand();
                if (command.equals("quit")) {
                    String from = clientMessage.getFrom();
                    MessageDistributor.getInstance().removeObserver(this);

                    clientMessage.setCommand(Disconnected);
                    clientMessage.setFrom("This");
                    clientMessage.setBody("");

                    synchronized (this) {
                        oos.writeObject(clientMessage);
                        oos.flush();
                    }

                    clientMessage.setCommand(ServerInfo);
                    clientMessage.setBody(from+"has left the chat");
                    MessageDistributor.getInstance().addMassage(clientMessage);
                }
                else{
                    clientMessage.setCommand("dispatched");
                    MessageDistributor.getInstance().addMassage(clientMessage);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        try{
            ois.close();
            ois=null;
            oos.close();
            oos=null;
            socket.close();
            socket=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notify(Object sender, Object args) {
        if(sender instanceof MessageDistributor
        && args instanceof Message
        && oos !=null){
            try{
                oos.writeObject(args);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

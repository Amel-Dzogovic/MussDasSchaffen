package com.example.Logic;

import com.example.models.Message;
import com.example.models.Messagedistributor;
import com.example.pattern.Observer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketThread extends Thread implements Observer {

    private final String quit = "quit";

    private final String Disc = "disconnected";

    private final String command = "";


    private ObjectOutputStream oos = null;

    private ObjectInputStream ois = null;

    Socket socket;

    public SocketThread(Socket socket1) {
        if (socket1 == null)
            throw new IllegalArgumentException();

        this.socket = socket1;

        try {
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setDaemon(true);
    }

    public void run() {
        boolean error = false;
        String command = "";

        while (!error && !command.equals(quit)) {
            try {
                Message clientMessage = (Message) ois.readObject();
                System.out.printf(clientMessage.toString());
                command=clientMessage.getCommand();
                if(command.equals(Disc)){
                    String from = clientMessage.getFrom();
                    Messagedistributor.getInstance().removeObserver(this);

                    synchronized (this){
                        oos.writeObject(clientMessage);
                        oos.flush();
                    }

                    clientMessage.setBody("One User left");
                    Messagedistributor.getInstance().addMessage(clientMessage);
                }
                else{
                    clientMessage.setCommand("dispatched");
                    Messagedistributor.getInstance().addMessage(clientMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        }

        try{
            oos.close();
            oos=null;
            ois.close();
            ois=null;
            socket.close();
            socket=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notify(Object sender, Object args) {
        if (sender instanceof Messagedistributor
                && args instanceof Message
                && oos != null){
            try{
                oos.writeObject(args);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.example.loginserver;

import com.example.models.Message;
import com.example.models.MessageDistributor;
import com.example.models.Share;
import com.example.pattern.Observer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketThread extends Thread implements Observer {

    private static final String QuitCommand = "quit";

    private static final String ServerInfoCommand = "serverinfo";

    private static final String DisconnectedCommand = "disconnected";

    private static Socket socket;

    private static ObjectInputStream ois=null;
    private static ObjectOutputStream oos=null;

    private Share share;

    public SocketThread(Socket socket1){
        if(socket1==null)
            throw new IllegalArgumentException("No Socket");

        this.socket = socket1;

        try{
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDaemon(true);
    }

    @Override
    public void run() {
        boolean error = false;
        String command = "";


        while(!error && !command.equals("disconnected")){
            try{
                Message clientMessage = (Message)ois.readObject();

                System.out.printf("%s\n",clientMessage.toString());
                command = clientMessage.getCommand();
                if(command.equals(QuitCommand)){
                    String from = clientMessage.getFrom();
                    MessageDistributor.getInstance().removeObserver(this);

                    clientMessage.setCommand(DisconnectedCommand);
                    clientMessage.setFrom("Login-Server");
                    clientMessage.setBody("");
                    synchronized (this){
                        oos.writeObject(clientMessage);
                        oos.flush();
                    }
                    clientMessage.setCommand(ServerInfoCommand);
                    clientMessage.setBody(String.format("%s left he Chat",from));
                    MessageDistributor.getInstance().addMessage(clientMessage);
                }
                else{
                    clientMessage.setCommand("dispatch");
                    MessageDistributor.getInstance().addMessage(clientMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
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
    public void notify(Object Sender, Object args) {
        if(Sender instanceof MessageDistributor
        && args instanceof  Message
        && oos != null){
            try{
                synchronized (this){
                    oos.writeObject(args);
                    oos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

package com.example.Chatserver;

import com.example.models.Message;
import com.example.models.MessageDistrubutor;
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
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    public SocketThread(Socket socket){
        if(socket==null)
            throw new IllegalArgumentException("Wrong");

        this.socket=socket;

        try{
            oos = new ObjectOutputStream(this.socket.getOutputStream());
            ois = new ObjectInputStream(this.socket.getInputStream());
        }
        catch(IOException e){
            e.printStackTrace();
        }
        setDaemon(true);
    }

    @Override
    public void run() {
        boolean error = false;
        String command = "";


        while (!error && !command.equals("disconnected")) {

            try{
                Message clientMessage = (Message)ois.readObject();

                System.out.printf("%s\n",clientMessage.toString());
                command = clientMessage.getCommand();
                if(command.equals(QuitCommand)){
                    String from = clientMessage.getFrom();
                    MessageDistrubutor.getInstance().RemoveObserver(this);

                    clientMessage.setCommand(DisconnectedCommand);
                    clientMessage.setFrom("Chat-Server");
                    clientMessage.setBody("");
                    synchronized (this){
                        oos.writeObject(clientMessage);
                        oos.flush();
                    }
                    clientMessage.setCommand(ServerInfoCommand);
                    clientMessage.setBody(String.format("%s left the Chat",from));
                    MessageDistrubutor.getInstance().addMessage(clientMessage);
                }
                else {
                    clientMessage.setCommand("dispatch");
                    MessageDistrubutor.getInstance().addMessage(clientMessage);
                }

            } catch (IOException e) {
                error = true;
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                error = true;
                e.printStackTrace();
            }
        }

        try{
            oos.close();
            oos = null;
            ois.close();
            ois=null;
            socket.close();
            socket =null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notify(Object sender, Object args) {
        if(sender instanceof MessageDistrubutor
        && args instanceof Message
        && oos!=null)
        {
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

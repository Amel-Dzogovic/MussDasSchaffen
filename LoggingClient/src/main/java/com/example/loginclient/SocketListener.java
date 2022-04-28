package com.example.loginclient;

import com.example.models.Message;
import com.example.models.MessageDistributor;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class SocketListener extends Thread{

    private static final String Disconnect = "quit";

    private UUID id;

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;


    public SocketListener(Socket socket1){
        if(socket1==null)
            throw new IllegalArgumentException("Wrong");
        this.socket=socket1;

        try{
            ois=new ObjectInputStream(socket.getInputStream());
            oos=new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDaemon(true);
        this.id=UUID.randomUUID();
    }

    public void run(){
        boolean error= false;
        String command="";

        while(!error && !command.equals("disconnected")){
            try{
                Message message = (Message) this.ois.readObject();
                MessageDistributor.getInstance().addMessage(message);

                System.out.printf("%s\n",message.toString());
                command= message.getCommand();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        try{
            socket.close();
            socket=null;
            oos.close();
            oos=null;
            ois.close();
            ois=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMessage(Message message){
        if(message==null)
            throw new IllegalArgumentException("No Message");

        if(this.oos!=null){
            try{
                message.setId(id);
                this.oos.writeObject(message);
                oos.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

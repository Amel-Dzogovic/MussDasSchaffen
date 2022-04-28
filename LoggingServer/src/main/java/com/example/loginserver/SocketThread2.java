package com.example.loginserver;

import com.example.models.Message;
import com.example.models.MessageDistributor;
import com.example.pattern.Observer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketThread2 extends Thread implements Observer {
    private static final String quit = "quit";

    private static final String dis = "disconnected";

    private static final String Serverinfo = "ServerInformation";

    private Socket socket;

    private ObjectInputStream ois = null;

    private ObjectOutputStream oos = null;


    public SocketThread2(Socket socket1){
        if(socket1==null)
            throw new IllegalArgumentException();

        this.socket=socket1;

        try{
            oos=new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


public void run(){

}



    @Override
    public void notify(Object Sender, Object args) {
        if(Sender instanceof MessageDistributor
        && args instanceof Message
        && oos!= null){
            synchronized (this){
                try{
                    oos.writeObject(args);
                    oos.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}

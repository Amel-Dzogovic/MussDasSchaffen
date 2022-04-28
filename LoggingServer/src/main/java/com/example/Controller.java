package com.example;

import com.example.loginserver.SocketThread;
import com.example.models.Message;
import com.example.models.MessageDistributor;
import com.example.pattern.Observer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Observer, Initializable {

  @FXML
  private TextField txtName;

  @FXML
  private TextField port;
  private ServerSocket serverSocket;


    @Override
    public void notify(Object Sender, Object args) {
        if(args instanceof Message){
            Platform.runLater(()->txtName.appendText(args.toString()+"\n"));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtName.setDisable(false);

        MessageDistributor.getInstance().addObserver(this);
    }

    public void onStart(ActionEvent actionEvent){
        if(serverSocket == null){
            int portt = Integer.parseInt(port.getText());

            txtName.clear();

            Thread t = new Thread(()->{
                try {
                    serverSocket = new ServerSocket(portt);

                    while(!serverSocket.isClosed()){
                        Socket socket = serverSocket.accept();
                        SocketThread socketThread = new SocketThread(socket);

                        MessageDistributor.getInstance().addObserver(socketThread);
                        socketThread.start();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                serverSocket = null;
            });
            t.setDaemon(true);
            t.start();
            System.out.printf("hi");
        }
    }


    public void onStop(ActionEvent actionEvent){
        if(serverSocket!=null){
            try{
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

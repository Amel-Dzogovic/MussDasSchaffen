package com.example;

import com.example.Logic.SocketThread;
import com.example.models.Message;
import com.example.models.Messagedistributor;
import com.example.pattern.Observer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Observer, Initializable {
    @FXML
    private TextField txtPort;

    @FXML
    private TextArea txtContent;

    @FXML
    private Button btnStart;

    @FXML
    private Button btnStop;

    private ServerSocket serverSocket;

    public void onStart(ActionEvent actionEvent){
        if(serverSocket==null){
            int port = Integer.parseInt(txtPort.getText());

            txtContent.clear();

            Thread t = new Thread(()->{
                try{
                    serverSocket = new ServerSocket(port);
                    while(!serverSocket.isClosed()){
                        Socket socket = serverSocket.accept();
                        SocketThread socketThread = new SocketThread(socket);

                        Messagedistributor.getInstance().addObserver(socketThread);
                        socketThread.start();

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            t.setDaemon(true);
            t.start();

            btnStart.setDisable(true);
            btnStop.setDisable(false);
            System.out.printf("Server is running");
        }
    }

    @Override
    public void notify(Object sender, Object args) {
        if(args instanceof Message){
            Platform.runLater(()->txtContent.appendText(args.toString()+"\n"));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtPort.setDisable(false);
        btnStop.setDisable(true);
        Messagedistributor.getInstance().addObserver(this);
    }

    public void onStop(ActionEvent actionEvent){
        if(serverSocket!=null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        btnStart.setDisable(false);
        btnStop.setDisable(true);
    }
}
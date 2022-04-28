package com.example;

import com.example.models.Message;
import com.example.models.MessageDistrubutor;
import com.example.pattern.Observer;
import com.example.Chatserver.SocketThread;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Observer, Initializable {
    @FXML
    public TextArea txtContent;

    @FXML
    public TextField txtPort;

    @FXML
    public Button btnStart;

    @FXML
    public Button btnStop;

    private ServerSocket serverSocket;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        txtContent.setDisable(false);
        txtContent.setEditable(false);

        txtPort.setText("3333");
        txtPort.setDisable(false);

        btnStart.setDefaultButton(true);
        btnStart.setDisable(false);

        btnStop.setDisable(true);

        MessageDistrubutor.getInstance().addObserver(this);
    }

    @Override
    public void notify(Object sender, Object args){
        if(args instanceof Message){
            Platform.runLater(() -> txtContent.appendText(args.toString()+ "\n"));
        }
    }

    public void onStart(ActionEvent actionEvent){
        if(serverSocket==null){

            int port = Integer.parseInt(txtPort.getText());

            txtContent.clear();

            Thread t = new Thread(()->{
                try{
                    serverSocket = new ServerSocket(port);
                    while(true){
                        Socket socket = serverSocket.accept();
                        SocketThread socketThread = new SocketThread(socket);

                        MessageDistrubutor.getInstance().addObserver(socketThread);
                        socketThread.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                serverSocket = null;
            });

            t.setDaemon(true);
            t.start();

            txtPort.setDisable(true);
            btnStart.setDisable(true);
            btnStop.setDisable(true);
            System.out.println("Chat server is running");
        }
    }

    public void onStop(ActionEvent actionEvent){
        if(serverSocket != null){
            try{
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            txtPort.setDisable(false);
            btnStart.setDisable(false);
            btnStop.setDisable(true);
        }
    }
}
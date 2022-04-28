package com.example;

import com.example.loginclient.SocketListener;
import com.example.models.Message;
import com.example.models.MessageDistributor;
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
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.UUID;

public class HelloController implements Observer, Initializable {
    @FXML
    private TextField txtHost;

    @FXML
    private TextField txtPort;

    @FXML
    private TextField txtGerät;

    @FXML
    private TextArea txtArea;

    @FXML
    private Button btnSend;

    private UUID id;

    private SocketListener socketListener;

    public HelloController(){

    }

    @Override
    public void notify(Object Sender, Object args) {
        if(Sender instanceof Message){

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtPort.setText("3333");
        txtPort.setDisable(true);
        txtHost.setDisable(false);
        txtGerät.setDisable(false);
        txtArea.setDisable(false);
        txtHost.setText("localhost");
        btnSend.setDisable(false);
        MessageDistributor.getInstance().addObserver(this);
    }

    public void onSend(ActionEvent actionEvent) {
        if (actionEvent == null)
            throw new IllegalArgumentException();

        if (socketListener == null) {
            int port = Integer.parseInt(txtPort.getText());
            try {
                Socket socket = new Socket(this.txtHost.getText(), port);
                this.socketListener = new SocketListener(socket);
                this.socketListener.start();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Message message = new Message();
        message.setId(id);
        message.setFrom(txtGerät.getText());
        message.setBody(txtArea.getText());
        message.setCommand("send");
        this.socketListener.writeMessage(message);
        txtArea.clear();

        socketListener = null;
    }

}
package com.example;

import com.example.chatClient.SocketListener;
import com.example.models.Message;
import com.example.models.MessageDistributor;
import com.example.pattern.Observer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
    public TextArea txtContent;
    @FXML
    public TextArea txtBody;
    @FXML
    public TextField txtName;
    @FXML
    public TextField txtServer;
    @FXML
    public TextField txtPort;
    @FXML
    public Button btnStart;
    @FXML
    public Button btnStop;
    @FXML
    public Button btnSend;
    private UUID id;
    private SocketListener socketListener;

    public HelloController(){

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.id = UUID.randomUUID();
        this.txtContent.setDisable(false);
        this.txtContent.setEditable(false);
        this.txtBody.setDisable(false);
        this.txtBody.setEditable(false);
        this.txtServer.setText("localhost");
        this.txtServer.setDisable(false);
        this.txtPort.setText("3333");
        this.txtPort.setDisable(false);
        this.txtName.setText("");
        this.txtName.setDisable(false);
        this.btnStart.setDefaultButton(true);
        this.btnStart.setDisable(false);
        this.btnStop.setDisable(true);
        this.btnSend.setDisable(true);
        MessageDistributor.getInstance().addObserver(this);
    }

    @Override
    public void notify(Object sender, Object args) {
        if(args instanceof Message){
            Platform.runLater(()->{
                this.txtContent.appendText(""+args+"\n");
            });
        }
    }

    public void onStart(ActionEvent actionEvent){
        if(actionEvent == null)
            throw new IllegalArgumentException("Wrong");

        if(this.socketListener == null){
            int port = Integer.parseInt(txtPort.getText());
            this.txtContent.clear();

            try{
                Socket socket = new Socket(this.txtServer.getText(), port);
                this.socketListener = new SocketListener(socket);
                this.socketListener.start();
                this.txtServer.setDisable(true);
                this.txtPort.setDisable(true);
                this.txtName.setDisable(true);
                this.txtBody.setEditable(true);
                this.btnStart.setDisable(true);
                this.btnStop.setDisable(false);
                this.btnSend.setDisable(false);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onStop(ActionEvent actionEvent){
        if(actionEvent==null)
            throw new IllegalArgumentException("Wrong");
        if(socketListener != null){
            Message message = new Message();
            message.setId(this.id);
            message.setFrom(this.txtName.getText());
            message.setCommand("quit");
            this.socketListener.writeMessage(message);
            this.txtPort.setDisable(false);
            this.txtServer.setDisable(false);
            this.txtName.setDisable(false);
            this.txtBody.setEditable(false);
            this.btnStart.setDisable(false);
            this.btnStop.setDisable(true);
            this.btnSend.setDisable(true);
        }

        socketListener = null;
    }

    public void onSend(ActionEvent actionEvent){
        if(actionEvent == null)
            throw new IllegalArgumentException();

        if(socketListener != null){
            Message message = new Message();
            message.setId(id);
            message.setBody(txtBody.getText());
            message.setFrom(txtName.getText());
            message.setCommand("send");
            this.socketListener.writeMessage(message);
            txtBody.clear();
        }
    }
}
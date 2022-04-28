package com.example;

import com.example.loginserver.SocketThread;
import com.example.models.Message;
import com.example.models.MessageDistributor;
import com.example.models.Share;
import com.example.pattern.Observer;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.AccessibleAction;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.UUID;

public class HelloController implements Observer, Initializable {
    @FXML
    private Button btnStart;

    @FXML
    private Button btnStop;

    @FXML
    private TextField txtPort;

    @FXML
    private TextArea txtArea;

    private ServerSocket serverSocket;

    private Share share;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtPort.setText("3333");
        txtPort.setDisable(false);

        txtArea.setDisable(false);
        txtArea.setEditable(false);

        btnStart.setDefaultButton(true);
        btnStart.setDisable(false);

        btnStop.setDisable(true);

        MessageDistributor.getInstance().addObserver(this);
    }

    @Override
    public void notify(Object Sender, Object args) {
        if (args instanceof Message) {
            Platform.runLater(() -> txtArea.appendText(args.toString() + "\n"));
        }
        else if (Sender instanceof Share) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Share share = (Share)Sender;
            Message message = new Message();

            message.setId(UUID.randomUUID());
            message.setCommand("ShareValueChanged");
            message.setFrom(share.getName());
            message.setBody("Time: " + LocalDateTime.now().format(formatter) + " Value: " + share.getValue() + " EUR");
            MessageDistributor.getInstance().addMessage(message);
        }
    }

    public void onStart(ActionEvent actionEvent) {
        if (serverSocket == null) {
            int port = Integer.parseInt(txtPort.getText());

            share = new Share("MSFT", 100.0);
            share.addObserver(this);
            share.start();

            txtArea.clear();

            Thread t = new Thread(() -> {
                try {
                    serverSocket = new ServerSocket(port);

                    while (!serverSocket.isClosed()) {
                        Socket socket = serverSocket.accept();
                        SocketThread socketThread = new SocketThread(socket);

                        MessageDistributor.getInstance().addObserver(socketThread);
                        socketThread.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                serverSocket = null;
            });

            t.setDaemon(true);
            t.start();

            txtPort.setDisable(true);
            btnStart.setDisable(true);
            btnStop.setDisable(false);
            System.out.println("Chat srver is running");
        }
    }

    public void onStop(ActionEvent actionEvent) {
        if (serverSocket != null) {

            share.stop();
            share.removeObserver(this);
            share = null;

            try {

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
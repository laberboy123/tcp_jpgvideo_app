package com.example.newtcpapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements TCPServer.Callback {
    private TextView serverInfoTextView;
    private TextView connectionStatusTextView;
    private TextView messageTextView;
    private TCPServer tcpServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        serverInfoTextView = findViewById(R.id.serverInfoTextView);
        connectionStatusTextView = findViewById(R.id.connectionStatusTextView);
        messageTextView = findViewById(R.id.messageTextView);

        tcpServer = new TCPServer(this);
        tcpServer.startServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tcpServer != null) {
            tcpServer.stopServer();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onServerStarted(String ip, int port) {
        serverInfoTextView.setText("服务器信息：" + ip + ":" + port);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClientConnected(String clientIp) {
        connectionStatusTextView.setText("客户端连接: " + clientIp);
    }

    @Override
    public void onMessageReceived(String message) {
        messageTextView.setText(message);
    }

    @Override
    public void onJpgDataReceived(byte[] jpgData) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onError(String errorMessage) {
        connectionStatusTextView.setText("服务器错误: " + errorMessage);
    }
}
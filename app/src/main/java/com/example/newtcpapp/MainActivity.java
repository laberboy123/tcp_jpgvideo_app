package com.example.newtcpapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
        tcpServer.stopServer();
    }

    @Override
    public void onServerStarted(String ip, int port) {
        serverInfoTextView.setText("服务器信息：" + ip + ":" + port);
    }

    @Override
    public void onClientConnected(String clientIp) {
        connectionStatusTextView.setText("客户端连接: " + clientIp);
    }

    @Override
    public void onMessageReceived(String message) {
        messageTextView.setText(message);
    }

    @Override
    public void onError(String errorMessage) {
        connectionStatusTextView.setText("服务器错误: " + errorMessage);
    }
}
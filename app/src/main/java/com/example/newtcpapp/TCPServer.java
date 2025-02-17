package com.example.newtcpapp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
    private static final String TAG = "TCPServer";
    private static final int PORT = 1234;
    private static final byte END_FLAG = (byte) 0xFF; // 定义结束标志
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private Handler handler;
    private Callback callback;

    public interface Callback {
        void onServerStarted(String ip, int port);
        void onClientConnected(String clientIp);
        void onMessageReceived(String message);
        void onError(String errorMessage);
    }

    public TCPServer(Callback callback) {
        this.callback = callback;
        this.handler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void startServer() {
        executorService.execute(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                String ip = getLocalIpAddress();
                handler.post(() -> callback.onServerStarted(ip, PORT));
                Log.d(TAG, "服务器启动: " + ip + ":" + PORT);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    String clientIp = clientSocket.getInetAddress().getHostAddress();
                    handler.post(() -> callback.onClientConnected(clientIp));
                    Log.d(TAG, "客户端连接: " + clientIp);

                    InputStream inputStream = clientSocket.getInputStream();
                    StringBuilder messageBuilder = new StringBuilder();
                    int byteRead;
                    while ((byteRead = inputStream.read()) != -1) {
                        if ((byte) byteRead == END_FLAG) {
                            final String message = messageBuilder.toString();
                            handler.post(() -> callback.onMessageReceived(message));
                            Log.d(TAG, "Received message: " + message);
                            messageBuilder.setLength(0); // 清空 StringBuilder 以便接收下一条消息
                        } else {
                            messageBuilder.append((char) byteRead);
                        }
                    }
                    clientSocket.close();
                }
            } catch (IOException e) {
                final String errorMessage = "Server error: " + e.getMessage();
                handler.post(() -> callback.onError(errorMessage));
                Log.e(TAG, errorMessage);
            }
        });
    }

    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            executorService.shutdownNow();
        } catch (IOException e) {
            final String errorMessage = "Error stopping server: " + e.getMessage();
            handler.post(() -> callback.onError(errorMessage));
            Log.e(TAG, errorMessage);
        }
    }

    private String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isUp()) {
                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (!addr.isLoopbackAddress() && addr.getAddress().length == 4) {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting IP address: " + e.getMessage());
        }
        return null;
    }
}
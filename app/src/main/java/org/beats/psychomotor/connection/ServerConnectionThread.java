package org.beats.psychomotor.connection;


import android.util.Log;

import org.beats.psychomotor.fragment.HostFragment;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class ServerConnectionThread extends Thread {
    private static final String TAG = "ServerConnectionThread";

    static final int SocketServerPORT = 23232;
    public static HashMap<Socket, String> socketUserMap = new HashMap();
    public static boolean serverStarted = false;
    public static ServerSocket serverSocket;

    public ServerConnectionThread() {

    }

    @Override
    public void run() {
        Log.d(TAG, "run: server trying to start");
        if (serverSocket == null) {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                Log.d(TAG, "run: server started");
                serverStarted = true;
                while (true) {
                    Log.d(TAG, "run: waiting connection");
                    Socket socket = serverSocket.accept();
                    Log.d(TAG, "run: connection accepted from "+socket.getInetAddress().getHostAddress());
                    Thread socketListenThread = new Thread(new ServerListenerThread(socket));
                    socketListenThread.start();
                    Log.d(TAG, "run: listening to socket");
                    ServerSenderThread sendGameName = new ServerSenderThread(socket, "Psychomotor");
                    sendGameName.start();
                    Log.d(TAG, "run: sent gameName");
                    socketUserMap.put(socket, socket.getInetAddress().getHostAddress());
                    Log.d(TAG, "run: usermapNow : "+socketUserMap.toString());
                    Log.d(TAG, "run: devicelist: "+ HostFragment.deviceList);
                }
            } catch (IOException e) {
                serverSocket = null;
                Log.d(TAG, "run: socket closed");
            }
        }
    }
}

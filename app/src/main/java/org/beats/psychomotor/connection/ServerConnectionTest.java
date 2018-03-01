package org.beats.psychomotor.connection;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Dani on 22/02/2018.
 */

public class ServerConnectionTest extends Thread {
    private static final String TAG = "ServerConnectionTest";

    static final int SocketServerPORT = 23233;
    public static ServerSocket testSocket;

    public ServerConnectionTest() {

    }

    @Override
    public void run() {
        if (testSocket == null) {
            try {
                testSocket = new ServerSocket(SocketServerPORT);
                while (true) {
                    Log.d(TAG, "run: waiting scan");
                    Socket socket = testSocket.accept();
                    socket.close();
                }
            } catch (IOException e) {
                testSocket = null;
                Log.d(TAG, "run: socket closed");
            }
        }
    }
}

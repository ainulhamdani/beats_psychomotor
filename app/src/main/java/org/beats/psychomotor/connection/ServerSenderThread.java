package org.beats.psychomotor.connection;

import android.util.Log;

import org.beats.psychomotor.fragment.AssessmentFragment;
import org.beats.psychomotor.fragment.HostFragment;
import org.beats.psychomotor.model.Assessment;
import org.beats.psychomotor.model.Block;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerSenderThread extends Thread {
    public static String TAG = "ServerSenderThread";

    private Socket hostThreadSocket;
    Object message;

    public ServerSenderThread(Socket socket, Object message) {
        hostThreadSocket = socket;
        this.message = message;
    }

    @Override
    public void run() {
        OutputStream outputStream;
        ObjectOutputStream objectOutputStream;

        try {
            outputStream = hostThreadSocket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(message);
            if (message instanceof Assessment) {
                HostFragment.gameObject = (Assessment) message;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}

package org.beats.psychomotor.connection;

import org.beats.psychomotor.fragment.ClientFragment;
import org.beats.psychomotor.model.Assessment;
import org.beats.psychomotor.model.Client;
import org.beats.psychomotor.utils.Constants;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientSenderThread extends Thread {

    private Socket hostThreadSocket;
    Object message;
    public static boolean isActive = true;

    public ClientSenderThread(Socket socket, Object message) {
        hostThreadSocket = socket;
        this.message = message;
    }

    @Override
    public void run() {
        OutputStream outputStream;
        ObjectOutputStream objectOutputStream;
        if (hostThreadSocket.isConnected()) {
            try {
                if (isActive) {
                    if (message instanceof Assessment && !Constants.isPlayerActive(ClientFragment.username, (Assessment) message)) {
                        isActive = false;
                    }
                    outputStream = hostThreadSocket.getOutputStream();
                    objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }



}

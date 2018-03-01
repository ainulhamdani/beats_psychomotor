package org.beats.psychomotor.connection;


import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.beats.psychomotor.model.Client;
import org.beats.psychomotor.model.ClientInfo;
import org.beats.psychomotor.utils.WifiHelper;

public class ClientConnectionThread extends Thread {
    private static final String TAG = "ClientConnectionThread";
    public static Socket socket;
    String dstAddress;
    int dstPort = 23232;
    public static boolean serverStarted = false;
    String userName;
    ClientListenerThread clientListener;
    ClientSenderThread sendclientInfo;
    ClientInfo clientInfo;

    public ClientConnectionThread(String userName) {
        this.userName = userName;
    }

    public ClientConnectionThread(String userName,String destAddress) {
        this.userName = userName;
        this.dstAddress = destAddress;
        socket = new Socket();
        clientInfo = new ClientInfo(userName);
    }

    public ClientConnectionThread setDstAddress(String destAddress){
        this.dstAddress = destAddress;
        return this;
    }

    @Override
    public void run() {
        if (!socket.isConnected()) {
            Log.d(TAG, "run: socker null");
            try {
                if (dstAddress != null) {
                    Log.d(TAG, "run: destination : "+dstAddress);
                    socket = new Socket(dstAddress, dstPort);
                    if (socket.isConnected()) {
                        Log.d(TAG, "run: socket connected");
                        serverStarted = true;
                        clientListener = new ClientListenerThread(socket);
                        clientListener.start();
                        Log.d(TAG, "run: listener started");
                        sendclientInfo = new ClientSenderThread(socket, clientInfo);
                        sendclientInfo.start();
                        Log.d(TAG, "run: username sent");
                    }else{
                        Log.d(TAG, "run: socket not connected");
                        serverStarted = false;
                    }
                }
            } catch (UnknownHostException e) {
                Log.d(TAG, "run: UnknownHostException");
                serverStarted = false;
//                e.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG, "run: IOException");
                serverStarted = false;
//                e.printStackTrace();
            }
        }else{
            Log.d(TAG, "run: socket not null");
        }
    }

    public void disconnect(){
        try {
            sendclientInfo = new ClientSenderThread(socket, clientInfo.setActive(false));
            sendclientInfo.start();
            Log.d(TAG, "disconnect: sending disconnect message");
            sendclientInfo.join();
            closeSocket();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket(){
        if(!socket.isClosed()){
            try {
                socket.close();
                if(clientListener!=null){
                    clientListener.closeSocket();
                }
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
    }

}

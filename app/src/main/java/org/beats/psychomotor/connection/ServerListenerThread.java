package org.beats.psychomotor.connection;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import org.beats.psychomotor.fragment.HostFragment;
import org.beats.psychomotor.model.Assessment;
import org.beats.psychomotor.model.Block;
import org.beats.psychomotor.model.ClientFinish;
import org.beats.psychomotor.model.ClientInfo;
import org.beats.psychomotor.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ServerListenerThread extends Thread {
    public static String TAG = "ServerListenerThread";

    private Socket hostThreadSocket;

    ServerListenerThread(Socket soc) {
        hostThreadSocket = soc;
    }

    @Override
    public void run() {
        while (true) {
            ObjectInputStream objectInputStream;
            try {
                InputStream inputStream = null;
                inputStream = hostThreadSocket.getInputStream();
                objectInputStream = new ObjectInputStream(inputStream);
                Object gameObject;
                Bundle data = new Bundle();
                gameObject = objectInputStream.readObject();
                if (gameObject != null) {
                    if (gameObject instanceof ClientFinish) {
                        data.putSerializable(Constants.DATA_KEY, (ClientFinish) gameObject);
                        data.putInt(Constants.ACTION_KEY, Constants.FINISH);
                    }else if (gameObject instanceof ClientInfo) {
                        data.putSerializable(Constants.DATA_KEY, (ClientInfo) gameObject);
                        data.putInt(Constants.ACTION_KEY, Constants.PLAYER_LIST_UPDATE);
                        if(((ClientInfo) gameObject).isActive){
                            ServerConnectionThread.socketUserMap.put(hostThreadSocket, ((ClientInfo) gameObject).username);
                        }else{
                            ServerConnectionThread.socketUserMap.remove(hostThreadSocket);
                        }

                    }else if (gameObject instanceof Block) {
                        data.putInt(Constants.ACTION_KEY, Constants.SEND_BLOCK);
                        data.putSerializable(Constants.DATA_KEY, (Block) gameObject);
                    }else if (gameObject instanceof Assessment) {
                        data.putSerializable(Constants.DATA_KEY, (Assessment) gameObject);
                    }
                    Message msg = new Message();
                    msg.setData(data);
                    HostFragment.serverHandler.sendMessage(msg);
                }

            } catch (IOException e) {
                try {
                    hostThreadSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Log.d("ServerListenerThread", "run: IOException");
                break;
//                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                Log.d("ServerListenerThread", "run: class not found");
                e.printStackTrace();
            }
        }
    }
}

package org.beats.psychomotor.connection;


import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import org.beats.psychomotor.fragment.ClientFragment;
import org.beats.psychomotor.model.Assessment;
import org.beats.psychomotor.model.Block;
import org.beats.psychomotor.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientListenerThread extends Thread {
    public static String TAG = "ClientListenerThread";

    Socket socket;

    ClientListenerThread(Socket soc) {
        socket = soc;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if(!socket.isClosed()){
                    ObjectInputStream objectInputStream;
                    InputStream inputStream = null;
                    inputStream = socket.getInputStream();
                    objectInputStream = new ObjectInputStream(inputStream);
                    Bundle data = new Bundle();
                    Object serverObject = (Object) objectInputStream.readObject();
                    if (serverObject != null) {
                        if (serverObject instanceof String) {
                            String string = (String) serverObject;
                            if (string.equals("finish")){
                                data.putSerializable(Constants.DATA_KEY, (String) serverObject);
                                data.putInt(Constants.ACTION_KEY, Constants.FINISH);
                            }else{
                                data.putSerializable(Constants.DATA_KEY, (String) serverObject);
                                data.putInt(Constants.ACTION_KEY, Constants.UPDATE_GAME_NAME);
                            }

                        }
                        if (serverObject instanceof Assessment) {
                            data.putSerializable(Constants.DATA_KEY, (Assessment) serverObject);
                        }
                        if (serverObject instanceof Block){
                            data.putSerializable(Constants.DATA_KEY, (Block) serverObject);
                        }
                        Message msg = new Message();
                        msg.setData(data);
                        ClientFragment.clientHandler.sendMessage(msg);
                    }
                }

            }
        } catch (IOException e) {
            Log.d("ClientListenerThread", "run: listener socket closed");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket(){
        if(!socket.isClosed()){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

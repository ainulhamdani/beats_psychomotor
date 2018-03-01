package org.beats.psychomotor.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import org.beats.psychomotor.connection.ClientConnectionThread;
import org.beats.psychomotor.connection.ClientSenderThread;
import org.beats.psychomotor.fragment.AssessmentFragment;
import org.beats.psychomotor.fragment.ClientFragment;
import org.beats.psychomotor.fragment.TouchFragment;
import org.beats.psychomotor.model.Assessment;
import org.beats.psychomotor.model.Block;
import org.beats.psychomotor.model.Client;


public class ClientHandler extends Handler {
    public static String TAG = "ClientHandler";

    Bundle messageData;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        messageData = msg.getData();
        int value = messageData.getInt(Constants.ACTION_KEY);
        Object clientObject = messageData.getSerializable(Constants.DATA_KEY);
        if (value == Constants.FINISH) {
            AssessmentFragment.endClientSession();
        }
        if (value == Constants.UPDATE_GAME_NAME) {
            String gameName = (String) clientObject;
            ClientFragment.hostName.setText(gameName);
            ClientFragment.disconnect.setVisibility(View.VISIBLE);
            ClientFragment.scan.setVisibility(View.GONE);
            ClientFragment.stopScan.setVisibility(View.GONE);
        }
        if (clientObject instanceof Assessment) {
            if (AssessmentFragment.gameObject != null) {
                AssessmentFragment.gameObject = (Assessment) clientObject;
                if (((Assessment) clientObject).senderUsername.equals(String.valueOf(Constants.NEW_GAME))) {
                    ClientSenderThread.isActive = true;
                    ClientFragment.gameObject = (Assessment) clientObject;
                    ClientFragment.goToTouchFragment();
//                    ((Game) clientObject).senderUsername = "";
                }
            } else {
                ClientFragment.gameObject = (Assessment) clientObject;
                ClientFragment.goToTouchFragment();
            }
        }
        if(clientObject instanceof Block){
            AssessmentFragment.updateBlock((Block) clientObject);
        }
    }

    public static void sendToServer(Object gameObject) {
        ClientSenderThread sendGameChange = new ClientSenderThread(ClientConnectionThread.socket, gameObject);
        sendGameChange.start();
    }
}

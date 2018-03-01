package org.beats.psychomotor.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.Socket;
import java.util.Iterator;

import org.beats.psychomotor.connection.ServerConnectionThread;
import org.beats.psychomotor.connection.ServerSenderThread;
import org.beats.psychomotor.fragment.AssessmentFragment;
import org.beats.psychomotor.fragment.HostFragment;
import org.beats.psychomotor.model.Assessment;
import org.beats.psychomotor.model.Block;
import org.beats.psychomotor.model.ClientFinish;
import org.beats.psychomotor.model.ClientInfo;
import org.beats.psychomotor.fragment.TouchFragment;


public class ServerHandler extends Handler {

    public static String TAG = "ServerHandler";


    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Bundle messageData = msg.getData();
        Object gameObject = messageData.getSerializable(Constants.DATA_KEY);

        if (gameObject instanceof ClientFinish) {
            ClientFinish clientFinish = (ClientFinish) gameObject;
            if(clientFinish.isFinish){
                AssessmentFragment.gameObject.getClient(((ClientFinish) gameObject).username).isFinished(true);
            }else{
                AssessmentFragment.gameObject.getClient(((ClientFinish) gameObject).username).isFinished(false);
            }
            AssessmentFragment.endSession();

        }
        if (gameObject instanceof ClientInfo) {
            ClientInfo playerInfo = (ClientInfo) gameObject;
            Log.d("ServerHandler", "handleMessage: joined "+playerInfo.username);
            if(playerInfo.isActive){
                HostFragment.deviceList.add(playerInfo.username);
                HostFragment.mAdapter.notifyItemInserted(HostFragment.deviceList.size() - 1);
            }else{
                HostFragment.deviceList.remove(playerInfo.username);
                HostFragment.mAdapter.notifyDataSetChanged();
            }


        }
        if (gameObject instanceof Assessment) {
            if (TouchFragment.gameObject != null) {
                TouchFragment.gameObject = (Assessment) gameObject;
                TouchFragment.updateCoordinate();
                sendToAll(gameObject);
            } else {
                HostFragment.gameObject = (Assessment) gameObject;
            }
        }

        if(gameObject instanceof Block){
            Log.d(TAG, "handleMessage: handle block");
            AssessmentFragment.updateBlock((Block) gameObject);
            HostFragment.saveBlock((Block)gameObject);
            Log.d(TAG, "handleMessage: send back block");
            sendToAll(gameObject);
        }


    }

    public static void sendToAll(Object gameObject) {
        Iterator<Socket> socketIterator = ServerConnectionThread.socketUserMap.keySet().iterator();
        Socket socket;
        while (socketIterator.hasNext()) {
            socket = socketIterator.next();
            if(gameObject instanceof String){
                if(((String)gameObject).equals("finish")){
                    ServerSenderThread sendFinish = new ServerSenderThread(socket, gameObject);
                    sendFinish.start();
                }
            }else if(gameObject instanceof Block){
                Log.d(TAG, "sendToAll: send Block");
                ServerSenderThread sendGameName = new ServerSenderThread(socket, gameObject);
                sendGameName.start();
            }else if (!ServerConnectionThread.socketUserMap.get(socket).equals(((Assessment) gameObject).senderUsername)) {
                Log.d(TAG, "sendToAll: send Assessment");
                ServerSenderThread sendGameName = new ServerSenderThread(socket, gameObject);
                sendGameName.start();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

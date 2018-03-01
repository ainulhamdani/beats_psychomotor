package org.beats.psychomotor.model;

import java.io.Serializable;

public class Client implements Serializable {
    public int playerID;
    public String username;
    public boolean isActive;
    public boolean isFinish;

    Client(int playerID, String username, boolean isActive) {
        this.playerID = playerID;
        this.username = username;
        this.isActive = isActive;
        this.isFinish = false;
    }

    public void isFinished(boolean isFinish){
        this.isFinish = isFinish;
    }
}

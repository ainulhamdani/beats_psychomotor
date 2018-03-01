package org.beats.psychomotor.model;

import java.io.Serializable;

/**
 * Created by Dani on 28/02/2018.
 */

public class ClientFinish implements Serializable {
    public String username;
    public boolean isFinish;

    public ClientFinish(String username, boolean isFinish) {
        this.username = username;
        this.isFinish = isFinish;
    }

    public ClientFinish setFinish(boolean isActive){
        this.isFinish = isActive;
        return this;
    }
}

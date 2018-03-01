package org.beats.psychomotor.model;

import java.io.Serializable;

public class ClientInfo implements Serializable {
    public String username;
    public boolean isActive;

    public ClientInfo(String username) {
        this.username = username;
        this.isActive = true;
    }

    public ClientInfo setActive(boolean isActive){
        this.isActive = isActive;
        return this;
    }
}

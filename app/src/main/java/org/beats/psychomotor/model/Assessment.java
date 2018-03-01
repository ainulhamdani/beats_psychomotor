package org.beats.psychomotor.model;

import java.io.PushbackInputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Dani on 20/02/2018.
 */

public class Assessment implements Serializable {

    public ArrayList<Client> clients;
    private int numberOfPlayer;
    public String assessmentName;
    public String senderUsername;
    public String coordinate;

    public Assessment(ArrayList<String> usernames, String gameName){
        this.senderUsername = null;
        this.clients = new ArrayList();
        this.numberOfPlayer = usernames.size();
        this.assessmentName = gameName;
        for (int i = 0; i < numberOfPlayer; i++) {
            clients.add(new Client(i + 1, usernames.get(i), true));
        }
    }

    public ArrayList<Client> getClients() {
        return clients;
    }
    public Client getClient(String username){
        for (Client client:clients) {
           if(client.username.equals(username)){
               return client;
           }
        }
        return null;
    }
    public void setCoordinate(String coor){
        coordinate = coor;
    }
    public String getCoordinate(){
        return coordinate;
    }
}

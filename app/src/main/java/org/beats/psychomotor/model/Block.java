package org.beats.psychomotor.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by Dani on 25/02/2018.
 */

public class Block implements Serializable {

    String pos;
    int x,y;
    int color;
    public String senderUsername;

    public Block(String uname,String pos,int x,int y,int color){
        this.senderUsername = uname;
        this.pos=pos;
        this.color=color;
        this.x=x;
        this.y=y;
    }

    public String getPos(){
        return pos;
    }

    public int getColor(){
        return color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getSenderUsername(){
        return senderUsername;
    }
}

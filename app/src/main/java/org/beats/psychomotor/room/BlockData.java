package org.beats.psychomotor.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Dani on 28/02/2018.
 */


@Entity
public class BlockData {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "assessment_id")
    private String assessmentId;

    @ColumnInfo(name = "task_id")
    private String taskId;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "color")
    private String color;

    @ColumnInfo(name = "pos_x")
    private int posX;

    @ColumnInfo(name = "pos_y")
    private int posY;

    @ColumnInfo(name = "timestamp")
    private String timestamp;

    @ColumnInfo(name = "synced")
    private int synced = 0;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }
}

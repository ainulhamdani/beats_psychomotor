package org.beats.psychomotor.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Dani on 01/03/2018.
 */

@Entity
public class AssessmentData {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "assessment_id")
    private String assessmentId;

    @ColumnInfo(name = "task_id")
    private String taskId;

    @ColumnInfo(name = "start")
    private String start;

    @ColumnInfo(name = "end")
    private String end;

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

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }
}

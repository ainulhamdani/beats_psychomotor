package org.beats.psychomotor.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Dani on 01/03/2018.
 */

@Dao
public interface AssessmentDataDao {

    @Query("SELECT * FROM assessmentdata")
    List<AssessmentData> getAll();

    @Query("SELECT * FROM assessmentdata WHERE synced=0")
    List<AssessmentData> getAllUnsync();

    @Query("SELECT * FROM assessmentdata WHERE assessment_id=:assessmentId AND task_id=:taskId")
    AssessmentData getAssessment(String assessmentId, String taskId);

    @Insert
    void insertAll(AssessmentData... assessmentData);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAssessment(AssessmentData assessmentData);

    @Delete
    void delete(AssessmentData assessmentData);
}

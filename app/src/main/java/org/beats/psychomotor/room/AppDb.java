package org.beats.psychomotor.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Dani on 28/02/2018.
 */
@Database(entities = {BlockData.class,AssessmentData.class}, version = 1)
public abstract class AppDb extends RoomDatabase {
    public abstract BlockDataDao blockDataDao();
    public abstract AssessmentDataDao assessmentDataDao();
}

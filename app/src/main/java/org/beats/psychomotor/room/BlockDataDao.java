package org.beats.psychomotor.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Dani on 28/02/2018.
 */

@Dao
public interface  BlockDataDao {
    @Query("SELECT * FROM blockdata")
    List<BlockData> getAll();

    @Query("SELECT * FROM blockdata WHERE synced=0")
    List<BlockData> getAllUnsync();

    @Query("SELECT * FROM blockdata WHERE uid IN (:blockIds)")
    List<BlockData> loadAllByIds(int[] blockIds);

    @Query("SELECT * FROM blockdata WHERE uid=:id")
    BlockData getBlock(int id);

    @Insert
    void insertAll(BlockData... blocks);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateBlock(BlockData block);

    @Delete
    void delete(BlockData block);
}

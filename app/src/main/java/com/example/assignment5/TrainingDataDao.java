package com.example.assignment5;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface TrainingDataDao {
    @Query("SELECT * FROM trainingdata")
    List<TrainingData> getAll();

    @Query("SELECT * FROM trainingdata where id=1")
    TrainingData getOne();

    @Query("DELETE FROM trainingdata")
    void delete();

    @Insert
    void insertAll(TrainingData td);

}
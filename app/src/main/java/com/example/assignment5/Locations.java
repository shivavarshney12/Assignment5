package com.example.assignment5;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import androidx.room.Database;

@Database(entities= {TrainingData.class},exportSchema=true, version=1)
public abstract class Locations extends RoomDatabase {
    private static final String DB_NAME ="allLocations";
    private static Locations instance;

    public static synchronized Locations getInstance(Context context) {
        if (instance == null) {
            instance= Room.databaseBuilder(context.getApplicationContext(),Locations.class,DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract TrainingDataDao trainingDataDao();

}
package com.kenjoel;

import android.app.Application;

import androidx.room.Room;
import androidx.room.util.DBUtil;

import com.kenjoel.dbUtils.LocalDb;

public class RoomImplementation extends Application {
    private static RoomImplementation instance;
    private LocalDb dbInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        dbInstance = Room.databaseBuilder(getApplicationContext(), LocalDb.class, "LocalDb").build();
    }

    public static RoomImplementation getInstance(){
        return instance;
    }

    public  LocalDb getDbInstance(){
        return dbInstance;
    }
}


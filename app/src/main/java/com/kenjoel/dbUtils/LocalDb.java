package com.kenjoel.dbUtils;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {UserInfo.class}, version = 1)
public abstract class LocalDb extends RoomDatabase {
    public abstract DAO dao();

}

package com.example.optimizedatabase.roomdatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.optimizedatabase.dataobject.FileDO;


@Database(entities = {FileDO.class},version = 1,exportSchema = false)
public abstract class AppDataBase  extends RoomDatabase {

    public abstract FileDao fileDao();
}
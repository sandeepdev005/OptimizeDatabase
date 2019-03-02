package com.example.optimizedatabase.roomdatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.example.optimizedatabase.dataobject.FileDO;

import java.util.List;

@Dao
public interface FileDao {

    @Query("SELECT * FROM FileDO")
    List<FileDO> getAll();

    @Insert
    void insert(FileDO fileDO);


    @Insert
    void insert(List<FileDO> fileDO);

    @Delete
    void delete(FileDO fileDO);

    @Update
    void update(FileDO fileDO);
}

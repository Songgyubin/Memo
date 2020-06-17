package com.android.myapplication;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
interface MemoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMemo(Memo memo);

    @Query("DELETE FROM memo WHERE id = :id")
    void deleteMemoById(Long id);

//    @Query("UPDATE FROM memo WHERE id = :id")


    @Query("SELECT * FROM memo ORDER BY date DESC")
    LiveData<List<Memo>> getAllMemos();

}

package com.android.myapplication;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


// Room DB 객체
@Database(entities = {Memo.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MemoDao memoDao();

    // Room DB 싱글톤
    private static AppDatabase INSTANCE;

    private static final Object sLock = new Object();

    public static AppDatabase getInstance(Context context){
        synchronized (sLock){
            if (INSTANCE==null){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class,"memo.db")
                        .build();
            }
            return INSTANCE;
        }
    }
}

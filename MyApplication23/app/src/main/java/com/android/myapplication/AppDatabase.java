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

    public static AppDatabase getInstance(Context context){

            if (INSTANCE==null){
                synchronized (AppDatabase.class){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class,"memo.db")
                        .build();
                }
            }
            return INSTANCE;
    }
    public static void destroyInstance(){
        INSTANCE = null;
    }
}

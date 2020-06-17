package com.android.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "memo")
public class Memo {


    @PrimaryKey(autoGenerate = true)
    private int id = 0;
    private String title = "";
    private String content = "";
    private String image = "";
    private String date = "";
    private boolean isChecked = false;

    public Memo(int id, String title, String content, String image, String date, boolean isChecked) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.image = image;
        this.date = date;
        this.isChecked = isChecked;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getImage() {
        return image;
    }

    public String getDate() {
        return date;
    }
}

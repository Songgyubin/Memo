package com.android.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "memo")
public class Memo implements Serializable {


    @PrimaryKey(autoGenerate = true)
    private int id= 0;
    private String title;
    private String content ;
    private String image="";
    private String date;
    private boolean isChecked = false;

    /*public Memo(String title, String content, String image, String date, boolean isChecked) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.date = date;
        this.isChecked = isChecked;
    }*/

    public Memo(int id, String title, String content, String image, String date, boolean isChecked) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.image = image;
        this.date = date;
        this.isChecked = isChecked;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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

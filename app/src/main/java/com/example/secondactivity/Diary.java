package com.example.secondactivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.Date;

public class Diary {
    private int id;
    private String title;
    private String content;
    private String date;
    private String author;
    private String picture;
    public Diary(int id,String title,String content,String date,String author,String picture){
        this.id=id;
        this.title=title;
        this.content=content;
        this.date=date;
        this.author=author;
        this.picture=picture;
    }
    public Diary(){
        this.id=-1;
        this.title="";
        this.content="";
        this.date="";
        this.author="author";
        this.picture="";}
    public String getTitle(){
        return this.title;
    }

    public String  getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public String getPicture() {
        return picture;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}

package com.example.assignment.Model;

import android.net.Uri;

public class notificationModel {
    private String content;
    private String date;
    private String name;
    private String time;
    private String title;

    public notificationModel(){}

    public notificationModel(String content, String date, String name, String time, String title){
        this.content = content;
        this.date = date;
        this.name = name;
        this.time = time;
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    //public void setContent(String content) {
        //this.content = content;
    //}

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

}

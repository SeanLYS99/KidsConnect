package com.example.assignment.Model;

import android.net.Uri;

public class notificationModel {
    private String id;
    private String isClicked;
    private String content;
    private String datetime;
    private String name;
    //private String time;
    private String title;

    public notificationModel(){}

    public notificationModel(String id, String isClicked, String content, String datetime, String name, String title){
        this.id = id;
        this.isClicked = isClicked;
        this.content = content;
        this.datetime = datetime;
        this.name = name;
        //this.time = time;
        this.title = title;
    }

    public String getId(){
        return id;
    }

    public String getIsClicked(){
        return isClicked;
    }

    public String getContent() {
        return content;
    }

    //public void setContent(String content) {
        //this.content = content;
    //}

    public String getDatetime() {
        return datetime;
    }

    public String getName() {
        return name;
    }

    /*public String getTime() {
        return time;
    }*/

    public String getTitle() {
        return title;
    }

}

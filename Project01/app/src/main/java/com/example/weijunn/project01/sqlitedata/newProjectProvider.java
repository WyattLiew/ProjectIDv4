package com.example.weijunn.project01.sqlitedata;

import java.io.Serializable;

public class newProjectProvider implements Serializable {
    private String location;
    private String name;
    private String number;
    private String title;
    private String description;
    private String notes;
    private String date;
    private long id;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public newProjectProvider(long id, String title, String description,String name,String number,String date, String location,String notes){
        this.id =id;
        this.title = title;
        this.description =description;
        this.name = name;
        this.number = number;
        this.location = location;
        this.date = date;
        this.notes = notes;
    }
}

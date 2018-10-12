package com.example.weijunn.project01.model;

import com.example.weijunn.project01.sqlitedata.newProjectProvider;

public class ProjectAddOnProvider {

    public static final String TAG = "ProjectAddOnProvider";

    private long id;
    private byte[] img;
    private String notes;
    private String date;
    private int Status;
    private int projectID;

    public ProjectAddOnProvider(long id,int status,String date,String notes,byte[]img,int projectID){
        this.id =id;
        this.Status = status;
        this.date=date;
        this.notes =notes;
        this.img = img;
        this.projectID =projectID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
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

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }
}



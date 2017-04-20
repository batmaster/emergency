package com.example.application.emergency.activities.list;

public class ListModel {
    private int id;
    private int typeId;
    private String type;
    private String title;
    private String detail;
    private double locationX;
    private double locationY;
    private int status;
    private String date;
    private String approveDate;

    private String color;

    public ListModel(int id, int typeId, String type, String title, String detail, double locationX, double locationY, int status, String date, String approveDate, String color) {
        this.id = id;
        this.typeId = typeId;
        this.type = type;
        this.title = title;
        this.detail = detail;
        this.locationX = locationX;
        this.locationY = locationY;
        this.status = status;
        this.date = date;
        this.approveDate = approveDate;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public double getLocationX() {
        return locationX;
    }

    public void setLocationX(double locationX) {
        this.locationX = locationX;
    }

    public double getLocationY() {
        return locationY;
    }

    public void setLocationY(double locationY) {
        this.locationY = locationY;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(String approveDate) {
        this.approveDate = approveDate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

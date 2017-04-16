package com.example.application.emergency.activities;

public class ListModel {
    private int typeId;
    private String type;
    private String title;
    private int peopleId;
    private String people;
    private int officerId;
    private String officer;
    private String photo;
    private String detail;
    private double locationX;
    private double locationY;
    private int status;
    private String date;
    private String approveDate;

    public ListModel(int typeId, String type, String title, int peopleId, String people, int officerId, String officer, String photo, String detail, double locationX, double locationY, int status, String date, String approveDate) {
        this.typeId = typeId;
        this.type = type;
        this.title = title;
        this.peopleId = peopleId;
        this.people = people;
        this.officerId = officerId;
        this.officer = officer;
        this.photo = photo;
        this.detail = detail;
        this.locationX = locationX;
        this.locationY = locationY;
        this.status = status;
        this.date = date;
        this.approveDate = approveDate;
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

    public int getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(int peopleId) {
        this.peopleId = peopleId;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public int getOfficerId() {
        return officerId;
    }

    public void setOfficerId(int officerId) {
        this.officerId = officerId;
    }

    public String getOfficer() {
        return officer;
    }

    public void setOfficer(String officer) {
        this.officer = officer;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public void setLocationX(float locationX) {
        this.locationX = locationX;
    }

    public double getLocationY() {
        return locationY;
    }

    public void setLocationY(float locationY) {
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
}

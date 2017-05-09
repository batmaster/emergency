package com.example.application.emergency.activities.list;

/**
 * class สำหรับบรรจุข้อมูลรายการการแจ้งเตือนที่ดาวน์โหลดมาจาก server
 */
public class ListModel {

    /** ประกาศตัวแปร **/
    private int id;
    private int typeId;
    private String type;
    private String title;
    private String userId;
    private String officerId;
    private double locationX;
    private double locationY;
    private int status;
    private String date;
    private String dateApprove;

    private String color;
    private String typeImage;

    public ListModel(int id, int typeId, String type, String title, String userId, String officerId, double locationX, double locationY, int status, String date, String dateApprove, String color, String typeImage) {
        this.id = id;
        this.typeId = typeId;
        this.type = type;
        this.title = title;
        this.userId = userId;
        this.officerId = officerId;
        this.locationX = locationX;
        this.locationY = locationY;
        this.status = status;
        this.date = date;
        this.dateApprove = dateApprove;
        this.color = color;
        this.typeImage = typeImage;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOfficerId() {
        return officerId;
    }

    public void setOfficerId(String officerId) {
        this.officerId = officerId;
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

    public String getDateApprove() {
        return dateApprove;
    }

    public void setDateApprove(String dateApprove) {
        this.dateApprove = dateApprove;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTypeImage() {
        return typeImage;
    }

    public void setTypeImage(String typeImage) {
        this.typeImage = typeImage;
    }
}

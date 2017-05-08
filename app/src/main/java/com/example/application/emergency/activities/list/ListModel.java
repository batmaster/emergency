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
    private String phone;
    private double locationX;
    private double locationY;
    private int status;
    private String date;

    private String color;
    private String typeImage;

    public ListModel(int id, int typeId, String type, String title, String phone, double locationX, double locationY, int status, String date, String color, String typeImage) {
        this.id = id;
        this.typeId = typeId;
        this.type = type;
        this.title = title;
        this.phone = phone;
        this.locationX = locationX;
        this.locationY = locationY;
        this.status = status;
        this.date = date;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

package com.example.application.emergency.activities.user;

/**
 * class สำหรับบรรจุข้อมูลรายการการแจ้งเตือนที่ดาวน์โหลดมาจาก server
 */
public class UserModel {

    /** ประกาศตัวแปร **/
    private String userId;
    private String currentName;
    private int type;
    private int status;
    private String lastUseDate;

    public UserModel(String userId, String currentName, int type, int status, String lastUseDate) {
        this.userId = userId;
        this.currentName = currentName;
        this.type = type;
        this.status = status;
        this.lastUseDate = lastUseDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCurrentName() {
        return currentName;
    }

    public void setCurrentName(String currentName) {
        this.currentName = currentName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLastUseDate() {
        return lastUseDate;
    }

    public void setLastUseDate(String lastUseDate) {
        this.lastUseDate = lastUseDate;
    }

    @Override
    public String toString() {
        return currentName;
    }
}

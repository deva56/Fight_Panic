package com.example.fightpanicnew.Models;

public class Room {

    private String roomName;
    private String roomDescription;
    private String roomPassword;
    private String admin;
    private boolean roomPrivate;
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public String getPassword() {
        return roomPassword;
    }

    public void setPassword(String roomPassword) {
        this.roomPassword = roomPassword;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public boolean getRoomPrivate() {
        return roomPrivate;
    }

    public void setRoomPrivate(boolean type) {
        this.roomPrivate = type;
    }
}

package com.example.fightpanicnew.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chatMessages_table")
public class ChatMessages {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userName;
    private String messageContent;
    private String roomName;
    private int viewType;
    private String bitmapPath;
    private String profilePictureURL;

    public ChatMessages(String userName, String messageContent, String roomName, int viewType) {
        this.userName = userName;
        this.messageContent = messageContent;
        this.roomName = roomName;
        this.viewType = viewType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getBitmapPath() {
        return bitmapPath;
    }

    public void setBitmapPath(String bitmapPath) {
        this.bitmapPath = bitmapPath;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public void setProfilePictureURL(String profilePictureURL) {
        this.profilePictureURL = profilePictureURL;
    }
}

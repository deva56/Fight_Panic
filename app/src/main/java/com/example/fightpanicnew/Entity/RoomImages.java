package com.example.fightpanicnew.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "room_images_table")
public class RoomImages {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String roomID;
    private String path;

    public RoomImages(String roomID, String path) {
        this.roomID = roomID;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
}

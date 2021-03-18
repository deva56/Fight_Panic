package com.example.fightpanicnew.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.fightpanicnew.Entity.RoomImages;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface ChatDAO {

    @Insert
    Completable insert(RoomImages roomImages);

    @Query("SELECT * FROM room_images_table WHERE roomID = :roomID")
    Observable<List<RoomImages>> getAllRoomImages(String roomID);

    @Query("DELETE FROM room_images_table WHERE roomID = :roomID")
    Completable deleteAllRoomImages(String roomID);
}

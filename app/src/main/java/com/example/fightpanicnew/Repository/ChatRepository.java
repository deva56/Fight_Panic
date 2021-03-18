package com.example.fightpanicnew.Repository;

import android.app.Application;

import com.example.fightpanicnew.DAO.ChatDAO;
import com.example.fightpanicnew.Database.ChatDatabase;
import com.example.fightpanicnew.Entity.RoomImages;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class ChatRepository {

    private final ChatDAO chatDAO;

    public ChatRepository(Application application) {
        ChatDatabase chatDatabase = ChatDatabase.getInstance(application);
        chatDAO = chatDatabase.roomImagesDAO();
    }

    public Completable insert(RoomImages roomImages) {
        return chatDAO.insert(roomImages);
    }

    public Observable<List<RoomImages>> getAllRoomImages(String roomID) {
        return chatDAO.getAllRoomImages(roomID);
    }

    public Completable deleteAllRoomImages(String roomID) {
        return chatDAO.deleteAllRoomImages(roomID);
    }
}

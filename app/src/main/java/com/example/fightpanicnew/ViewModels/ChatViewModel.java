package com.example.fightpanicnew.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.fightpanicnew.Entity.ChatMessages;
import com.example.fightpanicnew.Entity.RoomImages;
import com.example.fightpanicnew.Repository.ChatMessagesRepository;
import com.example.fightpanicnew.Repository.ChatRepository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class ChatViewModel extends AndroidViewModel {

    private final ChatRepository chatRepository;
    private final ChatMessagesRepository chatMessagesRepository;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        chatRepository = new ChatRepository(application);
        chatMessagesRepository = new ChatMessagesRepository(application);
    }

    public Completable insert(RoomImages roomImages) {
        return chatRepository.insert(roomImages);
    }

    public Observable<List<RoomImages>> getAllRoomImages(String roomID) {
        return chatRepository.getAllRoomImages(roomID);
    }

    public Completable deleteAllRoomImages(String roomID) {
        return chatRepository.deleteAllRoomImages(roomID);
    }

    public Completable insert(ChatMessages chatMessage) {
        return chatMessagesRepository.insert(chatMessage);
    }

    public Completable update(ChatMessages chatMessage) {
        return chatMessagesRepository.update(chatMessage);
    }

    public Observable<List<ChatMessages>> getChatMessagesList(String roomID) {
        return chatMessagesRepository.getChatMessagesList(roomID);
    }

    public Completable deleteChatMessagesList(String roomID) {
        return chatMessagesRepository.deleteChatMessagesList(roomID);
    }
}

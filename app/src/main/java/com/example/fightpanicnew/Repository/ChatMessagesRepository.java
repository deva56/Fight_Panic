package com.example.fightpanicnew.Repository;

import android.app.Application;

import com.example.fightpanicnew.DAO.ChatMessagesDAO;
import com.example.fightpanicnew.Database.ChatMessagesDatabase;
import com.example.fightpanicnew.Entity.ChatMessages;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class ChatMessagesRepository {

    private final ChatMessagesDAO chatMessagesDAO;

    public ChatMessagesRepository(Application application) {
        ChatMessagesDatabase chatMessagesDatabase = ChatMessagesDatabase.getInstance(application);
        chatMessagesDAO = chatMessagesDatabase.chatMessagesDAO();
    }

    public Completable insert(ChatMessages chatMessage) {
        return chatMessagesDAO.insert(chatMessage);
    }

    public Completable update(ChatMessages chatMessage) {
        return chatMessagesDAO.update(chatMessage);
    }

    public Observable<List<ChatMessages>> getChatMessagesList(String roomID) {
        return chatMessagesDAO.getChatMessagesList(roomID);
    }

    public Completable deleteChatMessagesList(String roomID) {
        return chatMessagesDAO.deleteChatMessagesList(roomID);
    }
}

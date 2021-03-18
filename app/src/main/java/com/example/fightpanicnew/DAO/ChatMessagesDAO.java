package com.example.fightpanicnew.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fightpanicnew.Entity.ChatMessages;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface ChatMessagesDAO {
    @Insert
    Completable insert(ChatMessages chatMessages);

    @Update
    Completable update(ChatMessages chatMessages);

    @Query("SELECT * FROM chatMessages_table WHERE roomName = :roomName")
    Observable<List<ChatMessages>> getChatMessagesList(String roomName);

    @Query("DELETE FROM chatMessages_table WHERE roomName = :roomName")
    Completable deleteChatMessagesList(String roomName);
}

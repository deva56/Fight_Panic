package com.example.fightpanicnew.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.fightpanicnew.DAO.ChatMessagesDAO;
import com.example.fightpanicnew.Entity.ChatMessages;

@Database(entities = ChatMessages.class, version = 3)
public abstract class ChatMessagesDatabase extends RoomDatabase {

    private static ChatMessagesDatabase chatMessageDatabase;

    public abstract ChatMessagesDAO chatMessagesDAO();

    public static synchronized ChatMessagesDatabase getInstance(Context context) {
        if (chatMessageDatabase == null) {
            chatMessageDatabase = Room.databaseBuilder(context.getApplicationContext(), ChatMessagesDatabase.class,
                    "chatMessages_table").fallbackToDestructiveMigration().build();
        }
        return chatMessageDatabase;
    }
}

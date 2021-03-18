package com.example.fightpanicnew.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.fightpanicnew.DAO.ChatDAO;
import com.example.fightpanicnew.Entity.RoomImages;

@Database(entities = RoomImages.class, version = 1)
public abstract class ChatDatabase extends RoomDatabase {

    private static ChatDatabase chatDatabase;

    public abstract ChatDAO roomImagesDAO();

    public static synchronized ChatDatabase getInstance(Context context) {
        if (chatDatabase == null) {
            chatDatabase = Room.databaseBuilder(context.getApplicationContext(), ChatDatabase.class,
                    "room_images_database").fallbackToDestructiveMigration().build();
        }
        return chatDatabase;
    }
}

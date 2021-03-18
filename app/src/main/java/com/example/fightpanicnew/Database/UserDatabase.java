package com.example.fightpanicnew.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.fightpanicnew.DAO.UserDAO;
import com.example.fightpanicnew.Entity.User;

@Database(entities = User.class, version = 1)
public abstract class UserDatabase extends RoomDatabase {

    private static UserDatabase userDatabase;

    public abstract UserDAO userDAO();

    public static synchronized UserDatabase getInstance(Context context) {
        if (userDatabase == null) {
            userDatabase = Room.databaseBuilder(context.getApplicationContext(), UserDatabase.class,
                    "user_database").fallbackToDestructiveMigration().build();
        }
        return userDatabase;
    }
}

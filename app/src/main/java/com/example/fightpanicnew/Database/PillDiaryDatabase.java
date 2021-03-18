package com.example.fightpanicnew.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.fightpanicnew.DAO.PillDiaryDAO;
import com.example.fightpanicnew.Entity.PillDiaryRecord;

@Database(entities = PillDiaryRecord.class, version = 2)
public abstract class PillDiaryDatabase extends RoomDatabase {

    private static PillDiaryDatabase pillDiaryDatabase;

    public abstract PillDiaryDAO pillDiaryDAO();

    public static synchronized PillDiaryDatabase getInstance(Context context) {
        if (pillDiaryDatabase == null) {
            pillDiaryDatabase = Room.databaseBuilder(context.getApplicationContext(), PillDiaryDatabase.class,
                    "pillDiary_database").fallbackToDestructiveMigration().build();
        }
        return pillDiaryDatabase;
    }
}

package com.example.fightpanicnew.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.fightpanicnew.DAO.PanicCalendarDAO;
import com.example.fightpanicnew.Entity.PanicAttackRecord;

@Database(entities = PanicAttackRecord.class, version = 3)
public abstract class PanicCalendarDatabase extends RoomDatabase {

    private static PanicCalendarDatabase panicCalendarDatabase;

    public abstract PanicCalendarDAO panicCalendarDAO();

    public static synchronized PanicCalendarDatabase getInstance(Context context) {
        if (panicCalendarDatabase == null) {
            panicCalendarDatabase = Room.databaseBuilder(context.getApplicationContext(), PanicCalendarDatabase.class,
                    "panicCalendar_database").fallbackToDestructiveMigration().build();
        }
        return panicCalendarDatabase;
    }
}

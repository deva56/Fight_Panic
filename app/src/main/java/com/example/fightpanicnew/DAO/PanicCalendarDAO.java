package com.example.fightpanicnew.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fightpanicnew.Entity.PanicAttackRecord;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface PanicCalendarDAO {

    @Insert
    Completable insert(PanicAttackRecord panicAttackRecord);

    @Update
    Completable update(PanicAttackRecord panicAttackRecord);

    @Query("DELETE FROM panicAttackRecord_table WHERE id = :id")
    Completable delete(int id);

    @Query("DELETE FROM panicAttackRecord_table")
    Single<Integer> deleteAllRecords();

    @Query("SELECT * FROM panicAttackRecord_table ORDER BY attackStrength DESC")
    LiveData<List<PanicAttackRecord>> getAllRecords();
}

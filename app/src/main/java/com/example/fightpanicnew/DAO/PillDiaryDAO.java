package com.example.fightpanicnew.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fightpanicnew.Entity.PillDiaryRecord;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface PillDiaryDAO {
    @Insert
    Completable insert(PillDiaryRecord pillDiaryRecord);

    @Update
    Completable update(PillDiaryRecord pillDiaryRecord);

    @Query("DELETE FROM pillDiaryRecord_table WHERE id = :id")
    Completable delete(int id);

    @Query("DELETE FROM pillDiaryRecord_table")
    Single<Integer> deleteAllRecords();

    @Query("SELECT * FROM pillDiaryRecord_table ORDER BY dateOfCreation DESC")
    LiveData<List<PillDiaryRecord>> getAllRecords();
}

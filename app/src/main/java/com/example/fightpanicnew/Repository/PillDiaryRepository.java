package com.example.fightpanicnew.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.fightpanicnew.DAO.PillDiaryDAO;
import com.example.fightpanicnew.Database.PillDiaryDatabase;
import com.example.fightpanicnew.Entity.PillDiaryRecord;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;


public class PillDiaryRepository {

    private final PillDiaryDAO pillDiaryDAO;
    private final LiveData<List<PillDiaryRecord>> pillDiaryRecords;

    public PillDiaryRepository(Application application) {
        PillDiaryDatabase pillDiaryDatabase = PillDiaryDatabase.getInstance(application);
        pillDiaryDAO = pillDiaryDatabase.pillDiaryDAO();
        pillDiaryRecords = pillDiaryDAO.getAllRecords();
    }

    public Completable insert(PillDiaryRecord pillDiaryRecord) {
        return pillDiaryDAO.insert(pillDiaryRecord);
    }

    public Completable update(PillDiaryRecord pillDiaryRecord) {
        return pillDiaryDAO.update(pillDiaryRecord);
    }

    public Completable delete(int id) {
        return pillDiaryDAO.delete(id);
    }

    public Single<Integer> deleteAllRecords() {
        return pillDiaryDAO.deleteAllRecords();
    }

    public LiveData<List<PillDiaryRecord>> getAllRecords() {
        return pillDiaryRecords;
    }
}

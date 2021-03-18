package com.example.fightpanicnew.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.fightpanicnew.DAO.PanicCalendarDAO;
import com.example.fightpanicnew.Database.PanicCalendarDatabase;
import com.example.fightpanicnew.Entity.PanicAttackRecord;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class PanicCalendarRepository {

    private final PanicCalendarDAO panicCalendarDAO;
    private final LiveData<List<PanicAttackRecord>> panicAttackRecords;

    public PanicCalendarRepository(Application application) {
        PanicCalendarDatabase panicCalendarDatabase = PanicCalendarDatabase.getInstance(application);
        panicCalendarDAO = panicCalendarDatabase.panicCalendarDAO();
        panicAttackRecords = panicCalendarDAO.getAllRecords();
    }

    public Completable insert(PanicAttackRecord panicAttackRecord) {
        return panicCalendarDAO.insert(panicAttackRecord);
    }

    public Completable update(PanicAttackRecord panicAttackRecord) {
        return panicCalendarDAO.update(panicAttackRecord);
    }

    public Completable delete(int id) {
        return panicCalendarDAO.delete(id);
    }

    public Single<Integer> deleteAllRecords() {
        return panicCalendarDAO.deleteAllRecords();
    }

    public LiveData<List<PanicAttackRecord>> getAllRecords() {
        return panicAttackRecords;
    }
}

package com.example.fightpanicnew.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.fightpanicnew.Entity.PanicAttackRecord;
import com.example.fightpanicnew.Repository.PanicCalendarRepository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class PanicCalendarViewModel extends AndroidViewModel {

    private final PanicCalendarRepository panicCalendarRepository;
    private final LiveData<List<PanicAttackRecord>> panicAttackRecords;

    public PanicCalendarViewModel(@NonNull Application application) {
        super(application);
        panicCalendarRepository = new PanicCalendarRepository(application);
        panicAttackRecords = panicCalendarRepository.getAllRecords();
    }

    public Completable insert(PanicAttackRecord panicAttackRecord) {
        return panicCalendarRepository.insert(panicAttackRecord);
    }

    public Completable update(PanicAttackRecord panicAttackRecord) {
        return panicCalendarRepository.update(panicAttackRecord);
    }

    public Completable delete(int id) {
        return panicCalendarRepository.delete(id);
    }

    public Single<Integer> deleteAllRecords() {
        return panicCalendarRepository.deleteAllRecords();
    }

    public LiveData<List<PanicAttackRecord>> getAllRecords() {
        return panicAttackRecords;
    }
}

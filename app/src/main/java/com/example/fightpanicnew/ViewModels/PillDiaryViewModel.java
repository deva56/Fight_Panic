package com.example.fightpanicnew.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.fightpanicnew.Entity.PillDiaryRecord;
import com.example.fightpanicnew.Repository.PillDiaryRepository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class PillDiaryViewModel extends AndroidViewModel {

    private final PillDiaryRepository pillDiaryRepository;
    private final LiveData<List<PillDiaryRecord>> pillDiaryRecords;

    public PillDiaryViewModel(@NonNull Application application) {
        super(application);
        pillDiaryRepository = new PillDiaryRepository(application);
        pillDiaryRecords = pillDiaryRepository.getAllRecords();
    }

    public Completable insert(PillDiaryRecord pillDiaryRecord) {
        return pillDiaryRepository.insert(pillDiaryRecord);
    }

    public Completable update(PillDiaryRecord pillDiaryRecord) {
        return pillDiaryRepository.update(pillDiaryRecord);
    }

    public Completable delete(int id) {
        return pillDiaryRepository.delete(id);
    }

    public Single<Integer> deleteAllRecords() {
        return pillDiaryRepository.deleteAllRecords();
    }

    public LiveData<List<PillDiaryRecord>> getAllRecords() {
        return pillDiaryRecords;
    }
}

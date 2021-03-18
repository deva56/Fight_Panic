package com.example.fightpanicnew.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.fightpanicnew.Entity.User;
import com.example.fightpanicnew.Repository.UserRepository;

import io.reactivex.Completable;
import io.reactivex.Single;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository userRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public Completable insert(User user) {
        return userRepository.insert(user);
    }

    public Completable update(User user) {
        return userRepository.update(user);
    }

    public Completable delete(int id) {
        return userRepository.delete(id);
    }

    public Single<User> getUser(String id) {
        return userRepository.getUser(id);
    }

    public Single<User> getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }
}

package com.example.fightpanicnew.Repository;

import android.app.Application;

import com.example.fightpanicnew.DAO.UserDAO;
import com.example.fightpanicnew.Database.UserDatabase;
import com.example.fightpanicnew.Entity.User;

import io.reactivex.Completable;
import io.reactivex.Single;

public class UserRepository {

    private final UserDAO userDAO;

    public UserRepository(Application application) {
        UserDatabase userDatabase = UserDatabase.getInstance(application);
        userDAO = userDatabase.userDAO();
    }

    public Completable insert(User user) {
        return userDAO.insert(user);
    }

    public Completable update(User user) {
        return userDAO.update(user);
    }

    public Completable delete(int id) {
        return userDAO.delete(id);
    }

    public Single<User> getUser(String id) {
        return userDAO.getUser(id);
    }

    public Single<User> getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }

}

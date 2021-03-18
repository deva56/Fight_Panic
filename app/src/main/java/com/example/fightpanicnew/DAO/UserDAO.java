package com.example.fightpanicnew.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fightpanicnew.Entity.User;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface UserDAO {
    @Insert
    Completable insert(User user);

    @Update
    Completable update(User user);

    @Query("DELETE FROM user_table WHERE id = :id")
    Completable delete(int id);

    @Query("SELECT * FROM user_table WHERE id = :id")
    Single<User> getUser(String id);

    @Query("SELECT * FROM user_table WHERE email = :email")
    Single<User> getUserByEmail(String email);
}

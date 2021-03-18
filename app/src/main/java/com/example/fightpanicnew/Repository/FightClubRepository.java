package com.example.fightpanicnew.Repository;

import com.example.fightpanicnew.Models.Response;
import com.example.fightpanicnew.Models.Room;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.example.fightpanicnew.Network.RetrofitBuilder.getRetrofit;

public class FightClubRepository {

    private static FightClubRepository fightClubRepository;

    public static synchronized FightClubRepository getInstance() {
        if (fightClubRepository == null) {
            fightClubRepository = new FightClubRepository();
        }
        return fightClubRepository;
    }

    public Observable<List<Room>> getAllRooms() {
        return getRetrofit().getRooms()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Room> createNewRoom(Room room) {
        return getRetrofit().createNewRoom(room);
    }

    public Observable<Response> authorizeJoinRoom(String roomName, String roomPassword) {
        return getRetrofit().authorizeJoinRoom(roomName, roomPassword);
    }
}

package com.example.fightpanicnew.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.fightpanicnew.Models.Response;
import com.example.fightpanicnew.Models.Room;
import com.example.fightpanicnew.Repository.FightClubRepository;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.socket.client.Socket;

public class FightClubViewModel extends AndroidViewModel {

    private final FightClubRepository fightClubRepository = FightClubRepository.getInstance();
    MutableLiveData<List<Room>> roomList = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable;
    private Socket socket;

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public MutableLiveData<List<Room>> getRoomList() {
        return roomList;
    }

    public void postRoomList(List<Room> roomList) {
        this.roomList.postValue(roomList);
    }

    public FightClubViewModel(@NonNull Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
        getRooms();
    }

    public MutableLiveData<List<Room>> getAllRooms() {
        return roomList;
    }

    public void getRooms() {
        fightClubRepository.getAllRooms().subscribe(new Observer<List<Room>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Room> rooms) {
                roomList.postValue(rooms);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.d("Error", e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public Observable<Room> createNewRoom(Room room) {
        return fightClubRepository.createNewRoom(room);
    }

    public Observable<Response> authorizeJoinRoom(String roomName, String roomPassword) {
        return fightClubRepository.authorizeJoinRoom(roomName, roomPassword);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}

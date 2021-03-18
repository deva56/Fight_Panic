/*
Krovna aktivnost sa listom dostupnih chat soba. Ako je korisnik ulogiran može vidjeti listu kreiranih soba i kreirati svoju chat sobu koja
može biti privatna sa lozinkom ili javna. Ako nije ulogiran mora se prvo ulogirati ili registrirati da bi mogao pristupiti chatu.
*/

package com.example.fightpanicnew.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fightpanicnew.Entity.RoomImages;
import com.example.fightpanicnew.FightPanic;
import com.example.fightpanicnew.Fragments.CreateChatRoomFragment;
import com.example.fightpanicnew.Fragments.FightClubLoginFragment;
import com.example.fightpanicnew.Fragments.JoinChatRoomFragment;
import com.example.fightpanicnew.Models.ChatClasses;
import com.example.fightpanicnew.Models.Response;
import com.example.fightpanicnew.Models.Room;
import com.example.fightpanicnew.R;
import com.example.fightpanicnew.RecyclerViewClasses.FightClubAdapter;
import com.example.fightpanicnew.ViewModels.ChatViewModel;
import com.example.fightpanicnew.ViewModels.FightClubViewModel;
import com.example.fightpanicnew.databinding.ActivityFightClubBinding;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.example.fightpanicnew.Constants.DARK_MODE_SWITCH;
import static com.example.fightpanicnew.Constants.DARK_MODE_VALUE;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_PROFILE_PICTURE_URL;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_ROOM_NAME;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_USER_NAME;
import static com.example.fightpanicnew.Constants.IS_BACKGROUND_TERMINATED;
import static com.example.fightpanicnew.Constants.IS_BACKGROUND_TERMINATED_VALUE;
import static com.example.fightpanicnew.Constants.IS_LOGGED_IN_DATA;
import static com.example.fightpanicnew.Constants.IS_LOGGED_IN_STATE;
import static com.example.fightpanicnew.Network.RetrofitBuilder.getRetrofit;

public class FightClub extends AppCompatActivity {

    private ActivityFightClubBinding binding;
    private FightClubViewModel fightClubViewModel;
    private final List<Room> roomList = new ArrayList<>();
    private FightClubAdapter adapter;

    private Socket socket;

    private io.reactivex.disposables.CompositeDisposable compositeDisposable2;

    private ChatViewModel chatViewModel;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFightClubBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable2 = new io.reactivex.disposables.CompositeDisposable();
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        gson = new Gson();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        DARK_MODE_VALUE = sharedPref.getBoolean(DARK_MODE_SWITCH, false);

        if (!DARK_MODE_VALUE) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        socket = ((FightPanic) this.getApplication()).getAppSocket();
        socket.emit("subscribeFightClubRoom");

        socket.on("updateFightClubActivity", updateFightClubActivity);

        Toolbar toolbar = findViewById(R.id.FightClubAppBar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.mainMenuFightClub));

        SharedPreferences sharedPreferencesUserLoggedIn = getSharedPreferences(IS_LOGGED_IN_DATA, MODE_PRIVATE);
        boolean isLoggedInBool = sharedPreferencesUserLoggedIn.getBoolean(IS_LOGGED_IN_STATE, false);

        binding.AddNewChatRoom.setOnClickListener(view1 -> {
            CreateChatRoomFragment createChatRoomFragment = new CreateChatRoomFragment();
            createChatRoomFragment.show(getSupportFragmentManager(), CreateChatRoomFragment.TAG);
        });

        if (!isLoggedInBool) {
            binding.AddNewChatRoom.setVisibility(View.GONE);
            binding.FightClubNoChatsYet.setVisibility(View.GONE);

            FightClubLoginFragment newFragment = new FightClubLoginFragment();
            newFragment.show(getSupportFragmentManager(), FightClubLoginFragment.TAG);

        } else {

            binding.AddNewChatRoom.setVisibility(View.VISIBLE);

            fightClubViewModel = new ViewModelProvider(this).get(FightClubViewModel.class);
            fightClubViewModel.setSocket(socket);
            RecyclerView recyclerView = binding.FightClubRecyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            adapter = new FightClubAdapter(roomList);
            recyclerView.setAdapter(adapter);

            fightClubViewModel.getAllRooms().observe(this, rooms -> {
                if (rooms.size() == 0) {
                    binding.FightClubNoChatsYet.setVisibility(View.VISIBLE);
                } else {
                    binding.FightClubNoChatsYet.setVisibility(View.GONE);
                }
                adapter.setRecords(rooms);
            });

            adapter.setOnItemClickListener(room -> {
                JoinChatRoomFragment joinChatRoomFragment = new JoinChatRoomFragment(room.getRoomPrivate(), room.getRoomDescription(), room.getRoomName());
                joinChatRoomFragment.show(getSupportFragmentManager(), JoinChatRoomFragment.TAG);
            });
        }

        Intent intent = getIntent();
        if (intent.getBooleanExtra("IsChatTerminated", false)) {
            compositeDisposable.add(getRetrofit().getIfRoomAlive(intent.getStringExtra("RoomName"))
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::onResponse, this::onFailure));
        }
    }

    private void onResponse(Response response) {

        if (response.getMessage().equals("Room alive.")) {
            SharedPreferences sharedPreferencesBackgroundTerminated = getSharedPreferences(IS_BACKGROUND_TERMINATED, MODE_PRIVATE);
            ChatClasses.sendMessageUserInactiveOrActive sendMessageUserInactiveOrActive = new ChatClasses.sendMessageUserInactiveOrActive(sharedPreferencesBackgroundTerminated.getString(FOREGROUND_SERVICE_ROOM_NAME, ""), sharedPreferencesBackgroundTerminated.getString(FOREGROUND_SERVICE_USER_NAME, ""));
            String jsonData = gson.toJson(sendMessageUserInactiveOrActive);
            socket.emit("userActive", jsonData);
            Intent a = new Intent(FightClub.this, Chat.class);
            a.putExtra("Username", sharedPreferencesBackgroundTerminated.getString(FOREGROUND_SERVICE_USER_NAME, ""));
            a.putExtra("RoomName", sharedPreferencesBackgroundTerminated.getString(FOREGROUND_SERVICE_ROOM_NAME, ""));
            a.putExtra("ProfilePictureURL", sharedPreferencesBackgroundTerminated.getString(FOREGROUND_SERVICE_PROFILE_PICTURE_URL, ""));
            startActivity(a);
        }
    }

    private void onFailure(Throwable throwable) {

        assert throwable.getMessage() != null;
        if (throwable.getMessage().equals("HTTP 404 Not Found")) {

            compositeDisposable2.add(chatViewModel.deleteChatMessagesList(FOREGROUND_SERVICE_ROOM_NAME)
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribe(this::onSuccessDeleteMessagesList, this::onFailureDeleteMessagesList));

            compositeDisposable2.add(chatViewModel.getAllRoomImages(FOREGROUND_SERVICE_ROOM_NAME)
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribe(this::onSuccessGetAllRooms, this::onFailureGetAllRooms));

            SharedPreferences sharedPreferencesBackgroundTerminated = getSharedPreferences(IS_BACKGROUND_TERMINATED, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesBackgroundTerminated.edit();
            editor.putBoolean(IS_BACKGROUND_TERMINATED_VALUE, false);
            editor.putString(FOREGROUND_SERVICE_ROOM_NAME, "");
            editor.putString(FOREGROUND_SERVICE_USER_NAME, "");
            editor.putString(FOREGROUND_SERVICE_PROFILE_PICTURE_URL, "");
            editor.apply();

            runOnUiThread(() -> Toast.makeText(getApplicationContext(), getString(R.string.roomDeletedWhileAway), Toast.LENGTH_LONG).show());

        } else {
            Log.d("FightClub", "onFailure: " + throwable.getMessage());
        }
    }

    Emitter.Listener updateFightClubActivity = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            fightClubViewModel.getRooms();
        }
    };

    private void onSuccessDeleteMessagesList() {
        Log.d("CHAT", "Uspješno obrisana chat list iz baze.");
    }

    private void onFailureDeleteMessagesList(Throwable throwable) {
        Log.e("CHAT", "onError: " + throwable.getMessage());
    }

    private void onSuccessGetAllRooms(List<RoomImages> roomImages) {
        for (RoomImages i : roomImages) {
            File fdelete = new File(i.getPath());
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    Log.e("CHAT", "file Deleted :" + i.getPath());
                } else {
                    Log.e("CHAT", "file not Deleted :" + i.getPath());
                }
            }
        }
        compositeDisposable2.add(chatViewModel.deleteAllRoomImages(roomImages.get(0).getRoomID())
                .observeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribe(this::onSuccessDeleteAllRooms, this::onFailureDeleteAllRooms));
    }

    private void onFailureGetAllRooms(Throwable throwable) {
        Log.e("CHAT", "onError: " + throwable.getMessage());
    }

    private void onSuccessDeleteAllRooms() {
        Log.d("CHAT", "Uspješno obrisane slike iz storagea i podatci iz baze");
    }

    private void onFailureDeleteAllRooms(Throwable throwable) {
        Log.e("CHAT", "onError: " + throwable.getMessage());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.fight_club_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.fightClubHelpIcon) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.fightClubHelp);
            builder.setPositiveButton(getString(R.string.gotIt), (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            if (getIntent().getBooleanExtra("IsChatTerminated", false)) {
                Intent intent = new Intent(FightClub.this, MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                onNavigateUp();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("IsChatTerminated", false)) {
            Intent intent = new Intent(FightClub.this, MainMenu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.off("updateFightClubActivity", updateFightClubActivity);
        socket = null;
    }
}
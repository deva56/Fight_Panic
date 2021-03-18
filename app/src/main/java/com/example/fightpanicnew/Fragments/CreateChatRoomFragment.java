/*
Fragment za kreiranje chat sobe. Sadrži pozive serveru za provjeru ispravnosti pri kreiranju sobe, da li već postoji soba sa istim imenom te da
li je kreiranje dobro prošlo te ako je korisnika prebacuje na chat aktivnost.
*/

package com.example.fightpanicnew.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fightpanicnew.Activity.Chat;
import com.example.fightpanicnew.Entity.User;
import com.example.fightpanicnew.Models.Room;
import com.example.fightpanicnew.R;
import com.example.fightpanicnew.ViewModels.FightClubViewModel;
import com.example.fightpanicnew.ViewModels.UserViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.example.fightpanicnew.Constants.LOGGED_USER_ID;
import static com.example.fightpanicnew.Constants.LOGIN_INFORMATION;
import static com.example.fightpanicnew.HelperClasses.Validation.validateFields;
import static com.example.fightpanicnew.HelperClasses.Validation.validatePassword;

public class CreateChatRoomFragment extends DialogFragment {

    public static final String TAG = CreateChatRoomFragment.class.getSimpleName();

    private EditText roomName;
    private EditText roomDescription;
    private EditText roomPassword;
    private TextInputLayout roomNameInputLayout;
    private TextInputLayout roomDescriptionInputLayout;
    private TextInputLayout roomPasswordInputLayout;
    private ProgressBar progressBar;
    private CheckBox checkBox;
    private String userName;
    private String profilePictureURL;
    private String roomName2;
    private CompositeDisposable compositeDisposable;
    private io.reactivex.rxjava3.disposables.CompositeDisposable compositeDisposable2;
    private FightClubViewModel fightClubViewModel;
    private Socket socket;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_new_chat_room, container, false);
        initViews(view);
        compositeDisposable = new CompositeDisposable();
        compositeDisposable2 = new io.reactivex.rxjava3.disposables.CompositeDisposable();

        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        SharedPreferences sharedPreferencesEditLoginInformation = this.getActivity().getSharedPreferences(LOGIN_INFORMATION, Context.MODE_PRIVATE);
        String loggedUserID = sharedPreferencesEditLoginInformation.getString(LOGGED_USER_ID, "");

        fightClubViewModel = new ViewModelProvider(requireActivity()).get(FightClubViewModel.class);

        socket = fightClubViewModel.getSocket();

        socket.on("jumpToChat", onJumpToChat);

        compositeDisposable.add(userViewModel.getUser(loggedUserID)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseGetUser, this::handleErrorGetUser));

        return view;
    }

    private void handleResponseGetUser(User user) {
        userName = user.getUsername();
        profilePictureURL = user.getProfilePicturePathFirebase();
    }

    private void handleErrorGetUser(Throwable throwable) {
        Log.e("GetUserError", throwable.getMessage());
    }

    private void initViews(View v) {
        roomName = v.findViewById(R.id.RoomNameEditText);
        roomDescription = v.findViewById(R.id.RoomDescriptionEditText);
        roomPassword = v.findViewById(R.id.RoomPasswordEditText);
        Button goBack = v.findViewById(R.id.GoBackCreateNewChatRoom);
        Button createNewRoom = v.findViewById(R.id.CreateRoomCreateNewChatRoom);
        progressBar = v.findViewById(R.id.CreateNewChatRoomProgressBar);
        roomNameInputLayout = v.findViewById(R.id.RoomNameTextInputLayout);
        roomDescriptionInputLayout = v.findViewById(R.id.RoomDescriptionTextInputLayout);
        roomPasswordInputLayout = v.findViewById(R.id.RoomPasswordTextInputLayout);
        checkBox = v.findViewById(R.id.MakeChatRoomPrivate);

        progressBar.setVisibility(View.GONE);
        roomPasswordInputLayout.setVisibility(View.GONE);
        roomPassword.setVisibility(View.GONE);

        checkBox.setOnClickListener(view -> {
            if (checkBox.isChecked()) {
                roomPasswordInputLayout.setVisibility(View.VISIBLE);
                roomPassword.setVisibility(View.VISIBLE);
            } else {
                roomPasswordInputLayout.setVisibility(View.GONE);
                roomPassword.setVisibility(View.GONE);
            }
        });

        goBack.setOnClickListener(view -> {
            dismiss();
        });

        createNewRoom.setOnClickListener(view12 -> {

            String roomNameValue = roomName.getText().toString().trim();
            String roomDescriptionValue = roomDescription.getText().toString().trim();
            String roomPasswordValue = roomPassword.getText().toString().trim();
            int errors = 0;

            setError();

            if (!validateFields(roomNameValue)) {

                errors++;
                roomNameInputLayout.setError(getString(R.string.roomNameNotEmpty));
            }

            if (!validateFields(roomDescriptionValue)) {

                errors++;
                roomDescriptionInputLayout.setError(getString(R.string.roomDescriptionNotEmpty));
            }

            if (checkBox.isChecked()) {

                if (!validatePassword(roomPasswordValue)) {
                    errors++;
                    roomPasswordInputLayout.setError(getString(R.string.roomPasswordTooShort));
                }

                if (!validateFields(roomPasswordValue)) {
                    errors++;
                    roomPasswordInputLayout.setError(getString(R.string.roomPasswordNotEmpty));
                }
            }

            if (errors == 0) {
                Room room = new Room();
                roomName2 = roomName.getText().toString();
                room.setRoomName(roomName.getText().toString());
                room.setRoomDescription(roomDescription.getText().toString());
                room.setRoomPrivate(checkBox.isChecked());
                room.setPassword(roomPassword.getText().toString());
                room.setAdmin(userName);

                progressBar.setVisibility(View.VISIBLE);

                compositeDisposable2.add(fightClubViewModel.createNewRoom(room)
                        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                        .observeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                        .subscribe(this::onSuccessResponse, this::onFailureResponse));

            }
        });
    }

    private void onSuccessResponse(Room room) {
        List<Room> list = new ArrayList<>(fightClubViewModel.getRoomList().getValue());
        list.add(room);
        fightClubViewModel.postRoomList(list);
        socket.emit("updateFightClubActivity");
    }

    private void onFailureResponse(Throwable throwable) {
        getActivity().runOnUiThread(() -> {
            if (throwable.getMessage().equals("HTTP 409 Conflict")) {
                roomNameInputLayout.setError(getString(R.string.roomWithSameNameAlreadyExists));
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    Emitter.Listener onJumpToChat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Intent i = new Intent(getActivity(), Chat.class);
            i.putExtra("Username", userName);
            i.putExtra("RoomName", roomName2);
            i.putExtra("ProfilePictureURL", profilePictureURL);
            dismiss();
            startActivity(i);
        }
    };

    private void setError() {
        roomNameInputLayout.setError(null);
        roomDescriptionInputLayout.setError(null);
        roomPasswordInputLayout.setError(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        socket.off("jumpToChat", onJumpToChat);
        socket = null;
    }
}

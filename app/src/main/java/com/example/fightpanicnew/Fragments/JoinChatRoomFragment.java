/*
Fragment za pridruživanje chat sobi. Ako je soba zaključana provjerava da li je upisana lozinka dobra i ako je šalje korisnika u chat aktivnost.
*/

package com.example.fightpanicnew.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fightpanicnew.Activity.Chat;
import com.example.fightpanicnew.Entity.User;
import com.example.fightpanicnew.Models.Response;
import com.example.fightpanicnew.R;
import com.example.fightpanicnew.ViewModels.FightClubViewModel;
import com.example.fightpanicnew.ViewModels.UserViewModel;
import com.google.android.material.textfield.TextInputLayout;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.fightpanicnew.Constants.LOGGED_USER_ID;
import static com.example.fightpanicnew.Constants.LOGIN_INFORMATION;
import static com.example.fightpanicnew.HelperClasses.Validation.validateFields;

public class JoinChatRoomFragment extends DialogFragment {

    public static final String TAG = JoinChatRoomFragment.class.getSimpleName();

    private EditText roomPassword;
    private TextInputLayout roomPasswordInputLayout;
    private ProgressBar progressBar;
    private String userName;
    private String userProfilePictureURL;
    private CompositeDisposable compositeDisposable;
    private io.reactivex.rxjava3.disposables.CompositeDisposable compositeDisposable2;
    private final boolean isPrivate;
    private final String roomNameString;
    private final String roomDescriptionString;
    private FightClubViewModel fightClubViewModel;

    public JoinChatRoomFragment(boolean isPrivate, String roomDescription, String roomName) {
        this.isPrivate = isPrivate;
        roomNameString = roomName;
        roomDescriptionString = roomDescription;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_chat_room, container, false);
        initViews(view);
        compositeDisposable = new CompositeDisposable();
        compositeDisposable2 = new io.reactivex.rxjava3.disposables.CompositeDisposable();
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        fightClubViewModel = new ViewModelProvider(requireActivity()).get(FightClubViewModel.class);

        SharedPreferences sharedPreferencesEditLoginInformation = this.getActivity().getSharedPreferences(LOGIN_INFORMATION, Context.MODE_PRIVATE);
        String loggedUserID = sharedPreferencesEditLoginInformation.getString(LOGGED_USER_ID, "");

        compositeDisposable.add(userViewModel.getUser(loggedUserID)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseGetUser, this::handleErrorGetUser));

        return view;
    }

    private void handleResponseGetUser(User user) {
        userName = user.getUsername();
        userProfilePictureURL = user.getProfilePicturePathFirebase();
    }

    private void handleErrorGetUser(Throwable throwable) {
        Log.e("GetUserError", throwable.getMessage());
    }

    private void initViews(View v) {
        TextView roomDescription = v.findViewById(R.id.RoomDescriptionContentTextView);
        roomPassword = v.findViewById(R.id.JoinRoomPasswordEditText);
        Button goBack = v.findViewById(R.id.GoBackJoinChatRoom);
        Button joinRoom = v.findViewById(R.id.JoinChatRoomButton);
        progressBar = v.findViewById(R.id.JoinChatRoomProgressBar);
        TextView roomName = v.findViewById(R.id.JoinRoomNameContentTextView);
        roomPasswordInputLayout = v.findViewById(R.id.JoinRoomPasswordTextInputLayout);

        roomName.setText(roomNameString);
        roomDescription.setText(roomDescriptionString);

        progressBar.setVisibility(View.GONE);

        if (isPrivate) {
            roomPasswordInputLayout.setVisibility(View.VISIBLE);
            roomPassword.setVisibility(View.VISIBLE);
        } else {
            roomPasswordInputLayout.setVisibility(View.GONE);
            roomPassword.setVisibility(View.GONE);
        }

        goBack.setOnClickListener(view -> {
            dismiss();
        });

        joinRoom.setOnClickListener(view12 -> {

            if (isPrivate) {
                String roomPasswordValue = roomPassword.getText().toString().trim();

                int errors = 0;
                roomPasswordInputLayout.setError(null);

                if (!validateFields(roomPasswordValue)) {
                    errors++;
                    roomPasswordInputLayout.setError(getString(R.string.roomPasswordNotEmpty));
                }

                if (errors == 0) {
                    progressBar.setVisibility(View.VISIBLE);

                    compositeDisposable2.add(fightClubViewModel.authorizeJoinRoom(roomNameString, roomPasswordValue)
                            .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                            .observeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                            .subscribe(this::onSuccessResponse, this::onFailureResponse));
                }

            } else {
                Intent i = new Intent(getActivity(), Chat.class);
                i.putExtra("Username", userName);
                i.putExtra("RoomName", roomNameString);
                i.putExtra("ProfilePictureURL", userProfilePictureURL);
                dismiss();
                startActivity(i);
            }
        });
    }

    private void onSuccessResponse(Response response) {
        new Handler(Looper.getMainLooper()).post(() -> {
            progressBar.setVisibility(View.GONE);
            if (response.getMessage().equals("Room password ok.")) {
                Intent i = new Intent(getActivity(), Chat.class);
                i.putExtra("Username", userName);
                i.putExtra("RoomName", roomNameString);
                i.putExtra("ProfilePictureURL", userProfilePictureURL);
                dismiss();
                startActivity(i);
            } else {
                Log.d(TAG, "onSuccessResponse: " + response.getMessage());
                Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onFailureResponse(Throwable throwable) {
        new Handler(Looper.getMainLooper()).post(() -> {
            progressBar.setVisibility(View.GONE);
            if (throwable.getMessage().equals("HTTP 401 Unauthorized")) {
                roomPasswordInputLayout.setError(getString(R.string.roomPasswordInvalid));
            } else {
                Log.d(TAG, "onSuccessResponse: " + throwable.getMessage());
                Toast.makeText(getActivity(), getString(R.string.roomJoinError), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}

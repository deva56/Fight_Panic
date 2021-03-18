package com.example.fightpanicnew.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.fightpanicnew.Models.ChatClasses;
import com.example.fightpanicnew.R;
import com.google.gson.Gson;

import io.socket.client.Socket;

import static android.content.Context.MODE_PRIVATE;
import static com.example.fightpanicnew.Constants.SHOW_LEAVE_WARNING;
import static com.example.fightpanicnew.Constants.SHOW_LEAVE_WARNING_VALUE;

public class ChatLeaveShowAlertDialog extends DialogFragment {

    public static final String TAG = ChatLeaveShowAlertDialog.class.getSimpleName();

    private final String userName;
    private final String roomName;
    private Socket socket;
    private final Gson gson;

    public ChatLeaveShowAlertDialog(String userName, String roomName, Socket socket, Gson gson) {
        this.userName = userName;
        this.roomName = roomName;
        this.socket = socket;
        this.gson = gson;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_leave_show_alert_dialog, container, false);
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        TextView stay = view.findViewById(R.id.ChatLeaveShowAlertDialogStay);
        TextView leaveButShow = view.findViewById(R.id.ChatLeaveShowAlertDialogLeaveButShowAgain);
        TextView leaveButDontShow = view.findViewById(R.id.ChatLeaveShowAlertDialogLeaveButDontShowAgain);

        stay.setOnClickListener(view1 -> dismiss());

        leaveButShow.setOnClickListener(view13 -> {
            ChatClasses.initialData initialData = new ChatClasses.initialData(userName, roomName, false, false);
            String jsonData = gson.toJson(initialData);
            socket.emit("leaveRoom", jsonData);
            dismiss();
        });

        leaveButDontShow.setOnClickListener(view12 -> {
            SharedPreferences sharedPreferencesEditTextInformation = getActivity().getSharedPreferences(SHOW_LEAVE_WARNING, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesEditTextInformation.edit();
            editor.putBoolean(SHOW_LEAVE_WARNING_VALUE, false);
            editor.apply();
            ChatClasses.initialData initialData = new ChatClasses.initialData(userName, roomName, false, false);
            String jsonData = gson.toJson(initialData);
            socket.emit("leaveRoom", jsonData);
            dismiss();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket = null;
    }
}
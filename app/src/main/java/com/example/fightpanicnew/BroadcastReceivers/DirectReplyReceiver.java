//Broadcast receiver za slanje poruke kroz direct reply button u foreground servisu chat aktivnosti

package com.example.fightpanicnew.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.RemoteInput;
import androidx.core.content.ContextCompat;

import com.example.fightpanicnew.Services.SocketBackgroundService;

import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_DIRECT_REPLY_RECEIVER_LOCK;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_DIRECT_REPLY_TEXT;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_PROFILE_PICTURE_URL;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_REMOTE_BUILDER_KEY;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_ROOM_NAME;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_USER_NAME;

public class DirectReplyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        if (remoteInput != null) {
            String replyText = (String) remoteInput.getCharSequence(FOREGROUND_SERVICE_REMOTE_BUILDER_KEY);
            String userName = intent.getStringExtra(FOREGROUND_SERVICE_USER_NAME);
            String roomName = intent.getStringExtra(FOREGROUND_SERVICE_ROOM_NAME);
            String profilePictureURL = intent.getStringExtra(FOREGROUND_SERVICE_PROFILE_PICTURE_URL);

            Intent foregroundService = new Intent(context, SocketBackgroundService.class);
            foregroundService.putExtra(FOREGROUND_SERVICE_ROOM_NAME, roomName);
            foregroundService.putExtra(FOREGROUND_SERVICE_USER_NAME, userName);
            foregroundService.putExtra(FOREGROUND_SERVICE_PROFILE_PICTURE_URL, profilePictureURL);
            foregroundService.putExtra(FOREGROUND_SERVICE_DIRECT_REPLY_RECEIVER_LOCK, true);
            foregroundService.putExtra(FOREGROUND_SERVICE_DIRECT_REPLY_TEXT, replyText);

            ContextCompat.startForegroundService(context, foregroundService);

        }
    }
}

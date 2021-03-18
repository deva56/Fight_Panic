package com.example.fightpanicnew;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.socket.client.IO;
import io.socket.client.Socket;

import static com.example.fightpanicnew.Constants.SOCKET_URL;

public class FightPanic extends Application {

    private Socket appSocket;

    public static final String GROUP_CHAT_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL = "groupChatForegroundServiceNotificationChannel";

    ExecutorService executorService = Executors.newFixedThreadPool(4);

    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

        try {
            appSocket = IO.socket(SOCKET_URL);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SocketIO", Objects.requireNonNull(e.getMessage()));
        }
        appSocket.connect();

    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel groupChatForegroundServiceNotificationChannel = new NotificationChannel(
                    GROUP_CHAT_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL,
                    "Group chat notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(groupChatForegroundServiceNotificationChannel);
        }
    }

    public Socket getAppSocket() {
        return appSocket;
    }

    public Executor getExecutor() {
        return executorService;
    }
}

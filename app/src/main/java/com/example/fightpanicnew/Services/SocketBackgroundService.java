/*
Foreground service za prikaz poruka iz chat sobe kada je aplikacija u pozadini. Nudi mogućnost direktnog odgovora kroz notifikaciju,
izlazak iz sobe kroz notifikaciju što vraća korisnika nazad u aktivnost te pri kliku na notifikaciju također vraća korisnika
nazad u aktivnost. Pri povratku u chat aktivnost service se gasi.
*/

package com.example.fightpanicnew.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.graphics.drawable.IconCompat;

import com.example.fightpanicnew.Activity.Chat;
import com.example.fightpanicnew.BroadcastReceivers.DirectReplyReceiver;
import com.example.fightpanicnew.BroadcastReceivers.LeaveRoomReceiver;
import com.example.fightpanicnew.Entity.ChatMessages;
import com.example.fightpanicnew.Entity.RoomImages;
import com.example.fightpanicnew.FightPanic;
import com.example.fightpanicnew.Models.ChatClasses;
import com.example.fightpanicnew.R;
import com.example.fightpanicnew.Repository.ChatMessagesRepository;
import com.example.fightpanicnew.Repository.ChatRepository;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_DIRECT_REPLY_RECEIVER_LOCK;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_DIRECT_REPLY_TEXT;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_PROFILE_PICTURE_URL;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_REMOTE_BUILDER_KEY;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_ROOM_NAME;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_USER_NAME;
import static com.example.fightpanicnew.FightPanic.GROUP_CHAT_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL;
import static com.example.fightpanicnew.HelperClasses.ImageManipulation.getAvatarFromUrl;
import static com.example.fightpanicnew.HelperClasses.ImageManipulation.saveToInternalStorage;

public class SocketBackgroundService extends Service {

    private static final String TAG = "SocketBackgroundService";

    private Socket socket;
    private CompositeDisposable compositeDisposable;
    private io.reactivex.rxjava3.disposables.CompositeDisposable compositeDisposable2;
    private ChatMessagesRepository chatMessagesRepository;
    private ChatRepository chatRepository;
    private List<ChatMessages> chatMessagesList;

    private String roomName;
    private String userName;
    private String profilePictureURL;
    private String directReplyText = null;

    private Gson gson;

    public SocketBackgroundService() {
    }

    @Override
    public void onCreate() {
        socket = ((FightPanic) this.getApplication()).getAppSocket();
        compositeDisposable = new CompositeDisposable();
        compositeDisposable2 = new io.reactivex.rxjava3.disposables.CompositeDisposable();
        chatMessagesRepository = new ChatMessagesRepository(getApplication());
        chatRepository = new ChatRepository(getApplication());
        gson = new Gson();

        socket.on("newUserToChatRoom", onNewUser);
        socket.on("updateChat", onUpdateChat);
        socket.on("userLeftChatRoom", onUserLeft);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        roomName = intent.getStringExtra(FOREGROUND_SERVICE_ROOM_NAME);
        userName = intent.getStringExtra(FOREGROUND_SERVICE_USER_NAME);
        profilePictureURL = intent.getStringExtra(FOREGROUND_SERVICE_PROFILE_PICTURE_URL);

        boolean directReplyReceiverLock = intent.getBooleanExtra(FOREGROUND_SERVICE_DIRECT_REPLY_RECEIVER_LOCK, false);
        directReplyText = intent.getStringExtra(FOREGROUND_SERVICE_DIRECT_REPLY_TEXT);

        if (!directReplyReceiverLock) {
            compositeDisposable.add(chatMessagesRepository.getChatMessagesList(roomName)
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribe(this::onSuccessGetAllChats, this::onFailureGetAllChats));
        } else {
            assert sendMessage(directReplyText) != null;
            compositeDisposable2.add(Observable.just(sendMessage(directReplyText))
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::onSuccessUpdateDirectReplyNotification, this::onFailureUpdateDirectReplyNotification));
        }
        return START_NOT_STICKY;
    }

    private void onSuccessUpdateDirectReplyNotification(String content) {

        ChatMessages chatMessages = new ChatMessages(userName, content, roomName, 0);
        chatMessages.setProfilePictureURL(profilePictureURL);
        chatMessagesList.add(chatMessages);
        updateNotification();

        ChatClasses.sendMessage sendMessage = new ChatClasses.sendMessage(userName, directReplyText, roomName, 1, profilePictureURL);
        String jsonData = gson.toJson(sendMessage);
        socket.emit("newMessage", jsonData);

        compositeDisposable.add(chatMessagesRepository.insert(chatMessages)
                .observeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onSuccessInsertChatList: chat insertan u servisu");
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(TAG, "onFailureInsertChatList: " + e.getMessage());
                    }
                }));
    }

    private void onFailureUpdateDirectReplyNotification(Throwable throwable) {
        Log.d(TAG, "onFailureUpdateDirectReplyNotification: " + throwable.getMessage());
    }

    private void onSuccessGetAllChats(List<ChatMessages> chatMessages) {

        chatMessagesList = chatMessages;

        //------dio za direct reply gumb u notifikaciji-----
        RemoteInput remoteInput = new RemoteInput.Builder(FOREGROUND_SERVICE_REMOTE_BUILDER_KEY)
                .setLabel(getString(R.string.remoteReplyHint))
                .build();

        Intent replyIntent = new Intent(this, DirectReplyReceiver.class);
        replyIntent.putExtra(FOREGROUND_SERVICE_ROOM_NAME, roomName);
        replyIntent.putExtra(FOREGROUND_SERVICE_USER_NAME, userName);
        replyIntent.putExtra(FOREGROUND_SERVICE_PROFILE_PICTURE_URL, profilePictureURL);
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(this, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_send_text_message,
                getString(R.string.reply),
                replyPendingIntent
        ).addRemoteInput(remoteInput).build();
        //-------------------------------------------------

        //---------gumb za napuštanje sobe kroz notifikaciju--------------
        Intent leaveRoomIntent = new Intent(this, LeaveRoomReceiver.class);
        leaveRoomIntent.putExtra(FOREGROUND_SERVICE_USER_NAME, userName);
        leaveRoomIntent.putExtra(FOREGROUND_SERVICE_ROOM_NAME, roomName);
        PendingIntent leaveRoomPendingIntent = PendingIntent.getBroadcast(this, 0, leaveRoomIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //-------------------------------------------------------------------

        //-------povratak u chat aktivnost pri kliku na notifikaciju-------
        Intent chatActivity = new Intent(this, Chat.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, chatActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        //-------------------------------------------------------------------

        String notificationTitle = chatMessages.get(0).getRoomName();

        Person person = new Person.Builder().setName("Me")
                .setIcon(IconCompat.createWithBitmap(getAvatarFromUrl(this, profilePictureURL)))
                .build();
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(person);
        messagingStyle.setConversationTitle(notificationTitle);
        messagingStyle.setGroupConversation(true);

        for (ChatMessages message : chatMessages) {

            String content;
            String name;
            String profilePictureURL = message.getProfilePictureURL();
            Person person2;

            if (!message.getUserName().equals(userName)) {
                name = message.getUserName();
            } else {
                name = "Me";
            }

            if (message.getViewType() == 6 || message.getViewType() == 5) {
                content = "PICTURE";
            } else {
                content = message.getMessageContent();
            }

            if (message.getViewType() == 2 || message.getViewType() == 3) {

                person2 = new Person.Builder()
                        .setBot(true)
                        .setName("Room bot")
                        .setIcon(IconCompat.createWithResource(this, R.drawable.ic_chat_bot))
                        .build();

            } else {
                if (profilePictureURL == null) {
                    person2 = new Person.Builder()
                            .setName(name)
                            .build();
                } else {
                    person2 = new Person.Builder()
                            .setName(name)
                            .setIcon(IconCompat.createWithBitmap(getAvatarFromUrl(this, profilePictureURL)))
                            .build();
                }
            }

            NotificationCompat.MessagingStyle.Message notificationMessage =
                    new NotificationCompat.MessagingStyle.Message(
                            content,
                            System.currentTimeMillis(),
                            person2
                    );
            messagingStyle.addMessage(notificationMessage);
        }


        Notification notification = new NotificationCompat.Builder(this, GROUP_CHAT_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL)
                .setStyle(messagingStyle)
                .addAction(replyAction)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_fist_logo_notification_blue)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_go_back, getString(R.string.leaveRoom), leaveRoomPendingIntent)
                .setNotificationSilent()
                .build();

        startForeground(1, notification);
    }

    private void onFailureGetAllChats(Throwable throwable) {
        Log.e(TAG, "onFailureGetAllChats: " + throwable.getMessage());
    }

    Emitter.Listener onNewUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            String name = args[0].toString();

            ChatMessages chatMessages = new ChatMessages(name, name + " has entered the room", roomName, 2);
            chatMessagesList.add(chatMessages);
            updateNotification();
            compositeDisposable.add(chatMessagesRepository.insert(chatMessages)
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            Log.e(TAG, "onFailureInsertChatList: " + e.getMessage());
                        }
                    }));
        }
    };

    Emitter.Listener onUpdateChat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            ChatMessages message = gson.fromJson(args[0].toString(), ChatMessages.class);
            if (message.getViewType() == 6) {
                compositeDisposable2.add(Observable.just(saveToInternalStorage(message.getMessageContent(), getApplicationContext()))
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableObserver<String>() {
                            @Override
                            public void onNext(@io.reactivex.rxjava3.annotations.NonNull String bitmapPath) {
                                message.setBitmapPath(bitmapPath);
                                message.setMessageContent("");
                                chatMessagesList.add(message);
                                updateNotification();

                                compositeDisposable.add(chatMessagesRepository.insert(message)
                                        .observeOn(io.reactivex.schedulers.Schedulers.io())
                                        .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                                        .subscribeWith(new DisposableCompletableObserver() {
                                            @Override
                                            public void onComplete() {
                                            }

                                            @Override
                                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                                Log.e(TAG, "onFailureInsertChatList: " + e.getMessage());
                                            }
                                        }));

                                compositeDisposable.add(chatRepository.insert(new RoomImages(roomName, bitmapPath))
                                        .observeOn(io.reactivex.schedulers.Schedulers.io())
                                        .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                                        .subscribeWith(new DisposableCompletableObserver() {
                                            @Override
                                            public void onComplete() {
                                            }

                                            @Override
                                            public void onError(@NonNull Throwable e) {
                                                Log.e(TAG, "onFailureInsertImage: " + e.getMessage());
                                            }
                                        }));
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                Log.e("CHAT", "onError: " + e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        }));
            } else {
                chatMessagesList.add(message);
                updateNotification();

                compositeDisposable.add(chatMessagesRepository.insert(message)
                        .observeOn(io.reactivex.schedulers.Schedulers.io())
                        .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                Log.e(TAG, "onFailureInsertChatList: " + e.getMessage());
                            }
                        }));
            }
        }
    };

    Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String name = args[0].toString();

            ChatMessages chatMessages = new ChatMessages(name, name + " has leaved the room", roomName, 3);
            chatMessagesList.add(chatMessages);
            updateNotification();

            compositeDisposable.add(chatMessagesRepository.insert(chatMessages)
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            Log.e(TAG, "onFailureInsertChatList: " + e.getMessage());
                        }
                    }));
        }
    };

    private void updateNotification() {

        //------dio za direct reply gumb u notifikaciji-----
        RemoteInput remoteInput = new RemoteInput.Builder(FOREGROUND_SERVICE_REMOTE_BUILDER_KEY)
                .setLabel(getString(R.string.remoteReplyHint))
                .build();

        Intent replyIntent = new Intent(this, DirectReplyReceiver.class);
        replyIntent.putExtra(FOREGROUND_SERVICE_ROOM_NAME, roomName);
        replyIntent.putExtra(FOREGROUND_SERVICE_USER_NAME, userName);
        replyIntent.putExtra(FOREGROUND_SERVICE_PROFILE_PICTURE_URL, profilePictureURL);
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(this, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_send_text_message,
                getString(R.string.reply),
                replyPendingIntent
        ).addRemoteInput(remoteInput).build();
        //-------------------------------------------------

        //---------gumb za napuštanje sobe kroz notifikaciju--------------
        Intent leaveRoomIntent = new Intent(this, LeaveRoomReceiver.class);
        leaveRoomIntent.putExtra(FOREGROUND_SERVICE_USER_NAME, userName);
        leaveRoomIntent.putExtra(FOREGROUND_SERVICE_ROOM_NAME, roomName);
        PendingIntent leaveRoomPendingIntent = PendingIntent.getBroadcast(this, 0, leaveRoomIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //-------------------------------------------------------------------

        //-------povratak u chat aktivnost pri kliku na notifikaciju-------
        Intent chatActivity = new Intent(this, Chat.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, chatActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        //--------------------------------------------------

        String notificationTitle = chatMessagesList.get(0).getRoomName();

        Person person = new Person.Builder().setName(userName)
                .setIcon(IconCompat.createWithBitmap(getAvatarFromUrl(this, profilePictureURL)))
                .build();
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(person);
        messagingStyle.setConversationTitle(notificationTitle);
        messagingStyle.setGroupConversation(true);

        if (chatMessagesList.size() >= 7) {
            for (int i = chatMessagesList.size() - 1; i != chatMessagesList.size() - 8; i--) {
                ChatMessages message = chatMessagesList.get(i);
                String content;
                String name = null;
                String profilePictureURL = message.getProfilePictureURL();
                Person person2;

                if (message.getViewType() != 2 || message.getViewType() != 3) {
                    name = message.getUserName();
                }

                if (message.getViewType() == 6 || message.getViewType() == 5) {
                    content = "PICTURE";
                } else {
                    content = message.getMessageContent();
                }

                if (profilePictureURL == null) {
                    person2 = new Person.Builder()
                            .setName(name)
                            .build();
                } else {
                    person2 = new Person.Builder()
                            .setName(name)
                            .setIcon(IconCompat.createWithBitmap(getAvatarFromUrl(this, profilePictureURL)))
                            .build();
                }
                NotificationCompat.MessagingStyle.Message notificationMessage =
                        new NotificationCompat.MessagingStyle.Message(
                                content,
                                System.currentTimeMillis(),
                                person2
                        );
                messagingStyle.addMessage(notificationMessage);
            }
        } else {
            for (ChatMessages message : chatMessagesList) {

                String content;
                String name = null;
                String profilePictureURL = message.getProfilePictureURL();
                Person person2;

                if (message.getViewType() != 2 || message.getViewType() != 3) {
                    name = message.getUserName();
                }

                if (message.getViewType() == 6 || message.getViewType() == 5) {
                    content = "PICTURE";
                } else {
                    content = message.getMessageContent();
                }

                if (profilePictureURL == null) {
                    person2 = new Person.Builder()
                            .setName(name)
                            .build();
                } else {
                    person2 = new Person.Builder()
                            .setName(name)
                            .setIcon(IconCompat.createWithBitmap(getAvatarFromUrl(this, profilePictureURL)))
                            .build();
                }
                NotificationCompat.MessagingStyle.Message notificationMessage =
                        new NotificationCompat.MessagingStyle.Message(
                                content,
                                System.currentTimeMillis(),
                                person2
                        );
                messagingStyle.addMessage(notificationMessage);
            }
        }

        Notification notification = new NotificationCompat.Builder(this, GROUP_CHAT_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL)
                .setStyle(messagingStyle)
                .addAction(replyAction)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_fist_logo_notification_blue)
                .addAction(R.drawable.ic_go_back, getString(R.string.leaveRoom), leaveRoomPendingIntent)
                .setContentIntent(pendingIntent)
                .setNotificationSilent()
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, notification);
    }

    private String sendMessage(String content) {

        return content;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        compositeDisposable2.dispose();
        socket.off("newUserToChatRoom", onNewUser);
        socket.off("updateChat", onUpdateChat);
        socket.off("userLeftChatRoom", onUserLeft);
        socket = null;
    }
}

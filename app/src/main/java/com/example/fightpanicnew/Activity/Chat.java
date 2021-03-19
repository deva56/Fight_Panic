/*
Aktivnost za grupni chat između korisnika. Aktivnost se sastoji od mnogo callbackova ovisno o raznim situacijama, slanju tekstualne poruke ili
slanja slike. Ako korisnik ode u pozadinu poziva se foreground service koji pokazuje korisniku poruke iz sobe i daje opciju odgovaranja na poruke
te izlaska iz sobe kao i iz glavne aktivnosti. Ako korisnik napusti sobu sve poruke i slike koje je imao u njoj se brišu. Sva komunikacija
sa serverom te i sami chat između korisnika se vrši pomoću web socketa.
*/

package com.example.fightpanicnew.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fightpanicnew.Entity.ChatMessages;
import com.example.fightpanicnew.Entity.RoomImages;
import com.example.fightpanicnew.FightPanic;
import com.example.fightpanicnew.Fragments.ChatLeaveShowAlertDialog;
import com.example.fightpanicnew.Models.ChatClasses;
import com.example.fightpanicnew.R;
import com.example.fightpanicnew.RecyclerViewClasses.ChatAdapter;
import com.example.fightpanicnew.Services.SocketBackgroundService;
import com.example.fightpanicnew.ViewModels.ChatViewModel;
import com.example.fightpanicnew.databinding.ActivityChatBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
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
import static com.example.fightpanicnew.Constants.SHOW_LEAVE_WARNING;
import static com.example.fightpanicnew.Constants.SHOW_LEAVE_WARNING_VALUE;
import static com.example.fightpanicnew.HelperClasses.ImageManipulation.compressImage;
import static com.example.fightpanicnew.HelperClasses.ImageManipulation.saveToInternalStorage;
import static com.example.fightpanicnew.HelperClasses.ImageManipulation.saveToInternalStorageChat;

public class Chat extends AppCompatActivity {

    private static final String TAG = "CHAT";

    private ActivityChatBinding binding;

    private boolean isBackgroundTerminated;

    private Socket socket;
    private Gson gson;

    private String username;
    private String roomName;
    private String profilePictureURL;

    private final List<ChatMessages> chatList = new ArrayList<>();

    private RecyclerView recyclerView;
    private ChatAdapter adapter;

    private boolean userCurrentlyTyping = false;
    private final Handler typingHandler = new Handler();
    private static final int TYPING_TIMER_LENGTH = 600;
    public static final int PICK_IMAGE_CHAT = 1;

    private CompositeDisposable compositeDisposable;
    private io.reactivex.disposables.CompositeDisposable compositeDisposable2;

    private boolean showLeaveWarning = true;
    private boolean activityExitWay = false;

    private ChatViewModel chatViewModel;

    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        executor = ((FightPanic) this.getApplication()).getExecutor();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        DARK_MODE_VALUE = sharedPref.getBoolean(DARK_MODE_SWITCH, false);

        if (!DARK_MODE_VALUE) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        compositeDisposable = new CompositeDisposable();
        compositeDisposable2 = new io.reactivex.disposables.CompositeDisposable();
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        Intent foregroundService = new Intent(this, SocketBackgroundService.class);
        stopService(foregroundService);

        SharedPreferences sharedPreferencesBackgroundTerminated = getSharedPreferences(IS_BACKGROUND_TERMINATED, MODE_PRIVATE);
        isBackgroundTerminated = sharedPreferencesBackgroundTerminated.getBoolean(IS_BACKGROUND_TERMINATED_VALUE, false);
        SharedPreferences.Editor editor = sharedPreferencesBackgroundTerminated.edit();
        editor.putBoolean(IS_BACKGROUND_TERMINATED_VALUE, false);
        editor.apply();

        SharedPreferences sharedPreferencesUserLoggedIn = getSharedPreferences(SHOW_LEAVE_WARNING, MODE_PRIVATE);
        showLeaveWarning = sharedPreferencesUserLoggedIn.getBoolean(SHOW_LEAVE_WARNING_VALUE, true);

        username = getIntent().getStringExtra("Username");
        roomName = getIntent().getStringExtra("RoomName");
        profilePictureURL = getIntent().getStringExtra("ProfilePictureURL");

        Toolbar toolbar = findViewById(R.id.ChatActivityAppBar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(roomName);

        gson = new Gson();

        recyclerView = binding.RecyclerViewChatActivity;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new ChatAdapter(this, chatList);
        recyclerView.setAdapter(adapter);

        binding.send.setOnClickListener(view1 -> sendMessage());

        socket = ((FightPanic) this.getApplication()).getAppSocket();
        socket.on("newUserToChatRoom", onNewUser);
        socket.on("updateChat", onUpdateChat);
        socket.on("userLeftChatRoom", onUserLeft);
        socket.on("typing", typing);
        socket.on("stopTyping", stopTyping);
        socket.on("leaveRoom", onLeaveRoom);
        socket.on("leaveRoomFinal", onLeaveRoomFinal);
        socket.on("leaveRoomWarning", onLeaveRoomWarning);
        socket.on("roomDeleted", onRoomDeleted);
        socket.on("userSetActive", onUserSetActive);

        if (isBackgroundTerminated) {
            compositeDisposable2.add(chatViewModel.getChatMessagesList(roomName)
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribe(this::onSuccessGetAllChats, this::onFailureGetAllChats));
            ChatClasses.initialData initialData = new ChatClasses.initialData(username, roomName, true, showLeaveWarning);
            String jsonData = gson.toJson(initialData);
            socket.emit("subscribe", jsonData);
        } else {
            ChatClasses.initialData initialData = new ChatClasses.initialData(username, roomName, false, showLeaveWarning);
            String jsonData = gson.toJson(initialData);
            socket.emit("subscribe", jsonData);
        }

        binding.ChatActivityEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == R.id.send || i == EditorInfo.IME_NULL) {
                sendMessage();
                return true;
            }
            return false;
        });

        binding.ChatActivityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    binding.send.setRotation(-10);
                } else {
                    binding.send.setRotation(0);
                }

                if (!userCurrentlyTyping) {
                    userCurrentlyTyping = true;
                    ChatClasses.UserTyping userTyping = new ChatClasses.UserTyping(username, roomName);
                    String jsonData = gson.toJson(userTyping);
                    socket.emit("userTyping", jsonData);
                }
                typingHandler.removeCallbacks(onTypingTimeout);
                typingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 0) {
                    binding.send.setRotation(-10);
                } else {
                    binding.send.setRotation(0);
                }
            }
        });

        binding.chooseGaleryPicture.setOnClickListener(view12 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_CHAT);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CHAT && data != null) {
            Uri uri = data.getData();
            executor.execute(() -> {
                try {
                    Bitmap bitmap = Glide.with(getApplicationContext()).asBitmap().load(uri).submit(500, 500).get();
                    sendMessageImage(bitmap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        ChatClasses.initialData initialData = new ChatClasses.initialData(username, roomName, false, showLeaveWarning);
        String jsonData = gson.toJson(initialData);
        socket.emit("leaveRoom", jsonData);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ChatClasses.initialData initialData = new ChatClasses.initialData(username, roomName, false, showLeaveWarning);
            String jsonData = gson.toJson(initialData);
            socket.emit("leaveRoom", jsonData);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.lastPersonInRoom);
        builder.setPositiveButton(getString(R.string.stay), (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setNegativeButton(getString(R.string.leave), (dialogInterface, i) -> {
            ChatClasses.initialData initialData = new ChatClasses.initialData(username, roomName, false, showLeaveWarning);
            String jsonData = gson.toJson(initialData);
            socket.emit("deleteRoom", jsonData);
            dialogInterface.dismiss();

            compositeDisposable2.add(chatViewModel.deleteChatMessagesList(roomName)
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribe(this::onSuccessDeleteMessagesList, this::onFailureDeleteMessagesList));

            compositeDisposable2.add(chatViewModel.getAllRoomImages(roomName)
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribe(this::onSuccessGetAllRooms, this::onFailureGetAllRooms));

            activityExitWay = true;

            finish();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
        compositeDisposable2.add(chatViewModel.deleteAllRoomImages(roomName)
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

    private void onSuccessDeleteMessagesList() {
        Log.d("CHAT", "Uspješno obrisana chat list iz baze.");

    }

    private void onFailureDeleteMessagesList(Throwable throwable) {
        Log.e("CHAT", "onError: " + throwable.getMessage());
    }

    private void showAlertLeaveWarning() {
        ChatLeaveShowAlertDialog chatLeaveShowAlertDialog = new ChatLeaveShowAlertDialog(username, roomName, socket, gson);
        chatLeaveShowAlertDialog.show(getSupportFragmentManager(), ChatLeaveShowAlertDialog.TAG);
    }

    Emitter.Listener onNewUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(() -> {
                String name = args[0].toString();
                ChatMessages message = new ChatMessages(name, "", roomName, 2);
                chatList.add(message);
                adapter.notifyItemInserted(chatList.size());
                recyclerView.scrollToPosition(chatList.size() - 1);

                ChatMessages chatMessages = new ChatMessages(name, name + " has entered the room", roomName, 2);
                compositeDisposable2.add(chatViewModel.insert(chatMessages)
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
            });
        }
    };

    Emitter.Listener onUpdateChat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                ChatMessages message = gson.fromJson(args[0].toString(), ChatMessages.class);
                if (message.getViewType() == 6) {
                    compositeDisposable.add(Observable.just(saveToInternalStorageChat(message.getMessageContent(), getApplicationContext()))
                            .observeOn(Schedulers.io())
                            .subscribeOn(Schedulers.io())
                            .subscribeWith(new DisposableObserver<String>() {
                                @Override
                                public void onNext(@io.reactivex.rxjava3.annotations.NonNull String bitmapPath) {
                                    message.setBitmapPath(bitmapPath);
                                    message.setMessageContent("");
                                    runOnUiThread(() -> {
                                        chatList.add(message);
                                        adapter.notifyItemInserted(chatList.size());
                                        recyclerView.scrollToPosition(chatList.size() - 1);
                                    });

                                    compositeDisposable2.add(chatViewModel.insert(message)
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

                                    compositeDisposable2.add(chatViewModel.insert(new RoomImages(roomName, bitmapPath))
                                            .observeOn(io.reactivex.schedulers.Schedulers.io())
                                            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                                            .subscribe(this::onCompleteInsert, this::onError));
                                }

                                private void onCompleteInsert() {
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
                    chatList.add(message);
                    adapter.notifyItemInserted(chatList.size());
                    recyclerView.scrollToPosition(chatList.size() - 1);

                    compositeDisposable2.add(chatViewModel.insert(message)
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
            });
        }
    };

    Emitter.Listener typing = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                String name = args[0].toString();
                ChatMessages message = new ChatMessages(name, "", "", 4);
                chatList.add(message);
                adapter.notifyItemInserted(chatList.size());
                recyclerView.scrollToPosition(chatList.size() - 1);
            });
        }
    };

    Emitter.Listener stopTyping = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                String name = args[0].toString();
                for (int i = chatList.size() - 1; i >= 0; i--) {
                    ChatMessages message = chatList.get(i);
                    if (message.getViewType() == 4 && message.getUserName().equals(name)) {
                        chatList.remove(i);
                        adapter.notifyItemRemoved(i);
                    }
                }
            });
        }
    };

    Emitter.Listener onLeaveRoom = args -> runOnUiThread(this::showAlert);

    Emitter.Listener onLeaveRoomWarning = args -> runOnUiThread(this::showAlertLeaveWarning);

    Emitter.Listener onLeaveRoomFinal = args -> {

        compositeDisposable2.add(chatViewModel.getAllRoomImages(roomName)
                .observeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribe(this::onSuccessGetAllRooms, this::onFailureGetAllRooms));

        compositeDisposable2.add(chatViewModel.deleteChatMessagesList(roomName)
                .observeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribe(this::onSuccessDeleteMessagesList, this::onFailureDeleteMessagesList));

        activityExitWay = true;

        finish();
    };

    Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                String name = args[0].toString();
                ChatMessages message = new ChatMessages(name, "", "", 3);
                chatList.add(message);
                adapter.notifyItemInserted(chatList.size());
                recyclerView.scrollToPosition(chatList.size() - 1);

                ChatMessages chatMessages = new ChatMessages(message.getUserName(), message.getUserName() + " has leaved the room", roomName, 3);
                compositeDisposable2.add(chatViewModel.insert(chatMessages)
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
            });
        }
    };

    Emitter.Listener onRoomDeleted = args -> {
        compositeDisposable2.add(chatViewModel.getAllRoomImages(roomName)
                .observeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribe(this::onSuccessGetAllRooms, this::onFailureGetAllRooms));

        compositeDisposable2.add(chatViewModel.deleteChatMessagesList(roomName)
                .observeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribe(this::onSuccessDeleteMessagesList, this::onFailureDeleteMessagesList));

        activityExitWay = true;

        runOnUiThread(() -> Toast.makeText(getApplicationContext(), getString(R.string.roomDeletedWhileAway), Toast.LENGTH_LONG).show());

        finish();
    };

    Emitter.Listener onUserSetActive = args -> {
        compositeDisposable2.add(chatViewModel.getChatMessagesList(roomName)
                .observeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribe(this::onSuccessGetAllChats, this::onFailureGetAllChats));
    };

    private void sendMessageImage(Bitmap bitmap) {
        userCurrentlyTyping = true;

        ChatMessages message = new ChatMessages(username, "", roomName, 5);
        message.setProfilePictureURL(profilePictureURL);

        compositeDisposable.add(Observable.just(saveToInternalStorage(bitmap, getApplicationContext()))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull String bitmapPath) {
                        message.setBitmapPath(bitmapPath);
                        runOnUiThread(() -> {
                            chatList.add(message);
                            adapter.notifyItemInserted(chatList.size());
                            recyclerView.scrollToPosition(chatList.size() - 1);
                        });

                        compositeDisposable2.add(chatViewModel.insert(message)
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

                        compositeDisposable2.add(chatViewModel.insert(new RoomImages(roomName, bitmapPath))
                                .observeOn(io.reactivex.schedulers.Schedulers.io())
                                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                                .subscribe(this::onCompleteInsert, this::onError));
                    }

                    private void onCompleteInsert() {
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.e("CHAT", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                }));

        ChatClasses.sendMessage sendMessage = new ChatClasses.sendMessage(username, "", roomName, 6, profilePictureURL);

        compositeDisposable.add(Observable.just(compressImage(bitmap))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull String s) {
                        sendMessage.setMessageContent(s);
                        String jsonData = gson.toJson(sendMessage);
                        socket.emit("newMessage", jsonData);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.e("ERROR", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    private void sendMessage() {

        userCurrentlyTyping = true;

        String content = binding.ChatActivityEditText.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            binding.ChatActivityEditText.requestFocus();
            return;
        }

        ChatMessages message = new ChatMessages(username, content, roomName, 0);
        message.setProfilePictureURL(profilePictureURL);
        chatList.add(message);
        adapter.notifyItemInserted(chatList.size());
        binding.ChatActivityEditText.setText("");
        recyclerView.scrollToPosition(chatList.size() - 1);

        compositeDisposable2.add(chatViewModel.insert(message)
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

        ChatClasses.sendMessage sendMessage = new ChatClasses.sendMessage(username, content, roomName, 1, profilePictureURL);
        String jsonData = gson.toJson(sendMessage);
        socket.emit("newMessage", jsonData);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!activityExitWay) {

            ChatClasses.sendMessageUserInactiveOrActive sendMessageUserInactiveOrActive = new ChatClasses.sendMessageUserInactiveOrActive(roomName, username);
            String jsonData = gson.toJson(sendMessageUserInactiveOrActive);
            socket.emit("userInactive", jsonData);

            Intent foregroundService = new Intent(this, SocketBackgroundService.class);
            foregroundService.putExtra(FOREGROUND_SERVICE_ROOM_NAME, roomName);
            foregroundService.putExtra(FOREGROUND_SERVICE_USER_NAME, username);
            foregroundService.putExtra(FOREGROUND_SERVICE_PROFILE_PICTURE_URL, profilePictureURL);
            ContextCompat.startForegroundService(this, foregroundService);

            SharedPreferences sharedPreferencesBackgroundTerminated = getSharedPreferences(IS_BACKGROUND_TERMINATED, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesBackgroundTerminated.edit();
            editor.putBoolean(IS_BACKGROUND_TERMINATED_VALUE, true);
            editor.putString(FOREGROUND_SERVICE_ROOM_NAME, roomName);
            editor.putString(FOREGROUND_SERVICE_USER_NAME, username);
            editor.putString(FOREGROUND_SERVICE_PROFILE_PICTURE_URL, profilePictureURL);
            editor.apply();

            isBackgroundTerminated = true;
        }

        socket.off("newUserToChatRoom", onNewUser);
        socket.off("updateChat", onUpdateChat);
        socket.off("userLeftChatRoom", onUserLeft);
        socket.off("typing", typing);
        socket.off("stopTyping", stopTyping);
        socket.off("leaveRoom", onLeaveRoom);
        socket.off("leaveRoomFinal", onLeaveRoomFinal);
        socket.off("roomDeleted", onRoomDeleted);
        socket.off("userSetActive", onUserSetActive);

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Intent foregroundService = new Intent(this, SocketBackgroundService.class);
        stopService(foregroundService);

        SharedPreferences sharedPreferencesBackgroundTerminated = getSharedPreferences(IS_BACKGROUND_TERMINATED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesBackgroundTerminated.edit();
        editor.putBoolean(IS_BACKGROUND_TERMINATED_VALUE, false);
        editor.putString(FOREGROUND_SERVICE_ROOM_NAME, "");
        editor.putString(FOREGROUND_SERVICE_USER_NAME, "");
        editor.putString(FOREGROUND_SERVICE_PROFILE_PICTURE_URL, "");
        editor.apply();

        isBackgroundTerminated = false;

        socket.on("newUserToChatRoom", onNewUser);
        socket.on("updateChat", onUpdateChat);
        socket.on("userLeftChatRoom", onUserLeft);
        socket.on("typing", typing);
        socket.on("stopTyping", stopTyping);
        socket.on("leaveRoom", onLeaveRoom);
        socket.on("leaveRoomFinal", onLeaveRoomFinal);
        socket.on("roomDeleted", onRoomDeleted);
        socket.on("userSetActive", onUserSetActive);

        ChatClasses.sendMessageUserInactiveOrActive sendMessageUserInactiveOrActive = new ChatClasses.sendMessageUserInactiveOrActive(roomName, username);
        String jsonData = gson.toJson(sendMessageUserInactiveOrActive);
        socket.emit("userActive", jsonData);
    }

    private void onSuccessGetAllChats(List<ChatMessages> chatMessages) {
        runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
            chatList.clear();
            chatList.addAll(chatMessages);
            adapter.notifyItemInserted(chatList.size());
            recyclerView.scrollToPosition(chatList.size() - 1);
        });
    }

    private void onFailureGetAllChats(Throwable throwable) {
        Log.e(TAG, "onFailureGetAllChats: " + throwable.getMessage());

        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.errorInLoadingChats), Snackbar.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("LEAVE_ROOM_BOOLEAN", false)) {
            String username = intent.getStringExtra(FOREGROUND_SERVICE_USER_NAME);
            String roomName = intent.getStringExtra(FOREGROUND_SERVICE_ROOM_NAME);

            ChatClasses.initialData initialData = new ChatClasses.initialData(username, roomName, false, showLeaveWarning);
            String jsonData = gson.toJson(initialData);
            socket.emit("leaveRoom", jsonData);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        compositeDisposable2.dispose();
        ChatClasses.initialData initialData = new ChatClasses.initialData(username, roomName, false, showLeaveWarning);
        String jsonData = gson.toJson(initialData);
        socket.emit("unsubscribe", jsonData);
        Intent foregroundService = new Intent(this, SocketBackgroundService.class);
        stopService(foregroundService);
        socket.off("newUserToChatRoom", onNewUser);
        socket.off("updateChat", onUpdateChat);
        socket.off("userLeftChatRoom", onUserLeft);
        socket.off("typing", typing);
        socket.off("stopTyping", stopTyping);
        socket.off("leaveRoom", onLeaveRoom);
        socket.off("leaveRoomFinal", onLeaveRoomFinal);
        socket.off("leaveRoomWarning", onLeaveRoomWarning);
        socket.off("roomDeleted", onRoomDeleted);
        socket.off("userSetActive", onUserSetActive);
        socket = null;
    }

    private final Runnable onTypingTimeout = () -> {
        if (!userCurrentlyTyping) return;

        userCurrentlyTyping = false;
        ChatClasses.UserTyping userTyping = new ChatClasses.UserTyping(username, roomName);
        String jsonData = gson.toJson(userTyping);
        socket.emit("userStopTyping", jsonData);
    };
}
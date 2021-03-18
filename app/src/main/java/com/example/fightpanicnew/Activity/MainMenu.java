package com.example.fightpanicnew.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
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

import com.example.fightpanicnew.Entity.User;
import com.example.fightpanicnew.R;
import com.example.fightpanicnew.ViewModels.UserViewModel;
import com.example.fightpanicnew.databinding.ActivityMainMenuBinding;

import java.util.Arrays;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static com.example.fightpanicnew.Constants.DARK_MODE_SWITCH;
import static com.example.fightpanicnew.Constants.DARK_MODE_VALUE;
import static com.example.fightpanicnew.Constants.IS_LOGGED_IN_DATA;
import static com.example.fightpanicnew.Constants.IS_LOGGED_IN_STATE;
import static com.example.fightpanicnew.Constants.LOGGED_USER_ID;
import static com.example.fightpanicnew.Constants.LOGIN_INFORMATION;
import static com.example.fightpanicnew.HelperClasses.ImageManipulation.loadImageFromStoragePicasso;

public class MainMenu extends AppCompatActivity {

    private boolean isLoggedInBool;
    private boolean hasInternetConnection;

    private CompositeDisposable compositeDisposable;
    private io.reactivex.disposables.CompositeDisposable compositeDisposable2;

    private ActivityMainMenuBinding binding;

    private NetworkReceiver receiver;

    private UserViewModel userViewModel;

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();

            if (networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
                hasInternetConnection = true;
            } else {
                hasInternetConnection = false;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        DARK_MODE_VALUE = sharedPref.getBoolean(DARK_MODE_SWITCH, false);

        if (!DARK_MODE_VALUE) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        compositeDisposable = new CompositeDisposable();
        compositeDisposable2 = new io.reactivex.disposables.CompositeDisposable();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        Toolbar toolbar = findViewById(R.id.appBar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        loadData();

        binding.HowToUseButton.setOnClickListener(view14 -> {

        });

        binding.PanicCalendarButton.setOnClickListener(view1 -> {
            Intent a = new Intent(MainMenu.this, PanicCalendar.class);
            startActivity(a);
        });

        binding.PillDiaryButton.setOnClickListener(view12 -> {
            Intent a = new Intent(MainMenu.this, PillDiary.class);
            startActivity(a);
        });

        binding.FightClubButton.setOnClickListener(view13 -> {
            Intent a = new Intent(MainMenu.this, FightClub.class);
            startActivity(a);
        });

        binding.ProfileIconImageView.setOnClickListener(view12 -> {

            if (!isLoggedInBool && hasInternetConnection) {
                Intent intent = new Intent(MainMenu.this, ProfileRegisterOrLogin.class);
                startActivity(intent);
            } else if (!hasInternetConnection) {
                Toast noInternetConnectionToast = Toast.makeText(MainMenu.this, R.string.noInternetConnection, Toast.LENGTH_LONG);
                noInternetConnectionToast.setGravity(Gravity.CENTER, 0, 0);
                noInternetConnectionToast.show();
            } else {
                Intent intent = new Intent(MainMenu.this, Profile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadData();
    }

    private void loadData() {
        SharedPreferences sharedPreferencesUserLoggedIn = getSharedPreferences(IS_LOGGED_IN_DATA, MODE_PRIVATE);
        isLoggedInBool = sharedPreferencesUserLoggedIn.getBoolean(IS_LOGGED_IN_STATE, false);

        if (isLoggedInBool) {
            SharedPreferences sharedPreferencesEditLoginInformation = getSharedPreferences(LOGIN_INFORMATION, MODE_PRIVATE);
            String loggedUserID = sharedPreferencesEditLoginInformation.getString(LOGGED_USER_ID, "");

            compositeDisposable2.add(userViewModel.getUser(loggedUserID)
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribe(this::handleResponseGetUser, this::handleErrorGetUser));
        } else {
            binding.ProfileIconImageView.setBackgroundResource(R.drawable.ic_person);
        }
    }

    private void handleResponseGetUser(User user) {

        new Handler(Looper.getMainLooper()).post(() -> {
            loadImageFromStoragePicasso(user.getProfilePicturePathLocalStorage(), binding.ProfileIconImageView);
        });
    }

    private void handleErrorGetUser(Throwable throwable) {
        Log.e("GetProfileError", Arrays.toString(throwable.getStackTrace()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shr_toolbar_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            Intent i = new Intent(this, Settings.class);
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.information) {
            Intent a = new Intent(MainMenu.this, AboutApplication.class);
            startActivity(a);
            return true;
        }
        else if(item.getItemId() == R.id.mainMenuHowToUseApplication)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.howToUseApplication));
            builder.setMessage(R.string.howToUseApplicationDescription);
            builder.setPositiveButton(getString(R.string.gotIt), (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        compositeDisposable2.dispose();
        unregisterReceiver(receiver);
    }
}
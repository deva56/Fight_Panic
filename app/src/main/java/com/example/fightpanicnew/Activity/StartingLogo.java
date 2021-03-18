/*
Početna aktivnost koja predstavlja splash-logo pri učitavanju aplikacije.
Podešava osnovne parametre day/night teme te iz sharedPrefs-a vadi podatke potrebne za nastavak učitavanja aplikacije.
*/

package com.example.fightpanicnew.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.example.fightpanicnew.R;
import com.example.fightpanicnew.databinding.ActivityStartingLogoBinding;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.fightpanicnew.Constants.DARK_MODE_SWITCH;
import static com.example.fightpanicnew.Constants.DARK_MODE_VALUE;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_INFORMATION;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_PROFILE_PICTURE_URL;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_ROOM_NAME;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_USER_NAME;
import static com.example.fightpanicnew.Constants.IS_BACKGROUND_TERMINATED;
import static com.example.fightpanicnew.Constants.IS_BACKGROUND_TERMINATED_VALUE;
import static com.example.fightpanicnew.Constants.REGISTER_USER_INFORMATION_EDIT_TEXT_FIELDS_INFORMATION;
import static com.example.fightpanicnew.Constants.REGISTRATION_OR_LOGIN_OR_SKIP_MENU;
import static com.example.fightpanicnew.Constants.SHARED_PREFS;

public class StartingLogo extends AppCompatActivity {

    private Boolean registrationOrLoginOrSkipMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        DARK_MODE_VALUE = sharedPref.getBoolean(DARK_MODE_SWITCH, false);

        if (!DARK_MODE_VALUE) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        SharedPreferences sharedPreferencesBackgroundTerminated = getSharedPreferences(IS_BACKGROUND_TERMINATED, MODE_PRIVATE);
        boolean isChatTerminated = sharedPreferencesBackgroundTerminated.getBoolean(IS_BACKGROUND_TERMINATED_VALUE, false);

        ActivityStartingLogoBinding binding = ActivityStartingLogoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        loadData();

        if (isChatTerminated) {
            Intent b = new Intent(StartingLogo.this, FightClub.class);
            b.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            b.putExtra("IsChatTerminated", true);
            b.putExtra("RoomName", sharedPreferencesBackgroundTerminated.getString(FOREGROUND_SERVICE_ROOM_NAME, ""));
            startActivity(b);
            finish();
            return;
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!registrationOrLoginOrSkipMenu) {
                    deleteData();
                    Intent a = new Intent(StartingLogo.this, RegistrationOrLoginOrSkip.class);
                    a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(a);
                } else {
                    Intent a = new Intent(StartingLogo.this, MainMenu.class);
                    a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(a);
                }
                finish();
            }
        }, 2000);
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        registrationOrLoginOrSkipMenu = sharedPreferences.getBoolean(REGISTRATION_OR_LOGIN_OR_SKIP_MENU, false);
    }

    public void deleteData() {
        SharedPreferences sharedPreferencesEditTextInformation = getSharedPreferences(EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        sharedPreferencesEditTextInformation.edit().clear().apply();

        SharedPreferences sharedPreferencesEditTextUserInformation = getSharedPreferences(REGISTER_USER_INFORMATION_EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        sharedPreferencesEditTextUserInformation.edit().clear().apply();
    }
}
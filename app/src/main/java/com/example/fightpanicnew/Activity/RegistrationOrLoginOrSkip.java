/*
Aktivnost koju korisnik vidi prvi put kada ulazi u aplikaciju.
Bira između registracije novog računa, logiranja u postojeći ili preskakanja na glavni meni.
*/

package com.example.fightpanicnew.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fightpanicnew.Activity.Registration.RegistrationBasicDetails;
import com.example.fightpanicnew.databinding.ActivityRegistrationOrLoginOrSkipBinding;

import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_INFORMATION;
import static com.example.fightpanicnew.Constants.REGISTER_USER_INFORMATION_EDIT_TEXT_FIELDS_INFORMATION;
import static com.example.fightpanicnew.Constants.REGISTRATION_OR_LOGIN_OR_SKIP_MENU;
import static com.example.fightpanicnew.Constants.SHARED_PREFS;

public class RegistrationOrLoginOrSkip extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegistrationOrLoginOrSkipBinding binding = ActivityRegistrationOrLoginOrSkipBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.SkipThisStepForNow.setOnClickListener(view1 -> {
            saveData();
            deleteData();
            startActivity(new Intent(RegistrationOrLoginOrSkip.this, MainMenu.class));
            finish();
        });

        binding.RegisterNewAccountButton.setOnClickListener(view12 -> {
            startActivity(new Intent(RegistrationOrLoginOrSkip.this, RegistrationBasicDetails.class));
        });

        binding.LoginToAnExistingAccount.setOnClickListener(view13 -> {
            startActivity(new Intent(RegistrationOrLoginOrSkip.this, ProfileRegisterOrLogin.class));
        });
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(REGISTRATION_OR_LOGIN_OR_SKIP_MENU, true);
        editor.apply();
    }

    public void deleteData() {
        SharedPreferences sharedPreferencesEditTextInformation = getSharedPreferences(EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        sharedPreferencesEditTextInformation.edit().clear().apply();

        SharedPreferences sharedPreferencesEditTextUserInformation = getSharedPreferences(REGISTER_USER_INFORMATION_EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        sharedPreferencesEditTextUserInformation.edit().clear().apply();
    }

}
/*
Prva aktivnost u procesu registracije, unose se osnovni podatci o računu:
korisničko ime, e-mail i lozinka. Korisničko ime i e-mail moraju biti jedinstveni te se to pri registraciji provjerava.
Nakon uspješne provjere korisnik nastavlja dalje u procesu registracije.
*/

package com.example.fightpanicnew.Activity.Registration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.preference.PreferenceManager;

import com.example.fightpanicnew.Activity.ProfileRegisterOrLogin;
import com.example.fightpanicnew.Activity.RegistrationOrLoginOrSkip;
import com.example.fightpanicnew.Models.Response;
import com.example.fightpanicnew.R;
import com.example.fightpanicnew.databinding.ActivityRegistrationBasicDetailsBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.example.fightpanicnew.Constants.DARK_MODE_SWITCH;
import static com.example.fightpanicnew.Constants.DARK_MODE_VALUE;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_EMAIL;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_INFORMATION;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_PASSWORD;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_USERNAME;
import static com.example.fightpanicnew.Constants.REGISTER_USER_INFORMATION_EDIT_TEXT_FIELDS_INFORMATION;
import static com.example.fightpanicnew.Constants.REGISTRATION_OR_LOGIN_OR_SKIP_MENU;
import static com.example.fightpanicnew.Constants.SHARED_PREFS;
import static com.example.fightpanicnew.HelperClasses.Validation.validateEmail;
import static com.example.fightpanicnew.HelperClasses.Validation.validateFields;
import static com.example.fightpanicnew.HelperClasses.Validation.validatePasswordRegistration;
import static com.example.fightpanicnew.Network.RetrofitBuilder.getRetrofit;

public class RegistrationBasicDetails extends AppCompatActivity {

    private ActivityRegistrationBasicDetailsBinding binding;
    private CompositeDisposable compositeDisposable;
    private int controlContinueFlag = 0;
    private boolean registrationOrLoginOrSkipMenu;

    private String name;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBasicDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        DARK_MODE_VALUE = sharedPref.getBoolean(DARK_MODE_SWITCH, false);

        if (!DARK_MODE_VALUE) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        loadEditTextFieldsData();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        registrationOrLoginOrSkipMenu = sharedPreferences.getBoolean(REGISTRATION_OR_LOGIN_OR_SKIP_MENU, false);

        compositeDisposable = new CompositeDisposable();

        Toolbar toolbar = findViewById(R.id.RegisterBasicDetailsAppBar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.registrationBasicDetails));

        binding.RegistrationProgressBar.setVisibility(View.GONE);

        //Dio koda za bojanje ikone u text input layoutu - bez toga ikona je default bijela
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_email);
        assert drawable != null;
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorSecondary));
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        binding.EmailEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.register_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.registerUserDetailsNext) {
            name = Objects.requireNonNull(binding.UsernameEditText.getText()).toString();
            email = Objects.requireNonNull(binding.EmailEditText.getText()).toString();
            password = Objects.requireNonNull(binding.PasswordEditText.getText()).toString();
            String confirmPassword = Objects.requireNonNull(binding.ConfirmPasswordEditText.getText()).toString();

            setError();

            int err = 0;

            if (!validateFields(name)) {
                err++;
                binding.UsernameTextInputLayout.setError(getString(R.string.userNameNotEmpty));
            }

            if (!validateEmail(email)) {
                err++;
                binding.EmailTextInputLayout.setError(getString(R.string.emailNotValid));
            }

            if (!validateFields(email)) {
                err++;
                binding.EmailTextInputLayout.setError(getString(R.string.emailNotEmpty));
            }

            if (!validatePasswordRegistration(password)) {
                err++;
                binding.PasswordTextInputLayout.setError(getString(R.string.registrationPasswordTooShort));
            }

            if (!validateFields(password)) {
                err++;
                binding.PasswordTextInputLayout.setError(getString(R.string.passwordNotEmpty));
            }

            if (!password.equals(confirmPassword)) {
                err++;
                binding.ConfirmPasswordTextInputLayout.setError(getString(R.string.passwordConfirmationDoesntMatch));
            }

            if (!validateFields(confirmPassword)) {
                err++;
                binding.ConfirmPasswordTextInputLayout.setError(getString(R.string.passwordConfirmationNotEmpty));
            }

            if (err == 0) {
                binding.RegistrationProgressBar.setVisibility(View.VISIBLE);
                checkIfUserExists(name, email);
            }

            return true;

        } else if (item.getItemId() == android.R.id.home) {
            if (registrationOrLoginOrSkipMenu) {
                Intent i = getIntent();
                if (i.getBooleanExtra("GoBackFightClub", false)) {
                    onBackPressed();
                    return true;
                }
                Intent a = new Intent(RegistrationBasicDetails.this, ProfileRegisterOrLogin.class);
                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                deleteData();
                startActivity(a);
                finish();
                return true;
            } else {
                Intent i = getIntent();
                if (i.getBooleanExtra("GoBack", false)) {
                    Intent a = new Intent(RegistrationBasicDetails.this, ProfileRegisterOrLogin.class);
                    a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(a);
                } else {
                    saveEditTextFieldsDataGoBackButton();
                    Intent a = new Intent(RegistrationBasicDetails.this, RegistrationOrLoginOrSkip.class);
                    a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(a);
                }
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (registrationOrLoginOrSkipMenu) {
            Intent i = getIntent();

            if (i.getBooleanExtra("GoBackFightClub", false)) {
                finish();
                return;
            }

            Intent a = new Intent(RegistrationBasicDetails.this, ProfileRegisterOrLogin.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            deleteData();
            startActivity(a);
            finish();
        } else {
            Intent i = getIntent();
            if (i.getBooleanExtra("GoBack", false)) {
                Intent a = new Intent(RegistrationBasicDetails.this, ProfileRegisterOrLogin.class);
                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(a);
            } else {
                saveEditTextFieldsDataGoBackButton();
                Intent a = new Intent(RegistrationBasicDetails.this, RegistrationOrLoginOrSkip.class);
                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(a);
            }
            finish();
        }
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        name = Objects.requireNonNull(binding.UsernameEditText.getText()).toString();
        email = Objects.requireNonNull(binding.EmailEditText.getText()).toString();
        password = Objects.requireNonNull(binding.PasswordEditText.getText()).toString();

        savedInstanceState.putString(EDIT_TEXT_FIELDS_USERNAME, name);
        savedInstanceState.putString(EDIT_TEXT_FIELDS_EMAIL, email);
        savedInstanceState.putString(EDIT_TEXT_FIELDS_PASSWORD, password);

        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        name = savedInstanceState.getString(EDIT_TEXT_FIELDS_USERNAME);
        binding.UsernameEditText.setText(name);
        email = savedInstanceState.getString(EDIT_TEXT_FIELDS_EMAIL);
        binding.EmailEditText.setText(email);
        password = savedInstanceState.getString(EDIT_TEXT_FIELDS_PASSWORD);
        binding.PasswordEditText.setText(password);
    }

    private void setError() {

        binding.UsernameTextInputLayout.setError(null);
        binding.EmailTextInputLayout.setError(null);
        binding.PasswordTextInputLayout.setError(null);
        binding.ConfirmPasswordTextInputLayout.setError(null);
    }

    private void showSnackBarMessage(String message) {

        if (findViewById(android.R.id.content) != null) {

            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        }
    }

    private void checkToContinue() {
        if (controlContinueFlag == 1) {
            binding.RegistrationProgressBar.setVisibility(View.GONE);
            controlContinueFlag = 0;

            saveEditTextFieldsDataNextButton();
            Intent a = new Intent(RegistrationBasicDetails.this, RegistrationUserDetails.class);
            startActivity(a);

        } else {
            controlContinueFlag++;
        }
    }

    private void checkIfUserExists(String username, String email) {
        controlContinueFlag = 0;

        binding.RegistrationProgressBar.setVisibility(View.VISIBLE);

        compositeDisposable.add(getRetrofit().getExistingUserEmail(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseEmail, this::handleErrorEmail));
        compositeDisposable.add(getRetrofit().getExistingUsername(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUsername, this::handleErrorUsername));
    }

    private void handleResponseEmail(Response response) {

        if (response.getMessage().equals("User with same email already exists !")) {
            binding.EmailTextInputLayout.setError(getString(R.string.userWithSameEmailExists));
            binding.RegistrationProgressBar.setVisibility(View.GONE);
        } else {
            checkToContinue();
        }
    }

    private void handleResponseUsername(Response response) {

        if (response.getMessage().equals("User with same username already exists !")) {
            binding.UsernameTextInputLayout.setError(getString(R.string.userWithSameUsernameExists));
            binding.RegistrationProgressBar.setVisibility(View.GONE);
        } else {
            checkToContinue();
        }
    }

    private void handleErrorEmail(Throwable error) {

        binding.RegistrationProgressBar.setVisibility(View.GONE);

        if (!error.getMessage().equals("Attempt to invoke virtual method 'boolean java.lang.String.equals(java.lang.Object)' on a null object reference")) {
            if (error instanceof HttpException) {

                Gson gson = new GsonBuilder().create();

                try {
                    String errorBody = ((HttpException) error).response().errorBody().string();
                    Response response = gson.fromJson(errorBody, Response.class);
                    Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showSnackBarMessage(error.getMessage());
            }
        }
    }

    private void handleErrorUsername(Throwable error) {

        binding.RegistrationProgressBar.setVisibility(View.GONE);

        if (!error.getMessage().equals("Attempt to invoke virtual method 'boolean java.lang.String.equals(java.lang.Object)' on a null object reference")) {
            if (error instanceof HttpException) {

                Gson gson = new GsonBuilder().create();

                try {
                    String errorBody = ((HttpException) error).response().errorBody().string();
                    Response response = gson.fromJson(errorBody, Response.class);
                    Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showSnackBarMessage(error.getMessage());
            }
        }
    }

    private void saveEditTextFieldsDataGoBackButton() {
        SharedPreferences sharedPreferencesEditTextInformation = getSharedPreferences(EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesEditTextInformation.edit();
        editor.putString(EDIT_TEXT_FIELDS_USERNAME, binding.UsernameEditText.getText().toString());
        editor.putString(EDIT_TEXT_FIELDS_EMAIL, binding.EmailEditText.getText().toString());
        editor.apply();
    }

    private void saveEditTextFieldsDataNextButton() {
        SharedPreferences sharedPreferencesEditTextInformation = getSharedPreferences(EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesEditTextInformation.edit();
        editor.putString(EDIT_TEXT_FIELDS_USERNAME, binding.UsernameEditText.getText().toString());
        editor.putString(EDIT_TEXT_FIELDS_EMAIL, binding.EmailEditText.getText().toString());
        editor.putString(EDIT_TEXT_FIELDS_PASSWORD, binding.PasswordEditText.getText().toString());
        editor.apply();
    }

    private void loadEditTextFieldsData() {
        SharedPreferences sharedPreferencesEditTextInformation = getSharedPreferences(EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        binding.UsernameEditText.setText(sharedPreferencesEditTextInformation.getString(EDIT_TEXT_FIELDS_USERNAME, ""));
        binding.EmailEditText.setText(sharedPreferencesEditTextInformation.getString(EDIT_TEXT_FIELDS_EMAIL, ""));
        binding.PasswordEditText.setText(sharedPreferencesEditTextInformation.getString(EDIT_TEXT_FIELDS_PASSWORD, ""));
    }

    public void deleteData() {
        SharedPreferences sharedPreferencesEditTextInformation = getSharedPreferences(EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        sharedPreferencesEditTextInformation.edit().clear().apply();

        SharedPreferences sharedPreferencesEditTextUserInformation = getSharedPreferences(REGISTER_USER_INFORMATION_EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        sharedPreferencesEditTextUserInformation.edit().clear().apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.EmailEditText.setText("");
        compositeDisposable.clear();
    }
}
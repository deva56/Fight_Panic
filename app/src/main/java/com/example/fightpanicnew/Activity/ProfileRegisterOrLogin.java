/*
Aktivnost za logiranje korisnika. Ako korisnik nema račun može se kroz ovu aktivnost registrirati ili ako je zaboravio lozinku podnijeti
zahtjev za promjenu lozinke na e-mail pomoću jednokratnog tokena.
*/

package com.example.fightpanicnew.Activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.fightpanicnew.Activity.Registration.RegistrationBasicDetails;
import com.example.fightpanicnew.Fragments.ForgotPasswordFragment;
import com.example.fightpanicnew.Models.Response;
import com.example.fightpanicnew.Models.User;
import com.example.fightpanicnew.R;
import com.example.fightpanicnew.ViewModels.UserViewModel;
import com.example.fightpanicnew.databinding.ActivityProfileRegisterOrLoginBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.example.fightpanicnew.Constants.DARK_MODE_SWITCH;
import static com.example.fightpanicnew.Constants.DARK_MODE_VALUE;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_INFORMATION;
import static com.example.fightpanicnew.Constants.IS_LOGGED_IN_DATA;
import static com.example.fightpanicnew.Constants.IS_LOGGED_IN_STATE;
import static com.example.fightpanicnew.Constants.LOGGED_USER_ID;
import static com.example.fightpanicnew.Constants.LOGIN_EMAIL;
import static com.example.fightpanicnew.Constants.LOGIN_INFORMATION;
import static com.example.fightpanicnew.Constants.LOGIN_TOKEN;
import static com.example.fightpanicnew.Constants.REGISTER_USER_INFORMATION_EDIT_TEXT_FIELDS_INFORMATION;
import static com.example.fightpanicnew.Constants.REGISTRATION_OR_LOGIN_OR_SKIP_MENU;
import static com.example.fightpanicnew.Constants.SHARED_PREFS;
import static com.example.fightpanicnew.HelperClasses.Validation.validateEmail;
import static com.example.fightpanicnew.HelperClasses.Validation.validateFields;
import static com.example.fightpanicnew.Network.RetrofitBuilder.getRetrofit;

public class ProfileRegisterOrLogin extends AppCompatActivity implements ForgotPasswordFragment.Listener {

    private CompositeDisposable compositeDisposable;
    private io.reactivex.disposables.CompositeDisposable compositeDisposable2;
    private ProgressBar progressBar;
    private ActivityProfileRegisterOrLoginBinding binding;
    private UserViewModel userViewModel;
    private com.example.fightpanicnew.Entity.User tempUser;

    private boolean registrationOrLoginOrSkipMenu;

    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = firebaseStorage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileRegisterOrLoginBinding.inflate(getLayoutInflater());
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

        progressBar = findViewById(R.id.ProfileLoginProgressBar);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        registrationOrLoginOrSkipMenu = sharedPreferences.getBoolean(REGISTRATION_OR_LOGIN_OR_SKIP_MENU, false);

        setError();

        Toolbar toolbar = findViewById(R.id.ProfileLoginAppBar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.Register.setOnClickListener(view1 -> {
            Intent a = new Intent(ProfileRegisterOrLogin.this, RegistrationBasicDetails.class);
            if (!registrationOrLoginOrSkipMenu) {
                a.putExtra("GoBack", true);
            }
            startActivity(a);
        });

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_email);
        assert drawable != null;
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorSecondary));
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        binding.ProfileLoginEmailEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);

        binding.ForgotPassword.setOnClickListener(view12 -> {
            ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
            forgotPasswordFragment.show(getSupportFragmentManager(), ForgotPasswordFragment.TAG);
        });

        binding.LoginButton.setOnClickListener(view13 -> login());

    }

    private void login() {

        setError();

        String loginEmail = binding.ProfileLoginEmailEditText.getText().toString();
        String loginPassword = binding.ProfileLoginPasswordEditText.getText().toString();

        int err = 0;

        if (!validateEmail(loginEmail)) {
            err++;
            binding.ProfileLoginEmailTextInputLayout.setError(getString(R.string.emailNotValid));
        }

        if (!validateFields(loginEmail)) {
            err++;
            binding.ProfileLoginEmailTextInputLayout.setError(getString(R.string.emailNotEmpty));
        }

        if (!validateFields(loginPassword)) {
            err++;
            binding.ProfileLoginPasswordTextInputLayout.setError(getString(R.string.passwordNotEmpty));
        }

        if (err == 0) {

            loginProcess(loginEmail, loginPassword);
            progressBar.setVisibility(View.VISIBLE);
            binding.LoginButton.setEnabled(false);
            binding.Register.setEnabled(false);
            binding.ForgotPassword.setEnabled(false);
        }
    }

    private void loginProcess(String email, String password) {

        compositeDisposable.add(getRetrofit(email, password).login()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Response response) {

        if (response.getMessage().equals("Invalid Credentials !")) {
            showSnackBarMessage(getString(R.string.invalidCredentials));
            return;
        }

        SharedPreferences sharedPreferencesEditLoginInformation = getSharedPreferences(LOGIN_INFORMATION, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesEditLoginInformation.edit();
        editor.putString(LOGIN_TOKEN, response.getToken());
        editor.putString(LOGIN_EMAIL, response.getMessage());
        editor.apply();

        compositeDisposable2.add(userViewModel.getUserByEmail(response.getMessage())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribe(this::onSuccessUserOnDevice, this::onFailureUserOnDevice));
    }

    private void handleError(Throwable error) {

        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            binding.LoginButton.setEnabled(true);
            binding.Register.setEnabled(true);
            binding.ForgotPassword.setEnabled(true);

            if (error instanceof HttpException) {

                Gson gson = new GsonBuilder().create();

                try {

                    String errorBody = Objects.requireNonNull(Objects.requireNonNull(((HttpException) error).response()).errorBody()).string();
                    Response response = gson.fromJson(errorBody, Response.class);

                    if (response.getMessage().equals("Invalid Credentials !")) {
                        showSnackBarMessage(getString(R.string.invalidCredentials));
                    } else if (response.getMessage().equals("User Not Found !")) {
                        showSnackBarMessage(getString(R.string.userNotFound));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showSnackBarMessage();
            }
        });
    }

    private void onSuccessUserOnDevice(com.example.fightpanicnew.Entity.User user) {

        if (user.getProfilePicturePathLocalStorage() != null) {
            File profileImage = new File(user.getProfilePicturePathLocalStorage());
            if (!profileImage.exists()) {

                StorageReference firebaseStoragePictureReference = storageRef.child("fightPanicProfilePictures/" + user.getProfilePictureName());

                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File myPath = new File(directory, user.getProfilePictureName());

                firebaseStoragePictureReference.getFile(myPath).addOnSuccessListener(taskSnapshot -> {
                    checkToContinue(user.getId());
                }).addOnFailureListener(exception -> {

                    Log.d("TAG", "handleResponseGetProfile: " + exception.getLocalizedMessage());

                    runOnUiThread(() -> {
                        binding.LoginButton.setEnabled(true);
                        binding.Register.setEnabled(true);
                        binding.ForgotPassword.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.loginInUnsuccessfull), Snackbar.LENGTH_SHORT).show();
                    });
                });
            } else {
                checkToContinue(user.getId());
            }
        } else {
            StorageReference firebaseStoragePictureReference = storageRef.child("fightPanicProfilePictures/" + user.getProfilePictureName());

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File myPath = new File(directory, user.getProfilePictureName());

            firebaseStoragePictureReference.getFile(myPath).addOnSuccessListener(taskSnapshot -> {
                checkToContinue(user.getId());
            }).addOnFailureListener(exception -> {

                Log.d("TAG", "handleResponseGetProfile: " + exception.getLocalizedMessage());

                runOnUiThread(() -> {
                    binding.LoginButton.setEnabled(true);
                    binding.Register.setEnabled(true);
                    binding.ForgotPassword.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.loginInUnsuccessfull), Snackbar.LENGTH_SHORT).show();
                });
            });
        }
        checkToContinue(user.getId());
    }

    private void onFailureUserOnDevice(Throwable throwable) {
        SharedPreferences sharedPreferencesEditLoginInformation = getSharedPreferences(LOGIN_INFORMATION, MODE_PRIVATE);

        compositeDisposable.add(getRetrofit(sharedPreferencesEditLoginInformation.getString(LOGIN_TOKEN, "")).getProfile(sharedPreferencesEditLoginInformation.getString(LOGIN_EMAIL, ""))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseGetProfile, this::handleError));
    }

    private void handleResponseGetProfile(User user) {

        tempUser = new com.example.fightpanicnew.Entity.User(user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(),
                user.getLastName(), user.getGender(), user.getAge(), user.getOccupation(), user.getLocation(), user.getShortDescription(),
                user.getGenderHidden(), user.getProfilePicturePath(), user.getProfile_picture_name());

        StorageReference firebaseStoragePictureReference = storageRef.child("fightPanicProfilePictures/" + user.getProfile_picture_name());

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, user.getProfile_picture_name());

        firebaseStoragePictureReference.getFile(myPath).addOnSuccessListener(taskSnapshot -> {
            tempUser.setProfilePicturePathLocalStorage(myPath.toString());
            compositeDisposable2.add(userViewModel.insert(tempUser)
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribe(this::handleResponseInsertUser, this::handleErrorInsertUser));

        }).addOnFailureListener(exception -> {
            Log.d("TAG", "handleResponseGetProfile: " + exception.getLocalizedMessage());

            runOnUiThread(() -> {
                binding.LoginButton.setEnabled(true);
                binding.Register.setEnabled(true);
                binding.ForgotPassword.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.loginInUnsuccessfull), Snackbar.LENGTH_SHORT).show();
            });
        });
    }

    private void handleResponseInsertUser() {
        checkToContinue(tempUser.getId());
    }

    private void handleErrorInsertUser(Throwable throwable) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            binding.LoginButton.setEnabled(true);
            binding.Register.setEnabled(true);
            binding.ForgotPassword.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        });
    }

    private void checkToContinue(String id) {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (!registrationOrLoginOrSkipMenu) {
            SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
            sharedPrefsEditor.putBoolean(REGISTRATION_OR_LOGIN_OR_SKIP_MENU, true);
            sharedPrefsEditor.apply();
            deleteData();
        }

        SharedPreferences sharedPreferencesEditLoginInformation = getSharedPreferences(LOGIN_INFORMATION, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesEditLoginInformation.edit();
        editor.putString(LOGGED_USER_ID, id);
        editor.apply();

        SharedPreferences sharedPreferencesUserLoggedIn = getSharedPreferences(IS_LOGGED_IN_DATA, MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesUserLoggedInEditor = sharedPreferencesUserLoggedIn.edit();
        sharedPreferencesUserLoggedInEditor.putBoolean(IS_LOGGED_IN_STATE, true);
        sharedPreferencesUserLoggedInEditor.apply();

        new Handler(Looper.getMainLooper()).post(() -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.logInSuccessfully), Toast.LENGTH_LONG).show();
        });

        Intent intent = new Intent(ProfileRegisterOrLogin.this, MainMenu.class);
        startActivity(intent);
        finishAffinity();
        finish();
    }

    public void deleteData() {
        SharedPreferences sharedPreferencesEditTextInformation = getSharedPreferences(EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        sharedPreferencesEditTextInformation.edit().clear().apply();

        SharedPreferences sharedPreferencesEditTextUserInformation = getSharedPreferences(REGISTER_USER_INFORMATION_EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        sharedPreferencesEditTextUserInformation.edit().clear().apply();
    }

    @Override
    public void onPasswordReset(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.ProfileLoginPasswordEditText.setCursorVisible(false);
        binding.ProfileLoginEmailEditText.setText("");
        compositeDisposable.dispose();
        compositeDisposable2.dispose();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent a;
            //Intent sa kojim je pozvana aktivnost
            Intent i = getIntent();
            if (registrationOrLoginOrSkipMenu) {
                if (i.getBooleanExtra("GoBackFightClub", false)) {
                    onBackPressed();
                    return true;
                }
                a = new Intent(ProfileRegisterOrLogin.this, MainMenu.class);
            } else {
                a = new Intent(ProfileRegisterOrLogin.this, RegistrationOrLoginOrSkip.class);
            }
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackBarMessage() {

        if (findViewById(android.R.id.content) != null) {

            Snackbar.make(findViewById(android.R.id.content), R.string.noInternetConnection, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showSnackBarMessage(String message) {

        if (findViewById(android.R.id.content) != null) {

            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setError() {

        binding.ProfileLoginEmailTextInputLayout.setError(null);
        binding.ProfileLoginPasswordTextInputLayout.setError(null);
    }
}
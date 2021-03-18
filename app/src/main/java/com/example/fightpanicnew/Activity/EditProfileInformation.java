package com.example.fightpanicnew.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.fightpanicnew.Entity.User;
import com.example.fightpanicnew.Models.Response;
import com.example.fightpanicnew.R;
import com.example.fightpanicnew.ViewModels.UserViewModel;
import com.example.fightpanicnew.databinding.ActivityEditProfileInformationBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.example.fightpanicnew.Constants.DARK_MODE_SWITCH;
import static com.example.fightpanicnew.Constants.DARK_MODE_VALUE;
import static com.example.fightpanicnew.Constants.LOGGED_USER_ID;
import static com.example.fightpanicnew.Constants.LOGIN_INFORMATION;
import static com.example.fightpanicnew.HelperClasses.Validation.validateFields;
import static com.example.fightpanicnew.Network.RetrofitBuilder.getRetrofit;

public class EditProfileInformation extends AppCompatActivity {

    private ActivityEditProfileInformationBinding binding;
    private CompositeDisposable compositeDisposable;
    private io.reactivex.rxjava3.disposables.CompositeDisposable compositeDisposable2;
    private UserViewModel userViewModel;
    private String loggedUserID;
    private User entityUser;
    private String temp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileInformationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        DARK_MODE_VALUE = sharedPref.getBoolean(DARK_MODE_SWITCH, false);

        if (!DARK_MODE_VALUE) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        Toolbar toolbar = findViewById(R.id.EditProfileAppBar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.editProfileInformation));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel);

        compositeDisposable = new CompositeDisposable();
        compositeDisposable2 = new io.reactivex.rxjava3.disposables.CompositeDisposable();

        binding.EditProfileInformationProgressBar.setVisibility(View.GONE);

        binding.EditAgeEditText.setOnFocusChangeListener((View.OnFocusChangeListener) (view1, b) -> {

            if (b) {
                temp = binding.EditAgeEditText.getText().toString();
                binding.EditAgeEditText.setText("");
            } else {

                if (binding.EditAgeEditText.getText() == null || !validateFields(binding.EditAgeEditText.getText().toString())) {
                    temp = getString(R.string.privateForNow);
                } else {
                    temp = binding.EditAgeEditText.getText().toString();
                }
                binding.EditAgeEditText.setText(temp);
            }
        });

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        SharedPreferences sharedPreferencesEditLoginInformation = getSharedPreferences(LOGIN_INFORMATION, MODE_PRIVATE);
        loggedUserID = sharedPreferencesEditLoginInformation.getString(LOGGED_USER_ID, "");

        compositeDisposable.add(userViewModel.getUser(loggedUserID)
                .observeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribe(this::handleResponseGetUser, this::handleErrorGetUser));
    }

    private void handleResponseGetUser(User user) {

        entityUser = user;

        new Handler(Looper.getMainLooper()).post(() -> {

            if (user.getGenderHidden().equals("Male")) {
                binding.editProfileRadioGroup.check(R.id.editProfileRadioButtonMale);
            } else if (user.getGenderHidden().equals("Female")) {
                binding.editProfileRadioGroup.check(R.id.editProfileRadioButtonFemale);

            } else {
                binding.editProfileRadioGroup.check(R.id.editProfileRadioButtonUnknown);

            }
            binding.EditFirstNameEditText.setText(user.getFirstName());
            binding.EditLastNameEditText.setText(user.getLastName());
            binding.EditAgeEditText.setText(user.getAge());
            binding.EditOccupationEditText.setText(user.getOccupation());
            binding.EditLocationEditText.setText(user.getLocation());
            binding.EditShortDescriptionEditText.setText(user.getShortDescription());
        });
    }

    private void handleErrorGetUser(Throwable throwable) {
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.errorInLoadingProfileData), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.editProfileSaveIcon) {

            com.example.fightpanicnew.Models.User user = new com.example.fightpanicnew.Models.User();
            user.set_id(loggedUserID);

            if (binding.EditFirstNameEditText.getText() == null || !validateFields(binding.EditFirstNameEditText.getText().toString())) {
                user.setFirstName(getResources().getString(R.string.privateForNow));
                entityUser.setFirstName(getResources().getString(R.string.privateForNow));
            } else {
                user.setFirstName(binding.EditFirstNameEditText.getText().toString());
                entityUser.setFirstName(binding.EditFirstNameEditText.getText().toString());
            }

            if (binding.EditLastNameEditText.getText() == null || !validateFields(binding.EditLastNameEditText.getText().toString())) {
                user.setLastName(getResources().getString(R.string.privateForNow));
                entityUser.setLastName(getResources().getString(R.string.privateForNow));
            } else {
                user.setLastName(binding.EditLastNameEditText.getText().toString());
                entityUser.setLastName(binding.EditLastNameEditText.getText().toString());
            }

            if (binding.editProfileRadioGroup.getCheckedRadioButtonId() == R.id.editProfileRadioButtonMale) {
                user.setGender(getResources().getString(R.string.genderNameMale));
                user.setGenderHidden("Male");
                entityUser.setGender(getResources().getString(R.string.genderNameMale));
                entityUser.setGenderHidden("Male");
            } else if (binding.editProfileRadioGroup.getCheckedRadioButtonId() == R.id.editProfileRadioButtonFemale) {
                user.setGender(getResources().getString(R.string.genderNameFemale));
                user.setGenderHidden("Female");
                entityUser.setGender(getResources().getString(R.string.genderNameFemale));
                entityUser.setGenderHidden("Female");
            } else {
                user.setGender(getResources().getString(R.string.privateForNow));
                user.setGenderHidden("Unimportant");
                entityUser.setGender(getResources().getString(R.string.privateForNow));
                entityUser.setGenderHidden("Unimportant");
            }

            if (binding.EditAgeEditText.getText() == null || !validateFields(binding.EditAgeEditText.getText().toString())) {
                user.setAge(getResources().getString(R.string.privateForNow));
                entityUser.setAge(getResources().getString(R.string.privateForNow));
            } else if (binding.EditAgeEditText.getText().toString().equals(getString(R.string.privateForNow))) {
                user.setAge(getResources().getString(R.string.privateForNow));
                entityUser.setAge(getResources().getString(R.string.privateForNow));
            } else {
                if (Integer.parseInt(binding.EditAgeEditText.getText().toString()) > 99 || Integer.parseInt(binding.EditAgeEditText.getText().toString()) < 12) {
                    binding.EditAgeTextInputLayout.setError(getString(R.string.registrationAgeNotInConstraint));
                    return true;
                }
                user.setAge(binding.EditAgeEditText.getText().toString());
                entityUser.setAge(binding.EditAgeEditText.getText().toString());
            }

            if (binding.EditLocationEditText.getText() == null || !validateFields(binding.EditLocationEditText.getText().toString())) {
                user.setLocation(getResources().getString(R.string.privateForNow));
                entityUser.setLocation(getResources().getString(R.string.privateForNow));
            } else {
                user.setLocation(binding.EditLocationEditText.getText().toString());
                entityUser.setLocation(binding.EditLocationEditText.getText().toString());
            }

            if (binding.EditOccupationEditText.getText() == null || !validateFields(binding.EditOccupationEditText.getText().toString())) {
                user.setLocation(getResources().getString(R.string.privateForNow));
                entityUser.setOccupation(getResources().getString(R.string.privateForNow));
            } else {
                user.setOccupation(binding.EditOccupationEditText.getText().toString());
                entityUser.setOccupation(binding.EditOccupationEditText.getText().toString());
            }

            if (binding.EditShortDescriptionEditText.getText() == null || !validateFields(binding.EditShortDescriptionEditText.getText().toString())) {
                user.setShortDescription(getResources().getString(R.string.privateForNow));
                entityUser.setShortDescription(getResources().getString(R.string.privateForNow));
            } else {
                user.setShortDescription(binding.EditShortDescriptionEditText.getText().toString());
                entityUser.setShortDescription(binding.EditShortDescriptionEditText.getText().toString());
            }

            binding.EditProfileInformationProgressBar.setVisibility(View.VISIBLE);

            compositeDisposable2.add(getRetrofit().updateProfileInformation(user)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::updateProfileInformationSuccess, this::updateProfileInformationFailure));
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateProfileInformationSuccess(Response response) {
        compositeDisposable.add(userViewModel.update(entityUser)
                .observeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribe(this::updateProfileInformationDeviceCacheSuccess, this::updateProfileInformationDeviceCacheFailure));
    }

    private void updateProfileInformationFailure(Throwable throwable) {
        new Handler(Looper.getMainLooper()).post(() -> {
            binding.EditProfileInformationProgressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), getString(R.string.editProfileFailure), Toast.LENGTH_LONG).show();
            Log.e("TAG", "updateProfileInformationFailure: " + throwable.getMessage());
        });
    }

    private void updateProfileInformationDeviceCacheSuccess() {
        new Handler(Looper.getMainLooper()).post(() -> {
            binding.EditProfileInformationProgressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), getString(R.string.editProfileSuccess), Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        });
    }

    private void updateProfileInformationDeviceCacheFailure(Throwable throwable) {
        new Handler(Looper.getMainLooper()).post(() -> {
            binding.EditProfileInformationProgressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), getString(R.string.editProfileFailure), Toast.LENGTH_LONG).show();
            Log.e("TAG", "updateProfileInformationFailure: " + throwable.getMessage());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
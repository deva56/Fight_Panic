/*
Završni dio registracije. Korisnik prije finalizacije može promijeniti profilnu sliku, ako ju ne promijeni dodijeljen mu je default
avatar.
*/

package com.example.fightpanicnew.Activity.Registration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fightpanicnew.Activity.MainMenu;
import com.example.fightpanicnew.FightPanic;
import com.example.fightpanicnew.Models.Response;
import com.example.fightpanicnew.Models.User;
import com.example.fightpanicnew.R;
import com.example.fightpanicnew.databinding.ActivityRegistrationFinishBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.example.fightpanicnew.Constants.DARK_MODE_SWITCH;
import static com.example.fightpanicnew.Constants.DARK_MODE_VALUE;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_EMAIL;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_FIRST_NAME;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_GENDER_HIDDEN;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_GENDER_TEXT;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_INFORMATION;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_LAST_NAME;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_LOCATION;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_OCCUPATION;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_PASSWORD;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_SHORT_DESCRIPTION;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_USERNAME;
import static com.example.fightpanicnew.Constants.NUMBER_PICKER_AGE;
import static com.example.fightpanicnew.Constants.REGISTER_USER_INFORMATION_EDIT_TEXT_FIELDS_INFORMATION;
import static com.example.fightpanicnew.Constants.REGISTRATION_OR_LOGIN_OR_SKIP_MENU;
import static com.example.fightpanicnew.Constants.SHARED_PREFS;
import static com.example.fightpanicnew.HelperClasses.ImageManipulation.getBitmapFromDrawable;
import static com.example.fightpanicnew.HelperClasses.ImageManipulation.getBytesFromBitmap;
import static com.example.fightpanicnew.HelperClasses.Validation.validateField;
import static com.example.fightpanicnew.Network.RetrofitBuilder.getRetrofit;

public class RegistrationFinish extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;

    private String userName;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String gender;
    private String genderHidden;
    private String age;
    private String occupation;
    private String location;
    private String shortDescription;

    private CompositeDisposable compositeDisposable;
    private ActivityRegistrationFinishBinding binding;

    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationFinishBinding.inflate(getLayoutInflater());
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
        binding.RegistrationFinishChangeProfilePictureProgressBar.setVisibility(View.GONE);

        loadRegistrationBasicData();
        loadUserRegistrationDetailsInformation();

        binding.RegisterUserInformationProfilePictureChangeProfilePictureButton.setOnClickListener(view12 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });

        binding.RegisterUserInformationProfilePictureFinishButton.setOnClickListener(view13 -> {
            finishRegistration();
        });

        Toolbar toolbar = findViewById(R.id.RegistrationFinishAppBar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.registrationFinish));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent a = new Intent(RegistrationFinish.this, RegistrationUserDetails.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && data != null) {

            binding.RegistrationFinishChangeProfilePictureProgressBar.setVisibility(View.VISIBLE);
            binding.RegisterUserInformationProfilePictureImageView.setVisibility(View.GONE);
            binding.RegisterUserInformationProfilePictureFinishButton.setEnabled(false);
            binding.RegisterUserInformationProfilePictureChangeProfilePictureButton.setEnabled(false);

            Uri uri = data.getData();
            assert uri != null;

            Glide.with(this).load(uri)
                    .centerCrop().placeholder(R.drawable.ic_person)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    binding.RegistrationFinishChangeProfilePictureProgressBar.setVisibility(View.GONE);
                    binding.RegisterUserInformationProfilePictureImageView.setVisibility(View.VISIBLE);
                    binding.RegisterUserInformationProfilePictureFinishButton.setEnabled(true);
                    binding.RegisterUserInformationProfilePictureChangeProfilePictureButton.setEnabled(true);
                    Toast.makeText(getApplicationContext(), getString(R.string.errorInLoadingPicture), Toast.LENGTH_SHORT).show();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    binding.RegistrationFinishChangeProfilePictureProgressBar.setVisibility(View.GONE);
                    binding.RegisterUserInformationProfilePictureImageView.setVisibility(View.VISIBLE);
                    binding.RegisterUserInformationProfilePictureFinishButton.setEnabled(true);
                    binding.RegisterUserInformationProfilePictureChangeProfilePictureButton.setEnabled(true);
                    return false;
                }
            }).into(binding.RegisterUserInformationProfilePictureImageView);
        }
    }

    private void finishRegistration() {

        Executor executor = ((FightPanic) this.getApplication()).getExecutor();

        executor.execute(() -> {
            if (firstName == null || !validateField(firstName)) {
                firstName = getResources().getString(R.string.privateForNow);
            }
            if (lastName == null || !validateField(lastName)) {
                lastName = getResources().getString(R.string.privateForNow);
            }
            if (age == null || !validateField(age)) {
                age = getResources().getString(R.string.privateForNow);
            }
            if (location == null || !validateField(location)) {
                location = getResources().getString(R.string.privateForNow);
            }
            if (occupation == null || !validateField(occupation)) {
                occupation = getResources().getString(R.string.privateForNow);
            }
            if (gender == null) {
                gender = getResources().getString(R.string.privateForNow);
            }
            if (shortDescription == null || !validateField(shortDescription)) {
                shortDescription = getResources().getString(R.string.nothingAboutMeYet);
            }

            User user = new User();
            user.setUsername(userName);
            user.setEmail(email);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setGender(gender);
            user.setGenderHidden(genderHidden);
            user.setAge(age);
            user.setOccupation(occupation);
            user.setLocation(location);
            user.setShortDescription(shortDescription);
            user.setProfileImage(getBytesFromBitmap(getBitmapFromDrawable(binding.RegisterUserInformationProfilePictureImageView.getDrawable(), getApplicationContext())));

            runOnUiThread(() -> {
                binding.RegisterUserInformationProfilePictureProgressBar.setVisibility(View.VISIBLE);
                binding.RegisterUserInformationProfilePictureFinishButton.setEnabled(false);
                binding.RegisterUserInformationProfilePictureChangeProfilePictureButton.setEnabled(false);
            });

            registerProcess(user);
        });
    }

    private void registerProcess(User user) {

        String pictureName = user.getUsername() + "profilePicture";

        byte[] pictureNameByte = new byte[0];
        try {
            pictureNameByte = pictureName.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String base64EncodedPictureName = android.util.Base64.encodeToString(pictureNameByte, Base64.NO_WRAP);
        String random = UUID.randomUUID().toString();
        String tmp = android.util.Base64.encodeToString(random.getBytes(), Base64.NO_WRAP);
        String finalPictureName = base64EncodedPictureName + tmp;

        String firebaseStoragePicturePath = "fightPanicProfilePictures/" + finalPictureName + ".jpg";
        StorageReference firebaseStoragePictureReference = firebaseStorage.getReference(firebaseStoragePicturePath);

        UploadTask uploadTask = firebaseStoragePictureReference.putBytes(user.getProfileImage());

        Task<Uri> getProfilePictureUri = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return firebaseStoragePictureReference.getDownloadUrl();
        });

        getProfilePictureUri.addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                user.setProfilePicturePath(task.getResult().toString());
                user.setProfile_picture_name(finalPictureName + ".jpg");
                registerUser(user);
            }
        });

    }

    private void registerUser(User user) {

        compositeDisposable.add(getRetrofit().register(user)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUser, this::handleError));
    }

    private void handleResponseUser(Response response) {
        checkToFinish();
    }

    private void handleError(Throwable error) {

        runOnUiThread(() -> {
            binding.RegisterUserInformationProfilePictureFinishButton.setEnabled(true);
            binding.RegisterUserInformationProfilePictureChangeProfilePictureButton.setEnabled(true);
            binding.RegisterUserInformationProfilePictureProgressBar.setVisibility(View.GONE);

            if (!error.getMessage().equals("Attempt to invoke virtual method 'boolean java.lang.String.equals(java.lang.Object)' on a null object reference")) {
                if (error instanceof HttpException) {

                    Gson gson = new GsonBuilder().create();

                    try {

                        String errorBody = ((HttpException) error).response().errorBody().string();
                        Response response = gson.fromJson(errorBody, Response.class);
                        showSnackBarMessage(response.getMessage());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    showSnackBarMessage(getString(R.string.noInternetConnection));
                }
            }
        });
    }

    private void checkToFinish() {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(REGISTRATION_OR_LOGIN_OR_SKIP_MENU, true);
        editor.apply();

        SharedPreferences userInformationPreferences = getSharedPreferences(REGISTER_USER_INFORMATION_EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        userInformationPreferences.edit().clear().apply();

        SharedPreferences userRegistrationPreferences = getSharedPreferences(EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        userRegistrationPreferences.edit().clear().apply();

        new Handler(Looper.getMainLooper()).post(() -> {
            binding.RegisterUserInformationProfilePictureProgressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), getString(R.string.registrationSuccessful), Toast.LENGTH_LONG).show();
        });

        Intent a = new Intent(RegistrationFinish.this, MainMenu.class);
        startActivity(a);
        finishAffinity();
        finish();
    }

    private void showSnackBarMessage(String message) {

        if (findViewById(android.R.id.content) != null) {

            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void loadRegistrationBasicData() {
        SharedPreferences sharedPreferencesEditTextInformation = getSharedPreferences(EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        userName = sharedPreferencesEditTextInformation.getString(EDIT_TEXT_FIELDS_USERNAME, "");
        email = sharedPreferencesEditTextInformation.getString(EDIT_TEXT_FIELDS_EMAIL, "");
        password = sharedPreferencesEditTextInformation.getString(EDIT_TEXT_FIELDS_PASSWORD, "");
    }

    private void loadUserRegistrationDetailsInformation() {
        SharedPreferences sharedPreferencesUserRegistrationInformation = getSharedPreferences(REGISTER_USER_INFORMATION_EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);

        shortDescription = sharedPreferencesUserRegistrationInformation.getString(EDIT_TEXT_FIELDS_SHORT_DESCRIPTION, getResources().getString(R.string.privateForNow));
        gender = sharedPreferencesUserRegistrationInformation.getString(EDIT_TEXT_FIELDS_GENDER_TEXT, getResources().getString(R.string.privateForNow));
        age = String.valueOf(sharedPreferencesUserRegistrationInformation.getString(NUMBER_PICKER_AGE, getResources().getString(R.string.privateForNow)));
        occupation = sharedPreferencesUserRegistrationInformation.getString(EDIT_TEXT_FIELDS_OCCUPATION, getResources().getString(R.string.privateForNow));
        location = sharedPreferencesUserRegistrationInformation.getString(EDIT_TEXT_FIELDS_LOCATION, getResources().getString(R.string.privateForNow));
        firstName = sharedPreferencesUserRegistrationInformation.getString(EDIT_TEXT_FIELDS_FIRST_NAME, getResources().getString(R.string.privateForNow));
        lastName = sharedPreferencesUserRegistrationInformation.getString(EDIT_TEXT_FIELDS_LAST_NAME, getResources().getString(R.string.nothingAboutMeYet));
        genderHidden = sharedPreferencesUserRegistrationInformation.getString(EDIT_TEXT_FIELDS_GENDER_HIDDEN, "Unknown");

        assert genderHidden != null;
        if (genderHidden.equals("Male")) {
            final TypedArray maleImages = getResources().obtainTypedArray(R.array.maleProfileDefaultAvatars);
            final Random rand = new Random();
            final int rndInt = rand.nextInt(maleImages.length());
            final int resID = maleImages.getResourceId(rndInt, 0);
            maleImages.recycle();
            binding.RegisterUserInformationProfilePictureImageView.setImageResource(resID);
        } else if (genderHidden.equals("Female")) {
            final TypedArray femaleImages = getResources().obtainTypedArray(R.array.femaleProfileDefaultAvatars);
            final Random rand = new Random();
            final int rndInt = rand.nextInt(femaleImages.length());
            final int resID = femaleImages.getResourceId(rndInt, 0);
            femaleImages.recycle();
            binding.RegisterUserInformationProfilePictureImageView.setImageResource(resID);
        } else {
            final TypedArray noGenderImages = getResources().obtainTypedArray(R.array.noGenderProfileDefaultAvatars);
            final Random rand = new Random();
            final int rndInt = rand.nextInt(noGenderImages.length());
            final int resID = noGenderImages.getResourceId(rndInt, 0);
            noGenderImages.recycle();
            binding.RegisterUserInformationProfilePictureImageView.setImageResource(resID);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}

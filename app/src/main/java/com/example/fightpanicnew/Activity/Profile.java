/*
Aktivnost koja prikazuje profil svakog korisnika. Nudi pregled informacija o korisniku uključujući njegovi profilnu
sliku. Nudi mogućnost mijenjanja profilne slike te podataka korisničkom računa. Kroz tipku na toolbaru korisnik se
može izlogirati iz računa.
*/

package com.example.fightpanicnew.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fightpanicnew.Entity.User;
import com.example.fightpanicnew.FightPanic;
import com.example.fightpanicnew.Fragments.ChangePasswordFragment;
import com.example.fightpanicnew.HelperClasses.ImageManipulation;
import com.example.fightpanicnew.R;
import com.example.fightpanicnew.ViewModels.UserViewModel;
import com.example.fightpanicnew.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.example.fightpanicnew.Constants.DARK_MODE_SWITCH;
import static com.example.fightpanicnew.Constants.DARK_MODE_VALUE;
import static com.example.fightpanicnew.Constants.IS_LOGGED_IN_DATA;
import static com.example.fightpanicnew.Constants.IS_LOGGED_IN_STATE;
import static com.example.fightpanicnew.Constants.LOGGED_USER_ID;
import static com.example.fightpanicnew.Constants.LOGIN_EMAIL;
import static com.example.fightpanicnew.Constants.LOGIN_INFORMATION;
import static com.example.fightpanicnew.Constants.LOGIN_TOKEN;
import static com.example.fightpanicnew.HelperClasses.ImageManipulation.getBitmapFromDrawable;
import static com.example.fightpanicnew.HelperClasses.ImageManipulation.getBytesFromBitmap;
import static com.example.fightpanicnew.HelperClasses.ImageManipulation.loadImageFromStoragePicasso;
import static com.example.fightpanicnew.HelperClasses.ImageManipulation.saveToInternalStorageOverwrite;

public class Profile extends AppCompatActivity {

    public static final int EDIT_PROFILE_INFO = 1;
    public static final int PICK_IMAGE_PROFILE = 1;
    private ActivityProfileBinding binding;
    private CompositeDisposable compositeDisposable;
    private io.reactivex.rxjava3.disposables.CompositeDisposable compositeDisposable2;
    private UserViewModel userViewModel;
    private User loggedInUser;
    private String loggedUserID;
    private boolean isLoggedIn = true;

    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private Animator currentAnimator;
    private int shortAnimationDuration;

    private boolean zoomAble = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        DARK_MODE_VALUE = sharedPref.getBoolean(DARK_MODE_SWITCH, false);

        if (!DARK_MODE_VALUE) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        binding.ProfileActivityEditProfilePictureProgressBar.setVisibility(View.GONE);

        compositeDisposable = new CompositeDisposable();
        compositeDisposable2 = new io.reactivex.rxjava3.disposables.CompositeDisposable();

        Toolbar toolbar = findViewById(R.id.ProfileAppBar);
        this.setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        SharedPreferences sharedPreferencesEditLoginInformation = getSharedPreferences(LOGIN_INFORMATION, MODE_PRIVATE);
        loggedUserID = sharedPreferencesEditLoginInformation.getString(LOGGED_USER_ID, "");

        compositeDisposable.add(userViewModel.getUser(loggedUserID)
                .observeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribe(this::handleResponseGetUser, this::handleErrorGetUser));

        binding.ChangeProfilePictureButtonProfile.setOnClickListener(view13 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.profileChangePictureChooser)), PICK_IMAGE_PROFILE);
        });

        toolbar.setNavigationOnClickListener(view12 -> {
            binding.ProfileNavigationDrawerView.openDrawer(GravityCompat.START);
            TextView profileNavigationDrawerUserName = findViewById(R.id.ProfileNavigationDrawerUserName);
            profileNavigationDrawerUserName.setText(loggedInUser.getUsername());
            TextView profileNavigationDrawerUserEmail = findViewById(R.id.ProfileNavigationDrawerUserEmail);
            profileNavigationDrawerUserEmail.setText(loggedInUser.getEmail());
            loadImageFromStoragePicasso(loggedInUser.getProfilePicturePathLocalStorage(), findViewById(R.id.ProfileNavigationDrawerImageView));
        });


        binding.ProfileNavigationDrawerView.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_DRAGGING && !binding.ProfileNavigationDrawerView.isDrawerOpen(GravityCompat.START)) {
                    TextView profileNavigationDrawerUserName = findViewById(R.id.ProfileNavigationDrawerUserName);
                    profileNavigationDrawerUserName.setText(loggedInUser.getUsername());
                    TextView profileNavigationDrawerUserEmail = findViewById(R.id.ProfileNavigationDrawerUserEmail);
                    profileNavigationDrawerUserEmail.setText(loggedInUser.getEmail());
                    loadImageFromStoragePicasso(loggedInUser.getProfilePicturePathLocalStorage(), findViewById(R.id.ProfileNavigationDrawerImageView));
                }
            }
        });

        binding.ProfileActivityImageView.setOnClickListener(view1 -> {
                    if (zoomAble) {
                        Profile.this.zoomImageFromThumb(binding.ProfileActivityImageView, binding.ProfileActivityImageView.getDrawable());
                    }
                }
        );

        binding.ProfileDrawerNavigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.profileEditInformation) {
                if (isLoggedIn) {
                    Intent i = new Intent(getApplicationContext(), EditProfileInformation.class);
                    startActivityForResult(i, EDIT_PROFILE_INFO);
                }
                return true;
            } else if (item.getItemId() == R.id.profileChangePassword) {
                if (isLoggedIn) {
                    ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
                    changePasswordFragment.show(getSupportFragmentManager(), ChangePasswordFragment.TAG);
                }
                return true;
            } else if (item.getItemId() == R.id.profileLogoutButton) {
                if (isLoggedIn) logout();
                return true;
            } else if (item.getItemId() == R.id.profileGoBack) {
                if (isLoggedIn) onNavigateUp();
                return true;
            }
            return false;
        });

        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.profileHelpButton) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.profileHelp);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_PROFILE && data != null) {
            Uri uri = data.getData();

            zoomAble = false;
            binding.ProfileActivityImageView.setVisibility(View.GONE);
            binding.ProfileActivityEditProfilePictureProgressBar.setVisibility(View.VISIBLE);
            binding.ChangeProfilePictureButtonProfile.setEnabled(false);

            Glide.with(this).load(uri).centerCrop().placeholder(R.drawable.ic_person).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    binding.ProfileActivityImageView.setVisibility(View.VISIBLE);
                    binding.ProfileActivityEditProfilePictureProgressBar.setVisibility(View.GONE);
                    binding.ChangeProfilePictureButtonProfile.setEnabled(true);
                    zoomAble = true;
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.errorInUpdatingProfilePicture), Snackbar.LENGTH_SHORT).show();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    uploadProfilePictureOnServer(resource);
                    return false;
                }
            }).into(binding.ProfileActivityImageView);

        } else if (requestCode == EDIT_PROFILE_INFO && resultCode == RESULT_OK) {
            compositeDisposable.add(userViewModel.getUser(loggedUserID)
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribe(this::handleResponseGetUser, this::handleErrorGetUser));
        }
    }

    private void handleResponseGetUser(User user) {
        new Handler(Looper.getMainLooper()).post(() -> {
            loggedInUser = user;
            binding.UsernameTextViewProfileContent.setText(user.getUsername());
            binding.EmailTextViewProfileContent.setText(user.getEmail());
            binding.FirstNameTextViewProfileContent.setText(user.getFirstName());
            binding.LastNameTextViewProfileContent.setText(user.getLastName());
            binding.GenderTextViewProfileContent.setText(user.getGender());
            binding.AgeTextViewProfileContent.setText(user.getAge());
            binding.OccupationTextViewProfileContent.setText(user.getOccupation());
            binding.LocationTextViewProfileContent.setText(user.getLocation());
            binding.ShortDescriptionTextViewProfileContent.setText(user.getShortDescription());
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(user.getUsername());

            loadImageFromStoragePicasso(user.getProfilePicturePathLocalStorage(), binding.ProfileActivityImageView);
            zoomAble = true;
        });
    }

    private void handleErrorGetUser(Throwable throwable) {
        Log.e("GetProfileError", Arrays.toString(throwable.getStackTrace()));
    }

    private void uploadProfilePictureOnServer(Drawable drawable) {

        String deleteProfilePictureName = loggedInUser.getProfilePictureName();

        String firebaseStoragePicturePath = "fightPanicProfilePictures/" + deleteProfilePictureName;
        StorageReference firebaseStorageUploadPictureReference = firebaseStorage.getReference(firebaseStoragePicturePath);

        Executor executor = ((FightPanic) this.getApplication()).getExecutor();

        executor.execute(() -> {

            UploadTask uploadTask;
            uploadTask = firebaseStorageUploadPictureReference.putBytes(getBytesFromBitmap(Objects.requireNonNull(getBitmapFromDrawable(drawable, getApplicationContext()))));

            Task<Uri> getProfilePictureUri = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return firebaseStorageUploadPictureReference.getDownloadUrl();
            });

            getProfilePictureUri.addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    compositeDisposable2.add(Observable.just(saveToInternalStorageOverwrite(getBitmapFromDrawable(drawable, getApplicationContext()), getApplicationContext(), deleteProfilePictureName))
                            .observeOn(Schedulers.io())
                            .subscribeOn(Schedulers.io())
                            .subscribeWith(new DisposableObserver<Boolean>() {
                                @Override
                                public void onNext(@NonNull Boolean s) {
                                    runOnUiThread(() -> {
                                        ImageManipulation.loadImageFromStoragePicassoInvalidate(loggedInUser.getProfilePicturePathLocalStorage(), binding.ProfileActivityImageView);
                                        binding.ProfileActivityImageView.setVisibility(View.VISIBLE);
                                        binding.ProfileActivityEditProfilePictureProgressBar.setVisibility(View.GONE);
                                        binding.ChangeProfilePictureButtonProfile.setEnabled(true);
                                        zoomAble = true;
                                        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.profilePictureUpdated), Snackbar.LENGTH_SHORT).show();
                                    });
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    Log.e("Error", Objects.requireNonNull(e.getMessage()));
                                }

                                @Override
                                public void onComplete() {
                                }
                            }));
                }
            }).addOnFailureListener(exception -> new Handler(Looper.getMainLooper()).post(() -> {
                binding.ProfileActivityImageView.setVisibility(View.VISIBLE);
                binding.ProfileActivityEditProfilePictureProgressBar.setVisibility(View.GONE);
                binding.ChangeProfilePictureButtonProfile.setEnabled(true);
                zoomAble = true;
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.errorInUpdatingProfilePicture), Snackbar.LENGTH_SHORT).show();
                Log.e("Error", Objects.requireNonNull(exception.getMessage()));
            }));
        });
    }

    private void logout() {

        isLoggedIn = false;

        binding.ChangeProfilePictureButtonProfile.setEnabled(false);

        SharedPreferences sharedPreferencesUserLoggedIn = getSharedPreferences(IS_LOGGED_IN_DATA, MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesUserLoggedInEditor = sharedPreferencesUserLoggedIn.edit();
        sharedPreferencesUserLoggedInEditor.putBoolean(IS_LOGGED_IN_STATE, false);
        sharedPreferencesUserLoggedInEditor.apply();

        SharedPreferences sharedPreferencesUserLoggedInData = getSharedPreferences(LOGIN_INFORMATION, MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesUserLoggedInDataEditor = sharedPreferencesUserLoggedInData.edit();
        sharedPreferencesUserLoggedInDataEditor.putString(LOGIN_EMAIL, "");
        sharedPreferencesUserLoggedInDataEditor.putString(LOGIN_TOKEN, "");
        sharedPreferencesUserLoggedInDataEditor.putString(LOGGED_USER_ID, "");
        sharedPreferencesUserLoggedInDataEditor.apply();

        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.logOutSuccessfully), Toast.LENGTH_LONG).show();
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(Profile.this, MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        compositeDisposable2.dispose();
    }

    private void zoomImageFromThumb(View thumbView, Drawable drawable) {

        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        final ImageView expandedImageView = findViewById(
                R.id.ProfileActivityImageViewEnlarged);
        expandedImageView.setImageDrawable(drawable);
        expandedImageView.setBackgroundColor(getResources().getColor(R.color.black_overlay_on_start_animation));

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.ProfileNavigationDrawerView)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);


        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;


        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(view -> {

            expandedImageView.setBackgroundColor(getResources().getColor(R.color.black_overlay_on_end_animation));

            if (currentAnimator != null) {
                currentAnimator.cancel();
            }

            AnimatorSet set1 = new AnimatorSet();
            set1.play(ObjectAnimator
                    .ofFloat(expandedImageView, View.X, startBounds.left))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.Y, startBounds.top))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.SCALE_X, startScaleFinal))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.SCALE_Y, startScaleFinal));
            set1.setDuration(shortAnimationDuration);
            set1.setInterpolator(new DecelerateInterpolator());
            set1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    currentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    currentAnimator = null;
                }
            });
            set1.start();
            currentAnimator = set1;
        });
    }

}
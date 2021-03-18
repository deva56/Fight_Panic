package com.example.fightpanicnew.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fightpanicnew.Entity.PillDiaryRecord;
import com.example.fightpanicnew.R;
import com.example.fightpanicnew.RecyclerViewClasses.PillDiaryAdapter;
import com.example.fightpanicnew.ViewModels.PillDiaryViewModel;
import com.example.fightpanicnew.databinding.ActivityPillDiaryBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.fightpanicnew.Constants.DARK_MODE_SWITCH;
import static com.example.fightpanicnew.Constants.DARK_MODE_VALUE;
import static com.example.fightpanicnew.Constants.EXTRA_ATTACK_DATE;
import static com.example.fightpanicnew.Constants.EXTRA_ATTACK_TIME;
import static com.example.fightpanicnew.Constants.EXTRA_DESCRIPTION;
import static com.example.fightpanicnew.Constants.EXTRA_ID;
import static com.example.fightpanicnew.Constants.EXTRA_RECORD_CREATION_DATE;
import static com.example.fightpanicnew.Constants.EXTRA_SHOULD_DELETE;
import static com.example.fightpanicnew.Constants.EXTRA_TITLE;

public class PillDiary extends AppCompatActivity {

    private ActivityPillDiaryBinding binding;
    private PillDiaryViewModel pillDiaryViewModel;
    public static final int ADD_RECORD_REQUEST = 1;
    public static final int EDIT_RECORD_REQUEST = 2;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPillDiaryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        DARK_MODE_VALUE = sharedPref.getBoolean(DARK_MODE_SWITCH, false);

        if (!DARK_MODE_VALUE) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        Toolbar toolbar = findViewById(R.id.PillDiaryAppBar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.mainMenuPillDiary));

        RecyclerView recyclerView = binding.PillDiaryRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        PillDiaryAdapter adapter = new PillDiaryAdapter();
        recyclerView.setAdapter(adapter);

        pillDiaryViewModel = new ViewModelProvider(this).get(PillDiaryViewModel.class);
        pillDiaryViewModel.getAllRecords().observe(this, pillDiaryRecords -> {
            if (pillDiaryRecords.size() == 0) {
                binding.PillDiaryNoRecordYet.setVisibility(View.VISIBLE);
            } else {
                binding.PillDiaryNoRecordYet.setVisibility(View.INVISIBLE);
            }
            adapter.setRecords(pillDiaryRecords);
        });

        binding.AddPillRecordButton.setOnClickListener(view1 -> {
            Intent a = new Intent(PillDiary.this, AddOrEditPillDiaryRecord.class);
            startActivityForResult(a, ADD_RECORD_REQUEST);
        });

        adapter.setOnItemClickListener(pillDiaryRecord -> {
            Intent intent = new Intent(PillDiary.this, AddOrEditPillDiaryRecord.class);
            intent.putExtra(EXTRA_TITLE, pillDiaryRecord.getTitle());
            intent.putExtra(EXTRA_DESCRIPTION, pillDiaryRecord.getDescription());
            intent.putExtra(EXTRA_RECORD_CREATION_DATE, pillDiaryRecord.getDateOfCreation());
            intent.putExtra(EXTRA_ATTACK_DATE, pillDiaryRecord.getDateWhenTaken());
            intent.putExtra(EXTRA_ATTACK_TIME, pillDiaryRecord.getTimeWhenTaken());
            intent.putExtra(EXTRA_ID, pillDiaryRecord.getId());
            startActivityForResult(intent, EDIT_RECORD_REQUEST);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.panic_calendar_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.panicAttackInformationMainMenu) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.pillDiaryHelpWindowDescriptionText);
            builder.setPositiveButton(getString(R.string.gotIt), (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (item.getItemId() == R.id.panicAttackDeleteAllRecords) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.deleteAllFromDatabasePanicCalendar));
            builder.setPositiveButton(getString(R.string.doIt), (dialogInterface, i) -> {
                compositeDisposable.add(pillDiaryViewModel.deleteAllRecords()
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(this::onSuccessDeleteRecords, this::onFailureDeleteRecords));
            });
            builder.setNegativeButton(getString(R.string.goBack), (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean shouldDelete = false;
        if (data != null) {
            shouldDelete = data.getBooleanExtra(EXTRA_SHOULD_DELETE, false);
        }

        if (requestCode == ADD_RECORD_REQUEST && resultCode == RESULT_OK) {
            String title = data.getStringExtra(EXTRA_TITLE);
            String description = data.getStringExtra(EXTRA_DESCRIPTION);
            String date = data.getStringExtra(EXTRA_RECORD_CREATION_DATE);
            String dateOfTakingMedication = data.getStringExtra(EXTRA_ATTACK_DATE);
            String timeOfTakingMedication = data.getStringExtra(EXTRA_ATTACK_TIME);

            PillDiaryRecord pillDiaryRecord = new PillDiaryRecord(date, description, title, dateOfTakingMedication, timeOfTakingMedication);

            compositeDisposable.add(pillDiaryViewModel.insert(pillDiaryRecord)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(this::onSuccessAddRecord, this::onFailureAddRecord));
        } else if (requestCode == EDIT_RECORD_REQUEST && shouldDelete && resultCode == RESULT_OK) {
            int id = data.getIntExtra(EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(getApplicationContext(), getString(R.string.pillDiaryRecordCantBeDeleted), Toast.LENGTH_SHORT).show();
                return;
            }

            compositeDisposable.add(pillDiaryViewModel.delete(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(this::onSuccessDeleteRecord, this::onFailureDeleteRecord));
        } else if (requestCode == EDIT_RECORD_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(getApplicationContext(), getString(R.string.pillDiaryRecordCantBeUpdated), Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(EXTRA_TITLE);
            String description = data.getStringExtra(EXTRA_DESCRIPTION);
            String date = data.getStringExtra(EXTRA_RECORD_CREATION_DATE);
            String dateOfTakingMedication = data.getStringExtra(EXTRA_ATTACK_DATE);
            String timeOfTakingMedication = data.getStringExtra(EXTRA_ATTACK_TIME);

            PillDiaryRecord pillDiaryRecord = new PillDiaryRecord(date, description, title, dateOfTakingMedication, timeOfTakingMedication);
            pillDiaryRecord.setId(id);

            compositeDisposable.add(pillDiaryViewModel.update(pillDiaryRecord)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(this::onSuccessEditRecord, this::onFailureEditRecord));
        }
    }

    private void onSuccessAddRecord() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.pillDiaryRecordSaved), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void onFailureAddRecord(Throwable e) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.panicAttackErrorInSaving), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void onSuccessEditRecord() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.pillDiaryRecordUpdated), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void onFailureEditRecord(Throwable e) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.panicAttackErrorInUpdating), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void onSuccessDeleteRecord() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.pillDiaryRecordDeleted), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void onFailureDeleteRecord(Throwable e) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.errorInPanicAttackRecordDelete), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void onFailureDeleteRecords(Throwable throwable) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.pillDiaryRecordsCantDelete), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void onSuccessDeleteRecords(Integer integer) {
        if (integer > 0) {
            new Handler(Looper.getMainLooper()).post(() -> {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.pillDiaryRecordsDeleted), Snackbar.LENGTH_SHORT).show();
            });
        } else {
            new Handler(Looper.getMainLooper()).post(() -> {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.noPillDiaryRecordToDelete), Snackbar.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
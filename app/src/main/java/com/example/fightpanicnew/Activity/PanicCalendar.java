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

import com.example.fightpanicnew.Entity.PanicAttackRecord;
import com.example.fightpanicnew.R;
import com.example.fightpanicnew.RecyclerViewClasses.PanicCalendarAdapter;
import com.example.fightpanicnew.ViewModels.PanicCalendarViewModel;
import com.example.fightpanicnew.databinding.ActivityPanicCalendarBinding;
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
import static com.example.fightpanicnew.Constants.EXTRA_STRENGTH;
import static com.example.fightpanicnew.Constants.EXTRA_TITLE;

public class PanicCalendar extends AppCompatActivity {

    private ActivityPanicCalendarBinding binding;
    private PanicCalendarViewModel panicCalendarViewModel;
    public static final int ADD_RECORD_REQUEST = 1;
    public static final int EDIT_RECORD_REQUEST = 2;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPanicCalendarBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        DARK_MODE_VALUE = sharedPref.getBoolean(DARK_MODE_SWITCH, false);

        if (!DARK_MODE_VALUE) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        Toolbar toolbar = findViewById(R.id.PanicCalendarAppBar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.mainMenuPanicCalendar));

        RecyclerView recyclerView = binding.PanicCalendarRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        PanicCalendarAdapter adapter = new PanicCalendarAdapter();
        recyclerView.setAdapter(adapter);

        panicCalendarViewModel = new ViewModelProvider(this).get(PanicCalendarViewModel.class);
        panicCalendarViewModel.getAllRecords().observe(this, panicAttackRecords -> {
            if (panicAttackRecords.size() == 0) {
                binding.PanicCalendarNoRecordYet.setVisibility(View.VISIBLE);
            } else {
                binding.PanicCalendarNoRecordYet.setVisibility(View.INVISIBLE);
            }
            adapter.setRecords(panicAttackRecords);
        });

        binding.AddPanicRecordButton.setOnClickListener(view1 -> {
            Intent a = new Intent(PanicCalendar.this, AddOrEditPanicAttackRecord.class);
            startActivityForResult(a, ADD_RECORD_REQUEST);
        });

        adapter.setOnItemClickListener(panicAttackRecord -> {
            Intent intent = new Intent(PanicCalendar.this, AddOrEditPanicAttackRecord.class);
            intent.putExtra(EXTRA_TITLE, panicAttackRecord.getTitle());
            intent.putExtra(EXTRA_DESCRIPTION, panicAttackRecord.getDescription());
            intent.putExtra(EXTRA_RECORD_CREATION_DATE, panicAttackRecord.getDateOfCreation());
            intent.putExtra(EXTRA_STRENGTH, panicAttackRecord.getAttackStrength());
            intent.putExtra(EXTRA_ATTACK_DATE, panicAttackRecord.getAttackDate());
            intent.putExtra(EXTRA_ATTACK_TIME, panicAttackRecord.getAttackTime());
            intent.putExtra(EXTRA_ID, panicAttackRecord.getId());
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
            builder.setMessage(R.string.panicCalendarHelpWindowDescriptionText);
            builder.setPositiveButton(getString(R.string.gotIt), (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (item.getItemId() == R.id.panicAttackDeleteAllRecords) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.deleteAllFromDatabasePanicCalendar));
            builder.setPositiveButton(getString(R.string.doIt), (dialogInterface, i) -> {
                compositeDisposable.add(panicCalendarViewModel.deleteAllRecords()
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
            assert data != null;
            String title = data.getStringExtra(EXTRA_TITLE);
            String description = data.getStringExtra(EXTRA_DESCRIPTION);
            int attackStrength = data.getIntExtra(EXTRA_STRENGTH, 1);
            String date = data.getStringExtra(EXTRA_RECORD_CREATION_DATE);
            String attackDate = data.getStringExtra(EXTRA_ATTACK_DATE);
            String attackTime = data.getStringExtra(EXTRA_ATTACK_TIME);

            PanicAttackRecord panicAttackRecord = new PanicAttackRecord(attackStrength, date, description, title, attackDate, attackTime);

            compositeDisposable.add(panicCalendarViewModel.insert(panicAttackRecord)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(this::onSuccessAddRecord, this::onFailureAddRecord));

        } else if (requestCode == EDIT_RECORD_REQUEST && shouldDelete && resultCode == RESULT_OK) {
            int id = data.getIntExtra(EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(getApplicationContext(), getString(R.string.panicAttackRecordCantBeDeleted), Toast.LENGTH_SHORT).show();
                return;
            }

            compositeDisposable.add(panicCalendarViewModel.delete(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(this::onSuccessDeleteRecord, this::onFailureDeleteRecord));
        } else if (requestCode == EDIT_RECORD_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(getApplicationContext(), getString(R.string.panicAttackRecordCantBeUpdated), Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(EXTRA_TITLE);
            String description = data.getStringExtra(EXTRA_DESCRIPTION);
            int attackStrength = data.getIntExtra(EXTRA_STRENGTH, 1);
            String date = data.getStringExtra(EXTRA_RECORD_CREATION_DATE);
            String attackDate = data.getStringExtra(EXTRA_ATTACK_DATE);
            String attackTime = data.getStringExtra(EXTRA_ATTACK_TIME);

            PanicAttackRecord panicAttackRecord = new PanicAttackRecord(attackStrength, date, description, title, attackDate, attackTime);
            panicAttackRecord.setId(id);

            compositeDisposable.add(panicCalendarViewModel.update(panicAttackRecord)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(this::onSuccessEditRecord, this::onFailureEditRecord));
        }
    }

    private void onSuccessAddRecord() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.panicAttackRecordSaved), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void onFailureAddRecord(Throwable e) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.panicAttackErrorInSaving), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void onSuccessEditRecord() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.panicAttackRecordUpdated), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void onFailureEditRecord(Throwable e) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.panicAttackErrorInUpdating), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void onSuccessDeleteRecord() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.panicAttackRecordDeleted), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void onFailureDeleteRecord(Throwable e) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.errorInPanicAttackRecordDelete), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void onFailureDeleteRecords(Throwable throwable) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.panicAttackRecordsCantDelete), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void onSuccessDeleteRecords(Integer integer) {
        if (integer > 0) {
            new Handler(Looper.getMainLooper()).post(() -> {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.panicAttackRecordsDeleted), Snackbar.LENGTH_SHORT).show();
            });
        } else {
            new Handler(Looper.getMainLooper()).post(() -> {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.noPanicAttackRecordToDelete), Snackbar.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
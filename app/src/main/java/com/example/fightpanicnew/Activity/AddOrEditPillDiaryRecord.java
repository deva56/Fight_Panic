package com.example.fightpanicnew.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.example.fightpanicnew.Fragments.DateTimePickers;
import com.example.fightpanicnew.R;
import com.example.fightpanicnew.databinding.ActivityAddOrEditPillDiaryRecordBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import static com.example.fightpanicnew.Constants.DARK_MODE_SWITCH;
import static com.example.fightpanicnew.Constants.DARK_MODE_VALUE;
import static com.example.fightpanicnew.Constants.EXTRA_ATTACK_DATE;
import static com.example.fightpanicnew.Constants.EXTRA_ATTACK_TIME;
import static com.example.fightpanicnew.Constants.EXTRA_DESCRIPTION;
import static com.example.fightpanicnew.Constants.EXTRA_ID;
import static com.example.fightpanicnew.Constants.EXTRA_RECORD_CREATION_DATE;
import static com.example.fightpanicnew.Constants.EXTRA_SHOULD_DELETE;
import static com.example.fightpanicnew.Constants.EXTRA_TITLE;

public class AddOrEditPillDiaryRecord extends AppCompatActivity {

    private ActivityAddOrEditPillDiaryRecordBinding binding;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddOrEditPillDiaryRecordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        DARK_MODE_VALUE = sharedPref.getBoolean(DARK_MODE_SWITCH, false);

        if (!DARK_MODE_VALUE) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        Toolbar toolbar = findViewById(R.id.AddPillDiaryRecordAppBar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel);

        binding.AddPillDiaryRecordEditTextDate.setFocusable(false);
        binding.AddPillDiaryRecordEditTextDate.setClickable(false);
        binding.AddPillDiaryRecordEditTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DateTimePickers.DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        binding.AddPillDiaryRecordEditTextTime.setFocusable(false);
        binding.AddPillDiaryRecordEditTextTime.setClickable(false);
        binding.AddPillDiaryRecordEditTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DateTimePickers.TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            getSupportActionBar().setTitle(getString(R.string.editRecord));
            binding.AddPillDiaryRecordEditTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            binding.AddPillDiaryRecordEditTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            binding.AddPillDiaryRecordEditTextDate.setText(intent.getStringExtra(EXTRA_ATTACK_DATE));
            binding.AddPillDiaryRecordEditTextTime.setText(intent.getStringExtra(EXTRA_ATTACK_TIME));
            binding.AddOrEditPillDiaryCreationDateTextView.setText(intent.getStringExtra(EXTRA_RECORD_CREATION_DATE));
            binding.AddPillDiaryDeleteButton.setVisibility(View.VISIBLE);
            binding.AddOrEditPillDiaryHeadingTextView.setVisibility(View.VISIBLE);
            binding.AddOrEditPillDiaryCreationDateTextView.setVisibility(View.VISIBLE);

            binding.AddPillDiaryDeleteButton.setOnClickListener(view1 -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.deleteRecord));
                builder.setMessage(getString(R.string.confirmDeleteRecord));

                builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                    Intent data = new Intent();
                    int id = getIntent().getIntExtra(EXTRA_ID, -1);
                    if (id != -1) {
                        data.putExtra(EXTRA_ID, id);
                        data.putExtra(EXTRA_SHOULD_DELETE, true);
                    }

                    setResult(RESULT_OK, data);
                    finish();
                });
                builder.setNegativeButton(getString(R.string.no), (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            });
        } else {
            binding.AddOrEditPillDiaryHeadingTextView.setVisibility(View.GONE);
            binding.AddOrEditPillDiaryCreationDateTextView.setVisibility(View.GONE);
            binding.AddPillDiaryDeleteButton.setVisibility(View.GONE);
            getSupportActionBar().setTitle(getString(R.string.addNewRecord));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.panic_calendar_add_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onNavigateUp();
                return true;
            case R.id.confirmAddNewRecord:
                addNewRecord();
                return true;
            case R.id.panicAttackInformation:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.pillDiaryHelpWindowAddRecordDescriptionText);
                builder.setPositiveButton(getString(R.string.gotIt), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigateUp() {
        super.onNavigateUp();
        if ((!binding.AddPillDiaryRecordEditTextTitle.getText().toString().trim().isEmpty() ||
                !binding.AddPillDiaryRecordEditTextDescription.getText().toString().trim().isEmpty()) && !intent.hasExtra(EXTRA_ID)) {
            Toast.makeText(getApplicationContext(), getString(R.string.recordNotSaved), Toast.LENGTH_SHORT).show();
        } else if ((!binding.AddPillDiaryRecordEditTextTitle.getText().toString().trim().isEmpty() ||
                !binding.AddPillDiaryRecordEditTextDescription.getText().toString().trim().isEmpty()) && intent.hasExtra(EXTRA_ID)) {
            Toast.makeText(getApplicationContext(), getString(R.string.recordNotUpdated), Toast.LENGTH_SHORT).show();
        }

        finish();
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onNavigateUp();
        if ((!binding.AddPillDiaryRecordEditTextTitle.getText().toString().trim().isEmpty() ||
                !binding.AddPillDiaryRecordEditTextDescription.getText().toString().trim().isEmpty()) && !intent.hasExtra(EXTRA_ID)) {
            Toast.makeText(getApplicationContext(), getString(R.string.recordNotSaved), Toast.LENGTH_SHORT).show();
        } else if ((!binding.AddPillDiaryRecordEditTextTitle.getText().toString().trim().isEmpty() ||
                !binding.AddPillDiaryRecordEditTextDescription.getText().toString().trim().isEmpty()) && intent.hasExtra(EXTRA_ID)) {
            Toast.makeText(getApplicationContext(), getString(R.string.recordNotUpdated), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void setError() {
        binding.AddPillDiaryRecordEditTextTitle.setError(null);
    }

    private void addNewRecord() {
        String title;
        String description;
        String dateOfTakingMedication;
        String timeOfTakingMedication;

        setError();

        int error = 0;

        title = binding.AddPillDiaryRecordEditTextTitle.getText().toString();
        description = binding.AddPillDiaryRecordEditTextDescription.getText().toString();
        dateOfTakingMedication = binding.AddPillDiaryRecordEditTextDate.getText().toString();
        timeOfTakingMedication = binding.AddPillDiaryRecordEditTextTime.getText().toString();

        if (title.trim().isEmpty()) {
            error++;
            binding.AddPillDiaryRecordTextInputLayoutTitle.setError(getString(R.string.medicationCantBeEmpty));
        }

        if (error == 0) {
            String res = "";
            if (intent.hasExtra(EXTRA_ID)) {
                res = binding.AddOrEditPillDiaryCreationDateTextView.getText().toString();
            } else {
                Date presentTime_Date = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy|HH:mm:ss");
                dateFormat.setTimeZone(TimeZone.getDefault());
                res = dateFormat.format(presentTime_Date);
            }

            Intent data = new Intent();
            data.putExtra(EXTRA_TITLE, title);
            data.putExtra(EXTRA_DESCRIPTION, description);
            data.putExtra(EXTRA_RECORD_CREATION_DATE, res);
            data.putExtra(EXTRA_ATTACK_DATE, dateOfTakingMedication);
            data.putExtra(EXTRA_ATTACK_TIME, timeOfTakingMedication);

            int id = getIntent().getIntExtra(EXTRA_ID, -1);
            if (id != -1) {
                data.putExtra(EXTRA_ID, id);
            }

            setResult(RESULT_OK, data);
            finish();
        }
    }
}
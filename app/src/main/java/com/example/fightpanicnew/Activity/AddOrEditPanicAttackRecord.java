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
import com.example.fightpanicnew.databinding.ActivityAddOrEditPanicAttackRecordBinding;

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
import static com.example.fightpanicnew.Constants.EXTRA_STRENGTH;
import static com.example.fightpanicnew.Constants.EXTRA_TITLE;

public class AddOrEditPanicAttackRecord extends AppCompatActivity {

    private ActivityAddOrEditPanicAttackRecordBinding binding;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddOrEditPanicAttackRecordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        DARK_MODE_VALUE = sharedPref.getBoolean(DARK_MODE_SWITCH, false);

        if (!DARK_MODE_VALUE) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        Toolbar toolbar = findViewById(R.id.AddPanicAttackRecordAppBar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel);

        binding.AddPanicAttackRecordNumberPicker.setMinValue(1);
        binding.AddPanicAttackRecordNumberPicker.setMaxValue(10);

        binding.AddPanicAttackRecordEditTextDate.setFocusable(false);
        binding.AddPanicAttackRecordEditTextDate.setClickable(false);
        binding.AddPanicAttackRecordEditTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DateTimePickers.DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        binding.AddPanicAttackRecordEditTextTime.setFocusable(false);
        binding.AddPanicAttackRecordEditTextTime.setClickable(false);
        binding.AddPanicAttackRecordEditTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DateTimePickers.TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            getSupportActionBar().setTitle(getString(R.string.editRecord));
            binding.AddPanicAttackRecordEditTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            binding.AddPanicAttackRecordEditTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            binding.AddPanicAttackRecordNumberPicker.setValue(intent.getIntExtra(EXTRA_STRENGTH, 1));
            binding.AddPanicAttackRecordEditTextDate.setText(intent.getStringExtra(EXTRA_ATTACK_DATE));
            binding.AddPanicAttackRecordEditTextTime.setText(intent.getStringExtra(EXTRA_ATTACK_TIME));
            binding.AddOrEditPanicAttackCreationDateTextView.setText(intent.getStringExtra(EXTRA_RECORD_CREATION_DATE));
            binding.AddPanicAttackDeleteButton.setVisibility(View.VISIBLE);
            binding.AddOrEditPanicAttackCreationDateTextView.setVisibility(View.VISIBLE);
            binding.AddOrEditPanicAttackHeadingTextView.setVisibility(View.VISIBLE);

            binding.AddPanicAttackDeleteButton.setOnClickListener(view1 -> {
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
            binding.AddOrEditPanicAttackCreationDateTextView.setVisibility(View.GONE);
            binding.AddOrEditPanicAttackHeadingTextView.setVisibility(View.GONE);
            binding.AddPanicAttackDeleteButton.setVisibility(View.GONE);
            Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.addNewRecord));
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
                builder.setMessage(R.string.panicCalendarHelpWindowDescriptionText);
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
        if ((!binding.AddPanicAttackRecordEditTextTitle.getText().toString().trim().isEmpty() ||
                !binding.AddPanicAttackRecordEditTextDescription.getText().toString().trim().isEmpty()) && !intent.hasExtra(EXTRA_ID)) {
            Toast.makeText(getApplicationContext(), getString(R.string.recordNotSaved), Toast.LENGTH_SHORT).show();
        } else if ((!binding.AddPanicAttackRecordEditTextTitle.getText().toString().trim().isEmpty() ||
                !binding.AddPanicAttackRecordEditTextDescription.getText().toString().trim().isEmpty()) && intent.hasExtra(EXTRA_ID)) {
            Toast.makeText(getApplicationContext(), getString(R.string.recordNotUpdated), Toast.LENGTH_SHORT).show();
        }

        finish();
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onNavigateUp();
        if ((!binding.AddPanicAttackRecordEditTextTitle.getText().toString().trim().isEmpty() ||
                !binding.AddPanicAttackRecordEditTextDescription.getText().toString().trim().isEmpty()) && !intent.hasExtra(EXTRA_ID)) {
            Toast.makeText(getApplicationContext(), getString(R.string.recordNotSaved), Toast.LENGTH_SHORT).show();
        } else if ((!binding.AddPanicAttackRecordEditTextTitle.getText().toString().trim().isEmpty() ||
                !binding.AddPanicAttackRecordEditTextDescription.getText().toString().trim().isEmpty()) && intent.hasExtra(EXTRA_ID)) {
            Toast.makeText(getApplicationContext(), getString(R.string.recordNotUpdated), Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void setError() {

        binding.AddPanicAttackRecordTextInputLayoutTitle.setError(null);
        binding.AddPanicAttackRecordTextInputLayoutDescription.setError(null);
    }

    private void addNewRecord() {
        int attackStrength;
        String title;
        String description;
        String date;
        String time;

        attackStrength = binding.AddPanicAttackRecordNumberPicker.getValue();
        title = binding.AddPanicAttackRecordEditTextTitle.getText().toString();
        description = binding.AddPanicAttackRecordEditTextDescription.getText().toString();
        date = binding.AddPanicAttackRecordEditTextDate.getText().toString();
        time = binding.AddPanicAttackRecordEditTextTime.getText().toString();

        setError();

        int error = 0;

        if (title.trim().isEmpty()) {
            error++;
            binding.AddPanicAttackRecordTextInputLayoutTitle.setError(getResources().getString(R.string.titleCantBeEmpty));
        }

        if (description.trim().isEmpty()) {
            error++;
            binding.AddPanicAttackRecordTextInputLayoutDescription.setError(getResources().getString(R.string.descriptionCantBeEmpty));
        }

        if (error == 0) {
            String res = "";
            if (intent.hasExtra(EXTRA_ID)) {
                res = binding.AddOrEditPanicAttackCreationDateTextView.getText().toString();
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
            data.putExtra(EXTRA_STRENGTH, attackStrength);
            data.putExtra(EXTRA_ATTACK_DATE, date);
            data.putExtra(EXTRA_ATTACK_TIME, time);

            int id = getIntent().getIntExtra(EXTRA_ID, -1);
            if (id != -1) {
                data.putExtra(EXTRA_ID, id);
            }

            setResult(RESULT_OK, data);
            finish();
        }
    }
}
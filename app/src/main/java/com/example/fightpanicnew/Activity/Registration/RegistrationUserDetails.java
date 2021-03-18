/*
Drugi dio registracije korisnika, dio sa podatcima o samom korisniku, opcionalni dio koji korisnik može preskočiti,
ispuniti dijelom pa poslije dovršiti ili uopće ne ispuniti pa se profilu dodijele default podatci. Nakon ove aktivnosti
korisnik nastavlja dalje na finalni dio registracije.
*/

package com.example.fightpanicnew.Activity.Registration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.example.fightpanicnew.R;
import com.example.fightpanicnew.databinding.ActivityRegistrationUserDetailsBinding;

import java.util.Objects;

import static com.example.fightpanicnew.Constants.DARK_MODE_SWITCH;
import static com.example.fightpanicnew.Constants.DARK_MODE_VALUE;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_FIRST_NAME;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_GENDER_HIDDEN;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_GENDER_ID;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_GENDER_STATE;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_GENDER_TEXT;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_LAST_NAME;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_LOCATION;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_OCCUPATION;
import static com.example.fightpanicnew.Constants.EDIT_TEXT_FIELDS_SHORT_DESCRIPTION;
import static com.example.fightpanicnew.Constants.NUMBER_PICKER_AGE;
import static com.example.fightpanicnew.Constants.REGISTER_USER_INFORMATION_EDIT_TEXT_FIELDS_INFORMATION;
import static com.example.fightpanicnew.HelperClasses.Validation.validateFields;

public class RegistrationUserDetails extends AppCompatActivity {

    private ActivityRegistrationUserDetailsBinding binding;

    private String age;
    private String location;
    private String occupation;
    private String shortDescription;
    private String genderText;
    private int genderID;
    private Boolean genderState;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationUserDetailsBinding.inflate(getLayoutInflater());
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

        Toolbar toolbar = findViewById(R.id.RegisterUserDetailsAppBar);
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.registrationUserDetails));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.register_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.registerUserDetailsNext) {

            if (validateFields(binding.RegisterUserInformationAgeEditText.getText().toString())) {
                if (Integer.parseInt(binding.RegisterUserInformationAgeEditText.getText().toString()) > 99 || Integer.parseInt(binding.RegisterUserInformationAgeEditText.getText().toString()) < 12) {
                    binding.RegisterUserInformationAgeTextInputLayout.setError(getString(R.string.registrationAgeNotInConstraint));
                    return true;
                }
            }

            saveEditTextFieldsDataNextOrBackButton();
            startActivity(new Intent(RegistrationUserDetails.this, RegistrationFinish.class));
            return true;
        } else if (item.getItemId() == android.R.id.home) {

            if (validateFields(binding.RegisterUserInformationAgeEditText.getText().toString())) {
                if (Integer.parseInt(binding.RegisterUserInformationAgeEditText.getText().toString()) > 99 || Integer.parseInt(binding.RegisterUserInformationAgeEditText.getText().toString()) < 12) {
                    binding.RegisterUserInformationAgeEditText.setError(getString(R.string.registrationAgeNotInConstraint));
                    return true;
                }
            }

            saveEditTextFieldsDataNextOrBackButton();
            Intent a = new Intent(RegistrationUserDetails.this, RegistrationBasicDetails.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (validateFields(binding.RegisterUserInformationAgeEditText.getText().toString())) {
            if (Integer.parseInt(binding.RegisterUserInformationAgeEditText.getText().toString()) > 99 || Integer.parseInt(binding.RegisterUserInformationAgeEditText.getText().toString()) < 12) {
                binding.RegisterUserInformationAgeTextInputLayout.setError(getString(R.string.registrationAgeNotInConstraint));
                return;
            }
        }

        saveEditTextFieldsDataNextOrBackButton();
        Intent a = new Intent(RegistrationUserDetails.this, RegistrationBasicDetails.class);
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(a);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(NUMBER_PICKER_AGE, age);
        savedInstanceState.putString(EDIT_TEXT_FIELDS_LOCATION, location);
        savedInstanceState.putString(EDIT_TEXT_FIELDS_OCCUPATION, occupation);
        savedInstanceState.putString(EDIT_TEXT_FIELDS_SHORT_DESCRIPTION, shortDescription);
        savedInstanceState.putString(EDIT_TEXT_FIELDS_GENDER_TEXT, genderText);
        savedInstanceState.putInt(EDIT_TEXT_FIELDS_GENDER_ID, genderID);
        if (genderState != null) {
            savedInstanceState.putBoolean(EDIT_TEXT_FIELDS_GENDER_STATE, genderState);
        }
        savedInstanceState.putString(EDIT_TEXT_FIELDS_FIRST_NAME, firstName);
        savedInstanceState.putString(EDIT_TEXT_FIELDS_LAST_NAME, lastName);

        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        age = savedInstanceState.getString(NUMBER_PICKER_AGE);
        binding.RegisterUserInformationAgeEditText.setText(age);
        location = savedInstanceState.getString(EDIT_TEXT_FIELDS_LOCATION);
        binding.RegisterUserInformationLocationEditText.setText(location);
        occupation = savedInstanceState.getString(EDIT_TEXT_FIELDS_OCCUPATION);
        binding.RegisterUserInformationOccupationEditText.setText(occupation);
        shortDescription = savedInstanceState.getString(EDIT_TEXT_FIELDS_SHORT_DESCRIPTION);
        binding.RegisterUserInformationShortDescriptionEditText.setText(shortDescription);
        firstName = savedInstanceState.getString(firstName);
        binding.RegisterUserInformationFirstNameEditText.setText(firstName);
        lastName = savedInstanceState.getString(lastName);
        binding.RegisterUserInformationLastNameEditText.setText(lastName);
        genderText = savedInstanceState.getString(EDIT_TEXT_FIELDS_GENDER_TEXT);
        genderID = savedInstanceState.getInt(EDIT_TEXT_FIELDS_GENDER_ID);
        genderState = savedInstanceState.getBoolean(EDIT_TEXT_FIELDS_GENDER_STATE);

        if (genderState) {
            binding.RegisterUserInformationRadioGroup.check(genderID);
        }
    }

    private void saveEditTextFieldsDataNextOrBackButton() {
        SharedPreferences sharedPreferencesEditTextInformation = getSharedPreferences(REGISTER_USER_INFORMATION_EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesEditTextInformation.edit();
        editor.putString(EDIT_TEXT_FIELDS_LOCATION, Objects.requireNonNull(binding.RegisterUserInformationLocationEditText.getText()).toString());
        editor.putString(EDIT_TEXT_FIELDS_OCCUPATION, Objects.requireNonNull(binding.RegisterUserInformationOccupationEditText.getText()).toString());
        editor.putString(EDIT_TEXT_FIELDS_FIRST_NAME, Objects.requireNonNull(binding.RegisterUserInformationFirstNameEditText.getText()).toString());
        editor.putString(EDIT_TEXT_FIELDS_LAST_NAME, Objects.requireNonNull(binding.RegisterUserInformationLastNameEditText.getText()).toString());
        editor.putString(EDIT_TEXT_FIELDS_SHORT_DESCRIPTION, Objects.requireNonNull(binding.RegisterUserInformationShortDescriptionEditText.getText()).toString());
        editor.putString(NUMBER_PICKER_AGE, Objects.requireNonNull(binding.RegisterUserInformationAgeEditText.getText()).toString());

        int selectedId = binding.RegisterUserInformationRadioGroup.getCheckedRadioButtonId();

        if (selectedId != -1) {
            RadioButton radioButton = findViewById(selectedId);
            editor.putString(EDIT_TEXT_FIELDS_GENDER_TEXT, radioButton.getText().toString());
            if (selectedId == R.id.RegisterUserInformationMaleRadioButton) {
                editor.putString(EDIT_TEXT_FIELDS_GENDER_HIDDEN, "Male");
            } else if (selectedId == R.id.RegisterUserInformationFemaleRadioButton) {
                editor.putString(EDIT_TEXT_FIELDS_GENDER_HIDDEN, "Female");
            } else {
                editor.putString(EDIT_TEXT_FIELDS_GENDER_HIDDEN, "Unknown");
            }
            editor.putInt(EDIT_TEXT_FIELDS_GENDER_ID, selectedId);
            editor.putBoolean(EDIT_TEXT_FIELDS_GENDER_STATE, true);
        }
        editor.apply();
    }

    private void loadEditTextFieldsData() {
        SharedPreferences sharedPreferencesEditTextInformation = getSharedPreferences(REGISTER_USER_INFORMATION_EDIT_TEXT_FIELDS_INFORMATION, MODE_PRIVATE);
        binding.RegisterUserInformationAgeEditText.setText(sharedPreferencesEditTextInformation.getString(NUMBER_PICKER_AGE, ""), TextView.BufferType.EDITABLE);
        binding.RegisterUserInformationLocationEditText.setText(sharedPreferencesEditTextInformation.getString(EDIT_TEXT_FIELDS_LOCATION, ""), TextView.BufferType.EDITABLE);
        binding.RegisterUserInformationOccupationEditText.setText(sharedPreferencesEditTextInformation.getString(EDIT_TEXT_FIELDS_OCCUPATION, ""), TextView.BufferType.EDITABLE);
        binding.RegisterUserInformationShortDescriptionEditText.setText(sharedPreferencesEditTextInformation.getString(EDIT_TEXT_FIELDS_SHORT_DESCRIPTION, ""), TextView.BufferType.EDITABLE);
        binding.RegisterUserInformationFirstNameEditText.setText(sharedPreferencesEditTextInformation.getString(EDIT_TEXT_FIELDS_FIRST_NAME, ""), TextView.BufferType.EDITABLE);
        binding.RegisterUserInformationLastNameEditText.setText(sharedPreferencesEditTextInformation.getString(EDIT_TEXT_FIELDS_LAST_NAME, ""), TextView.BufferType.EDITABLE);

        boolean radioGroupState = sharedPreferencesEditTextInformation.getBoolean(EDIT_TEXT_FIELDS_GENDER_STATE, false);

        if (radioGroupState) {
            binding.RegisterUserInformationRadioGroup.check(sharedPreferencesEditTextInformation.getInt(EDIT_TEXT_FIELDS_GENDER_ID, 0));
        }
    }
}
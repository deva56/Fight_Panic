package com.example.fightpanicnew.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.fightpanicnew.Models.Response;
import com.example.fightpanicnew.Models.User;
import com.example.fightpanicnew.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.example.fightpanicnew.Constants.LOGIN_EMAIL;
import static com.example.fightpanicnew.Constants.LOGIN_INFORMATION;
import static com.example.fightpanicnew.Constants.LOGIN_TOKEN;
import static com.example.fightpanicnew.HelperClasses.Validation.validateFields;
import static com.example.fightpanicnew.HelperClasses.Validation.validatePasswordRegistration;
import static com.example.fightpanicnew.Network.RetrofitBuilder.getRetrofit;

public class ChangePasswordFragment extends DialogFragment {

    public static final String TAG = ChangePasswordFragment.class.getSimpleName();

    private TextInputLayout oldPasswordInputLayout;
    private TextInputLayout newPasswordInputLayout;
    private TextInputLayout newPasswordConfirmationInputLayout;
    private TextView resultTextView;
    private ProgressBar progressBar;

    private CompositeDisposable compositeDisposable;
    private io.reactivex.disposables.CompositeDisposable compositeDisposable2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        initViews(view);

        compositeDisposable = new CompositeDisposable();
        compositeDisposable2 = new io.reactivex.disposables.CompositeDisposable();

        return view;
    }

    private void initViews(View v) {
        Button goBack = v.findViewById(R.id.GoBackChangePassword);
        Button changePassword = v.findViewById(R.id.ChangePasswordButton);
        oldPasswordInputLayout = v.findViewById(R.id.ChangePasswordOldPasswordTextInputLayout);
        EditText oldPasswordEditText = v.findViewById(R.id.ChangePasswordOldPasswordEditText);
        newPasswordInputLayout = v.findViewById(R.id.ChangePasswordNewPasswordTextInputLayout);
        EditText newPasswordEditText = v.findViewById(R.id.ChangePasswordNewPasswordEditText);
        newPasswordConfirmationInputLayout = v.findViewById(R.id.ChangePasswordNewPasswordConfirmationTextInputLayout);
        EditText newPasswordConfirmationEditText = v.findViewById(R.id.ChangePasswordNewPasswordConfirmationEditText);
        resultTextView = v.findViewById(R.id.ChangePasswordResultTextView);
        progressBar = v.findViewById(R.id.ChangePasswordProgressBar);

        progressBar.setVisibility(View.GONE);
        resultTextView.setVisibility(View.GONE);

        goBack.setOnClickListener(view -> dismiss());

        changePassword.setOnClickListener(view ->
        {
            String oldPassword = oldPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String newPasswordConfirmation = newPasswordConfirmationEditText.getText().toString();

            int errors = 0;

            setError();

            if (!validateFields(oldPassword)) {
                errors++;
                oldPasswordInputLayout.setError(getString(R.string.oldPasswordNotEmpty));
            }
            if (!validatePasswordRegistration(newPassword)) {
                errors++;
                newPasswordInputLayout.setError(getString(R.string.registrationPasswordTooShort));
            }
            if (!validateFields(newPassword)) {
                errors++;
                newPasswordInputLayout.setError(getString(R.string.newPasswordCantBeEmpty));
            }
            if (!newPasswordConfirmation.equals(newPassword)) {
                errors++;
                newPasswordConfirmationInputLayout.setError(getString(R.string.passwordConfirmationDoesntMatch));
            }
            if (!validateFields(newPasswordConfirmation)) {
                errors++;
                newPasswordConfirmationInputLayout.setError(getString(R.string.passwordConfirmationNotEmpty));
            }

            if (errors == 0) {

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LOGIN_INFORMATION, Context.MODE_PRIVATE);
                String token = sharedPreferences.getString(LOGIN_TOKEN, "");
                String email = sharedPreferences.getString(LOGIN_EMAIL, "");

                User user = new User();
                user.setPassword(oldPassword);
                user.setNewPassword(newPassword);

                progressBar.setVisibility(View.VISIBLE);

                compositeDisposable.add(getRetrofit(token).changePassword(email, user)
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::onSuccessResponse, this::onFailureResponse));
            }

        });
    }

    private void onSuccessResponse(Response response) {
        getActivity().runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            dismiss();
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.passwordChangedSuccessfully), Toast.LENGTH_SHORT).show();
        });
    }

    private void onFailureResponse(Throwable throwable) {
        getActivity().runOnUiThread(() -> {

            progressBar.setVisibility(View.GONE);

            if (throwable instanceof HttpException) {

                Gson gson = new GsonBuilder().create();

                try {
                    String errorBody = ((HttpException) throwable).response().errorBody().string();
                    Response response = gson.fromJson(errorBody, Response.class);
                    switch (response.getMessage()) {
                        case "Invalid Old Password !":
                            oldPasswordInputLayout.setError(getString(R.string.invalidOldPassword));
                            break;
                        case "Invalid Token !":
                            resultTextView.setVisibility(View.VISIBLE);
                            resultTextView.setText(getString(R.string.invalidToken));
                            break;
                        default:
                            resultTextView.setVisibility(View.VISIBLE);
                            resultTextView.setText(throwable.getMessage());
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

                resultTextView.setVisibility(View.VISIBLE);
                resultTextView.setText(getString(R.string.noInternetConnection));
            }

            /*if (throwable.getMessage().equals("Invalid Old Password !")) {
                oldPasswordInputLayout.setError(getString(R.string.invalidOldPassword));
                progressBar.setVisibility(View.GONE);
            } else if (throwable.getMessage().equals("Invalid Token !")) {
                resultTextView.setVisibility(View.VISIBLE);
                resultTextView.setText(throwable.getMessage());
                progressBar.setVisibility(View.GONE);
            } else {
                resultTextView.setVisibility(View.VISIBLE);
                resultTextView.setText(throwable.getMessage());
                progressBar.setVisibility(View.GONE);
            }*/
        });
    }

    private void setError() {
        resultTextView.setVisibility(View.GONE);
        oldPasswordInputLayout.setError(null);
        newPasswordInputLayout.setError(null);
        newPasswordConfirmationInputLayout.setError(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        compositeDisposable2.dispose();
    }
}

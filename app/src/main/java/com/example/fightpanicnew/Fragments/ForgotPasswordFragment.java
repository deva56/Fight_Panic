/*
Fragment za slanje koda na e-mail ako je korisnik zaboravio lozinku te ju ovim putem može ponovno postaviti.
*/

package com.example.fightpanicnew.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.fightpanicnew.Activity.ProfileRegisterOrLogin;
import com.example.fightpanicnew.Models.Response;
import com.example.fightpanicnew.Models.User;
import com.example.fightpanicnew.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.example.fightpanicnew.HelperClasses.Validation.validateEmail;
import static com.example.fightpanicnew.HelperClasses.Validation.validateFields;
import static com.example.fightpanicnew.Network.RetrofitBuilder.getRetrofit;

public class ForgotPasswordFragment extends DialogFragment {

    public interface Listener {

        void onPasswordReset(String message);
    }

    public static final String TAG = ForgotPasswordFragment.class.getSimpleName();

    private EditText emailEditText;
    private EditText tokenEditText;
    private EditText passwordEditText;
    private TextView messageTextView;
    private TextInputLayout emailInputLayout;
    private TextInputLayout tokenInputLayout;
    private TextInputLayout passwordInputLayout;
    private ProgressBar progressBar;

    private CompositeDisposable disposable;

    private String email;

    private boolean isInit = true;

    private Listener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        disposable = new CompositeDisposable();
        initViews(view);
        return view;
    }

    private void initViews(View v) {

        emailEditText = v.findViewById(R.id.ForgotPasswordEmailEditText);
        tokenEditText = v.findViewById(R.id.ForgotTokenEmailEditText);
        passwordEditText = v.findViewById(R.id.ForgotPasswordPasswordEditText);
        Button resetPasswordButton = v.findViewById(R.id.ForgotPasswordResetPasswordButton);
        progressBar = v.findViewById(R.id.ForgotPasswordProgressBar);
        messageTextView = v.findViewById(R.id.ForgotPasswordTextMessage);
        emailInputLayout = v.findViewById(R.id.ForgotPasswordEmailTextInputLayout);
        tokenInputLayout = v.findViewById(R.id.ForgotPasswordTokenTextInputLayout);
        passwordInputLayout = v.findViewById(R.id.ForgotPasswordPasswordTextInputLayout);

        resetPasswordButton.setOnClickListener(view -> {
            if (isInit) resetPasswordInit();
            else resetPasswordFinish();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (ProfileRegisterOrLogin) context;
    }

    private void setEmptyFields() {

        emailInputLayout.setError(null);
        tokenInputLayout.setError(null);
        passwordInputLayout.setError(null);
        messageTextView.setText(null);
    }

    public void setToken(String token) {

        tokenEditText.setText(token);
    }

    private void resetPasswordInit() {

        setEmptyFields();

        email = emailEditText.getText().toString();

        int err = 0;

        if (!validateEmail(email)) {

            err++;
            emailInputLayout.setError(getString(R.string.emailNotValid));
        }

        if (!validateFields(email)) {

            err++;
            emailInputLayout.setError(getString(R.string.emailNotEmpty));
        }

        if (err == 0) {

            progressBar.setVisibility(View.VISIBLE);
            resetPasswordInitProgress(email);
        }
    }

    private void resetPasswordFinish() {

        setEmptyFields();

        String token = tokenEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        int err = 0;

        if (!validateFields(token)) {

            err++;
            tokenInputLayout.setError(getString(R.string.tokenNotEmpty));
        }

        if (!validateFields(password)) {

            err++;
            passwordInputLayout.setError(getString(R.string.passwordNotEmpty));
        }

        if (err == 0) {

            progressBar.setVisibility(View.VISIBLE);

            User user = new User();
            user.setPassword(password);
            user.setToken(token);
            resetPasswordFinishProgress(user);
        }
    }

    private void resetPasswordInitProgress(String email) {

        disposable.add(getRetrofit().resetPasswordInit(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void resetPasswordFinishProgress(User user) {

        disposable.add(getRetrofit().resetPasswordFinish(email, user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Response response) {

        progressBar.setVisibility(View.GONE);

        if (isInit) {

            isInit = false;
            if (response.getMessage().equals("Check mail for instructions")) {
                showMessage(getString(R.string.checkMailForInstructions));
            }
            emailInputLayout.setVisibility(View.GONE);
            tokenInputLayout.setVisibility(View.VISIBLE);
            passwordInputLayout.setVisibility(View.VISIBLE);

        } else {

            if (response.getMessage().equals("Password Changed Successfully!")) {
                listener.onPasswordReset(getString(R.string.passwordChangedSuccessfully));
            }
            dismiss();
        }
    }

    private void handleError(Throwable error) {

        progressBar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody, Response.class);
                switch (response.getMessage()) {
                    case "User Not Found!":
                        emailInputLayout.setError(getString(R.string.userNotFound));
                        break;
                    case "Time Out! Try again.":
                        showMessage(getString(R.string.timeOut));
                        break;
                    case "Invalid Token!":
                        tokenInputLayout.setError(getString(R.string.invalidToken));
                        break;
                    default:
                        showMessage(getString(R.string.noInternetConnection));
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showMessage(getString(R.string.noInternetConnection));
        }
    }

    private void showMessage(String message) {

        messageTextView.setVisibility(View.VISIBLE);
        messageTextView.setText(message);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
/*
Prikazuje se ako korisnik nije ulogiran ili registriran pri ulazu na fight club aktivnost te ne može pristupiti chat grupama.
*/

package com.example.fightpanicnew.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.fightpanicnew.Activity.ProfileRegisterOrLogin;
import com.example.fightpanicnew.Activity.Registration.RegistrationBasicDetails;
import com.example.fightpanicnew.R;

public class FightClubLoginFragment extends DialogFragment {

    public static final String TAG = FightClubLoginFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fight_club_login, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View v) {
        Button login = v.findViewById(R.id.FightClubFragmentLoginButton);
        Button register = v.findViewById(R.id.FightClubFragmentRegisterButton);

        login.setOnClickListener(view -> {
            Intent a = new Intent(getActivity(), ProfileRegisterOrLogin.class);
            a.putExtra("GoBackFightClub", true);
            startActivity(a);
        });

        register.setOnClickListener(view -> {
            Intent a = new Intent(getActivity(), RegistrationBasicDetails.class);
            a.putExtra("GoBackFightClub", true);
            startActivity(a);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireActivity().finish();
    }
}

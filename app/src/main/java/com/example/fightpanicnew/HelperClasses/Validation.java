/*
Pomoćne klase za validaciju inputa u raznim dijelovima aplikacije.
*/

package com.example.fightpanicnew.HelperClasses;

import android.text.TextUtils;
import android.util.Patterns;

public class Validation {

    public static boolean validateFields(String name) {

        if (TextUtils.isEmpty(name) || name.trim().length() == 0) {

            return false;

        } else {

            return true;
        }
    }

    public static boolean validateEmail(String string) {

        if (TextUtils.isEmpty(string) || !Patterns.EMAIL_ADDRESS.matcher(string).matches()) {

            return false;

        } else {

            return true;
        }
    }

    public static boolean validateField(String string) {

        int error = 0;
        int lengthOfString = 0;

        for (int i = 0; i < string.length(); i++) {
            lengthOfString++;

            if (string.charAt(i) == ' ') {
                error++;
            }
        }

        if (error == lengthOfString) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean validatePassword(String string)
    {
        if (string.trim().length() < 6) {

            return false;

        } else {

            return true;
        }
    }

    public static boolean validatePasswordRegistration(String string)
    {
        return string.trim().length() >= 8;
    }
}

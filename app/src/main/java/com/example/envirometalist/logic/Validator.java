package com.example.envirometalist.logic;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Validator {

    public static boolean isValidEmail (String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            return false;
        }
        Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(userEmail);
        return matcher.matches();
    }
    public static boolean isValidUserName(CharSequence target) {
        return (!TextUtils.isEmpty(target));
    }
    public static boolean isValidRole(CharSequence target) {
        return (!TextUtils.isEmpty(target));
    }

    public static boolean isValidAvatarUrl(CharSequence target) {
        return (!TextUtils.isEmpty(target));
    }

}

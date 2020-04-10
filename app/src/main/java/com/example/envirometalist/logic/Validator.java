package com.example.envirometalist.logic;

import android.text.TextUtils;
import android.util.Patterns;

public final class Validator {

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
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

package com.ecc.ema;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dg185p on 10/26/2015.
 */
public class Utility {

    /**
     * Provides a set of utility functions.
     */
    private static final String TAG = Utility.class.getSimpleName();

    /**
     * Disable instances.
     */
    private Utility() {
    }

    //TODO: Move all the utility functions here.

    /**
     * Verifies that the giving string is a valid email id
     *
     * @return boolean
     */
    public static boolean isValidEmailAddress(String emailId) {
        if (null == emailId)
            return false;

        Pattern ePattern = Patterns.EMAIL_ADDRESS;
        if (ePattern.matcher(emailId).matches())
            return true;
        else
            return false;
    }

    public static String validatePhoneNumber(String phoneNumber) {
        if (null == phoneNumber && phoneNumber.length() < 10) {
            return null;
        }

        phoneNumber = phoneNumber.replaceAll("[^[+]\\d]", "");
        // Valid 10 digits or 11 digit number which starts with 1 or +1
        String regExValidUSPhoneNUmber = "^[+][1][2-9]\\d{9}$|^[1][2-9]\\d{9}$|^[2-9]\\d{9}$";
        Pattern phNumPattern = Pattern.compile(regExValidUSPhoneNUmber);
        Matcher matcher = phNumPattern.matcher(phoneNumber);
        if (matcher.matches()) {
            if (phoneNumber.length() == 10)
                phoneNumber = "+1" + phoneNumber;
            else if (phoneNumber.length() == 11)
                phoneNumber = "+" + phoneNumber;
            return phoneNumber;
        }
        return null;
    }
}

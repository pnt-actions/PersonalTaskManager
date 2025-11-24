package com.example.personaltaskmanager.features.authentication.domain.validator;

public class UserValidator {

    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public static boolean isValidPassword(String pwd) {
        return pwd != null && pwd.length() >= 6;
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= 3;
    }
}

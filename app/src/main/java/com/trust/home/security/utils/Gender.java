package com.trust.home.security.utils;

public enum Gender {
    MALE(0, "Male"),
    FEMALE(1, "Female");

    int value;
    String text;

    Gender(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static Gender getGender(Integer value) {
        if(value == MALE.getValue()) {
            return MALE;
        } else return FEMALE;
    }
}

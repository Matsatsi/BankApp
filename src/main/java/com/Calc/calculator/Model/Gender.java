package com.Calc.calculator.Model;

public enum Gender {
    FEMALE("FEMALE"),
    MALE("MALE");
    private String value;

    private Gender(String value){
        this.value= value;
    }

    public String getValue() {
        return value;
    }
}

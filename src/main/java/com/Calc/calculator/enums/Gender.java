package com.Calc.calculator.enums;

import lombok.Getter;

@Getter
public enum Gender {
    FEMALE("FEMALE"),
    MALE("MALE");
    private final String value;

    private Gender(String value){
        this.value= value;
    }

}

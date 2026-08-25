package com.Calc.calculator.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Person {
    private String name;
    private int age;
    private Gender Gender;

    public Person(String name, int age,Gender gender) {
        this.name=name;
        this.age = age;

    }

}

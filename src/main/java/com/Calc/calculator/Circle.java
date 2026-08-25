package com.Calc.calculator;

import com.Calc.calculator.Model.Gender;
import com.Calc.calculator.Model.Person;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@SpringBootApplication
public class Circle {
	public static void main(String[] args) {

        List<Person> people = addPerson();

        List<Person> elders = people.stream()
                .filter(pips -> pips.getAge() > 50)
                .toList();

        elders.forEach(System.out::println);
    }
    public static List<Person> addPerson(){

       return List.of(
               new Person("Tumi",99, Gender.MALE),
               new Person("Mogaleadi",12, Gender.FEMALE),
               new Person("Germina",44, Gender.FEMALE),
               new Person("Matsatsi",7, Gender.MALE),
               new Person("Mina",68, Gender.MALE));

    }
}

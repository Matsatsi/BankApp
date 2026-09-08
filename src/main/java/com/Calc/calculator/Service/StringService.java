package com.Calc.calculator.Service;

import com.Calc.calculator.Model.Person;
import com.Calc.calculator.enums.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StringService {
    @Autowired
    //Person person;
    Map<Integer, Person> people = new HashMap<>();


    public void addPeople(){

        List<Person> pips =addPerson() ;
        for(Map.Entry<Integer,Person> entry: people.entrySet()){
           for(int i=0; i< pips.size();i++)
            people.put(i,pips.get(i));
        }

    }
    public static List<Person> addPerson(){

        return List.of(
                new Person("Tumi",99, Gender.MALE),
                new Person("Mogaleadi",12, Gender.FEMALE),
                new Person("Germina",44, Gender.FEMALE),
                new Person("Matsatsi",7, Gender.MALE),
                new Person("Mina",68, Gender.MALE));

    }
    public void getPeople(){

        people.forEach((key, value) -> System.out.print(key + ":" + key));

    }
    public void displayPeople(){

        people.entrySet().stream().filter( e-> e.getValue().getAge() >20 ).forEach(System.out::println);
    }

}

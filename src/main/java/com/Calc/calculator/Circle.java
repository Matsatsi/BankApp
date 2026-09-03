package com.Calc.calculator;

import com.Calc.calculator.Model.Person;
import com.Calc.calculator.Model.Reusable;
import com.Calc.calculator.enums.Gender;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Comparator;
import java.util.List;

@SpringBootApplication
public class Circle {
	public static void main(String[] args) {

        /**Generics*/
        Reusable<String> name = new Reusable<>("Germy");
        Reusable<Integer> age = new Reusable<>(23);
        //***************************************************

        /**Streams*/
        List<Person> people = addPerson();

        //filter
        List<Person> elders = people.stream()
                .filter(pips -> pips.getAge() > 50)
                .toList();

        //sort
        List<Person> sort = people.stream()
                        .sorted(Comparator.comparing(Person::getAge)).toList();
        //all match
        //List<Person> match = people.stream().anyMatch(person -> people.contains(Gender.FEMALE))
        //any match
        //non match
        //max
        //min

        //people.forEach(System.out::println);
    }
    public static List<Person> addPerson(){

       return List.of(
               new Person("Tumi",99, Gender.MALE),
               new Person("Mogaleadi",12, Gender.FEMALE),
               new Person("Germina",44, Gender.FEMALE),
               new Person("Matsatsi",7, Gender.MALE),
               new Person("Mina",68, Gender.MALE));

    }
    //Generic methods and WildCards used
    public static <T> void print(List<?> t){
        System.out.println(t);
    }
}

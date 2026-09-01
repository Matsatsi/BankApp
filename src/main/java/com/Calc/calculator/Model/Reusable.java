package com.Calc.calculator.Model;

public class Reusable<T> {
    //Generics and how they are used
    private T t;
    public Reusable(T t){
        this.t =t;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}

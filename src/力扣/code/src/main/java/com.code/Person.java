package com.code;


import java.util.function.Function;

public class Person {
    private String name;

    public Person(Function<String, String> nameSetter) {
        this.name = nameSetter.apply("John");
    }


    public void setName(Function<String, String> nameSetter) {
        this.name = nameSetter.apply("John");

    }

    public String getName() {
        return name;
    }

    public static void main(String[] args) {
        // 创建一个 Person 对象，并指定 name 属性的setter方法为接受一个字符串参数并将其转换为大写的方法
        Person person = new Person((name) -> name.toUpperCase());

        // 将 person 对象的 name 属性设置为 "JON"
        person.setName((name) -> name + "!");

        // 输出 person 对象的 name 属性
        System.out.println(person.getName()); // "JOHN!"
    }
}





package model;

import java.io.Serializable;

public class Participant implements Serializable, HasId<Integer> {
    private int id;
    private String name;
    private int age;

    public Participant() {
        this.id = 0;
        this.name = "";
        this.age = 0;
    }

    public Participant(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Participant [id=" + id + ", name=" + name + ", age=" + age + "]";
    }
}

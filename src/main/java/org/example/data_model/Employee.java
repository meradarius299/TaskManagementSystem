package org.example.data_model;
import java.io.Serializable;

public class Employee implements Serializable {
    private int idEmployee;
    private String name;

    public Employee(int idEmployee, String name) {
        this.idEmployee = idEmployee;
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public int getIdEmployee() {
        return idEmployee;
    }
}

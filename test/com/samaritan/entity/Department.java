package com.samaritan.entity;

import javax.persistence.*;

@Entity
@Table(name = "department", schema = "dummy")
public class Department{

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;

    @Column(name = "name", nullable = false, length = 50)
    private String name ;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="manager", unique = true)
    private Employee manager ;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }
}

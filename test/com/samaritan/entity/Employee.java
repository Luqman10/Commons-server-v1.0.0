/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.samaritan.entity;

import com.google.gson.annotations.Expose;

import javax.persistence.*;

/**
 * maps to the employee table in dummy (db)
 * @author electron
 */
@Entity
@Table(name = "employee", schema = "dummy")
public class Employee{
    
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Integer id ;
    
    @Column(name = "f_name", nullable = false, length = 50)
    @Expose
    private String firstName ;
    
    @Column(name = "l_name", nullable = false, length = 50)
    @Expose
    private String lastName ;
    
    @Column(name = "email", nullable = false, length = 50)
    @Expose
    private String email ;

    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public boolean equals(Object object){

        if (this == object) return true ;
        if (object == null || getClass() != object.getClass()) return false ;

        Employee employee = (Employee)object ;
        return id.equals(employee.getId()) &&
               firstName.equals(employee.getFirstName()) &&
               lastName.equals(employee.getLastName()) &&
               email.equals(employee.getEmail()) ;
    }
    
}

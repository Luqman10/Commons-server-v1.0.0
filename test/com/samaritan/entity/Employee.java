/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.samaritan.entity;

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
    private Integer id ;
    
    @Column(name = "f_name", nullable = false, length = 50)
    private String firstName ;
    
    @Column(name = "l_name", nullable = false, length = 50)
    private String lastName ;
    
    @Column(name = "email", nullable = false, length = 50)
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
    
    
    
}

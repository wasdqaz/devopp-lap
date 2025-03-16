package org.springframework.samples.petclinic.vets.model;

import java.util.HashSet;
import java.util.Set;

public class Vet {
    private Integer id;
    private String firstName;
    private String lastName;
    private String name;
    private Set<Specialty> specialties = new HashSet<>();

    // Getters and setters for id, firstName, lastName, and name

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Specialty> getSpecialties() {
        return specialties;
    }

    public void addSpecialty(Specialty specialty) {
        this.specialties.add(specialty);
    }
}

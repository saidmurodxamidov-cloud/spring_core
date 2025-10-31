package org.example.model;

import java.time.LocalDate;

public class Trainee extends User{
    private LocalDate dateOfBirth;
    private String address;

    public Trainee(Long userId,String firstName, String lastName, String userName, String password, boolean isActive, LocalDate dateOfBirth, String address) {
        super(userId,firstName, lastName, userName, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
    public Trainee(){}


    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

package org.example.model;

public class Trainer extends User{
    private String specialization;

    public Trainer(Long userId,String firstName, String lastName, String userName, String password, boolean isActive,String specialization) {
        super(userId,firstName, lastName, userName, password, isActive);
        this.specialization = specialization;
    }
    public Trainer(){}

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}

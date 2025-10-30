package org.example.model;

public class Trainer extends User{
    private Long userId;
    private String specialization;

    public Trainer(String firstName, String lastName, String userName, String password, boolean isActive,String specialization) {
        super(firstName, lastName, userName, password, isActive);
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

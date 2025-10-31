package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Setter
@Getter
@ToString(callSuper = true)
public class Trainer extends User{
    private Long userId;
    private String specialization;

    public Trainer(String firstName, String lastName, String userName, String password, boolean isActive,String specialization) {
        super(firstName, lastName, userName, password, isActive);
        this.specialization = specialization;
    }
    public Trainer(){}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Trainer trainer = (Trainer) o;
        return Objects.equals(userId, trainer.userId) && Objects.equals(specialization, trainer.specialization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, specialization);
    }
}

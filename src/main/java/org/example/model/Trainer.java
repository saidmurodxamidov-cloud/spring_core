package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Setter
@Getter
@ToString(callSuper = true)
public class Trainer extends User{
    private String specialization;

    public Trainer(Long userId,String firstName, String lastName, String userName, String password, boolean isActive,String specialization) {
        super(userId, firstName, lastName, userName, password, isActive);
        this.specialization = specialization;

    }
    public Trainer(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trainer trainer)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(specialization, trainer.specialization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),specialization);
    }
}

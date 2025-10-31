package org.example.model;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;


@Setter
@Getter
@ToString(callSuper = true)
public class Trainee extends User{
    private LocalDate dateOfBirth;
    private String address;

    public Trainee(String firstName, String lastName, String userName, String password, boolean isActive, Long userId, LocalDate dateOfBirth, String address) {
        super(userId, firstName, lastName, userName, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
    public Trainee(){}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trainee trainee)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(dateOfBirth, trainee.dateOfBirth)
                && Objects.equals(address, trainee.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),dateOfBirth, address);
    }
}

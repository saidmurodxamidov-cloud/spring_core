package org.example.model;

import lombok.*;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Trainer extends User{
    private String specialization;
    public Trainer(Long userId,String firstName, String lastName, String userName, char[] password, boolean isActive,String specialization) {
        super(userId, firstName, lastName, userName, password, isActive);
        this.specialization = specialization;
    }

}

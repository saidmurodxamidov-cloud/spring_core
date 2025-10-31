package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class User {
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private boolean isActive;

    public User(){}

    public User(String firstName, String lastName, String userName, String password, boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.isActive = isActive;
    }

}

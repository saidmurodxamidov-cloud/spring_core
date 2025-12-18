package org.example.service;

import org.example.dto.response.AuthResponse;

public interface UserService {


    AuthResponse changePassword(String usernameFromToken, String oldPassword, String newPassword);


    Boolean toggleUserActiveStatus(String username);
}

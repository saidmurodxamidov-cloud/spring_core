package org.example.persistence.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    TRAINEE, TRAINER, ADMIN, PROMETHEUS;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}

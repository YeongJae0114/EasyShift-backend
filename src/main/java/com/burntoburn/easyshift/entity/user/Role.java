package com.burntoburn.easyshift.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum Role implements GrantedAuthority {

    GUEST("GUEST"),
    WORKER("USER"),
    ADMINISTRATOR("ADMIN");

    private final String key;

    public static Role fromString(String key) {
        for (Role role : Role.values()) {
            if (role.getKey().equals(key)) {
                return role;
            }
        }
        return null;
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name(); // "ROLE_GUEST", "ROLE_WORKER", "ROLE_ADMINISTRATOR"
    }
}

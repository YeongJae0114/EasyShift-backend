package com.burntoburn.easyshift.service.login;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class NoOpPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        // 평문 그대로 반환
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // 평문과 인코딩된 비밀번호가 같으면 true 반환
        return rawPassword.toString().equals(encodedPassword);
    }
}

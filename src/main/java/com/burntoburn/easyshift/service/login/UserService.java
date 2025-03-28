package com.burntoburn.easyshift.service.login;

import com.burntoburn.easyshift.entity.user.Role;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.user.UserRepository;
import com.burntoburn.easyshift.service.login.dto.LoginRequest;
import com.burntoburn.easyshift.service.login.dto.SignUpRequest;
import com.burntoburn.easyshift.service.session.SessionService;
import jakarta.servlet.http.HttpSession;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final NoOpPasswordEncoder passwordEncoder;
    private final SessionService sessionService;

    public User signup(SignUpRequest signUpRequest){
        if (userRepository.existsByName(signUpRequest.getUsername())){
            throw new NoSuchElementException("이미 사용 중인 이름입니다..");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())){
            throw new NoSuchElementException("이미 사용 중인 이메일입니다.");
        }

        String encodePassword = passwordEncoder.encode(signUpRequest.getPassword());


        User signupUsers = User.builder()
                .name(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(encodePassword)
                .phoneNumber(signUpRequest.getPhoneNumber())
                .role(Role.WORKER)
                .build();

        return userRepository.save(signupUsers);
    }

    public User login(LoginRequest loginRequest, HttpSession httpSession) {
        User users = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> {
                    return new NoSuchElementException("이메일이 없음");
                });

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), users.getPassword())) {
            throw new NoSuchElementException("비밀번호가 잘못되었습니다");
        }

        sessionService.saveUserToSession(httpSession, users);
        return users;
    }
}

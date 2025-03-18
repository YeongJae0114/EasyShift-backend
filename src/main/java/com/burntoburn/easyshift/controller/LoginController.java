package com.burntoburn.easyshift.controller;

import com.burntoburn.easyshift.common.response.ApiResponse;
import com.burntoburn.easyshift.common.response.ErrorResponse;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.service.login.UserService;
import com.burntoburn.easyshift.service.login.dto.LoginRequest;
import com.burntoburn.easyshift.service.login.dto.SignUpRequest;
import com.burntoburn.easyshift.session.SessionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;
    private final SessionService sessionService;

    @GetMapping("/api/auth/session")
    public ResponseEntity<ApiResponse<User>> checkSession(HttpSession httpSession){
        User users =  (User) sessionService.getUserFromSession(httpSession);
        if (users == null){
            return ResponseEntity.ok(ApiResponse.fail(new ErrorResponse(401, "세션이 유효하지 않거나 데이터가 없습니다.")));
        }

        return ResponseEntity.ok(ApiResponse.success(users));
    }


    @PostMapping("/api/auth/login")
    public ResponseEntity<ApiResponse<User>> login(@Valid @RequestBody LoginRequest loginRequest, HttpSession httpSession){
        User login = userService.login(loginRequest, httpSession);

        return ResponseEntity.ok(ApiResponse.success(login));
    }

    @PostMapping("/api/auth/signup")
    public ResponseEntity<ApiResponse<User>> signup(@Valid @RequestBody SignUpRequest signUpRequest){
        User signup = userService.signup(signUpRequest);
        return ResponseEntity.ok(ApiResponse.success(signup));
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession httpSession){
        sessionService.removeUserFromSession(httpSession);
        return ResponseEntity.ok(ApiResponse.success());
    }

}

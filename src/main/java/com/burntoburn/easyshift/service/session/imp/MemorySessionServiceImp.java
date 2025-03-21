package com.burntoburn.easyshift.service.session.imp;

import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.service.login.CustomUserDetails;
import com.burntoburn.easyshift.service.session.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MemorySessionServiceImp implements SessionService {
    private static final String USER_SESSION_KEY = "user";
    private static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";


    @Override
    public void saveUserToSession(HttpSession session, Object user) {
        if (!(user instanceof User)) {
            throw new IllegalArgumentException("User 객체만 저장할 수 있습니다.");
        }
        CustomUserDetails userDetails = new CustomUserDetails((User) user);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authToken);

        // 🔥 세션에 SecurityContext 저장 (Spring Security가 자동 관리)
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);
        session.setAttribute(USER_SESSION_KEY, userDetails);

        log.info("{}", session);
    }

    @Override
    public Object getUserFromSession(HttpSession session) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() != null) {
            Object principal = securityContext.getAuthentication().getPrincipal();
            if (principal instanceof CustomUserDetails userDetails) {
                return userDetails.getUser();
            }
        }
        return null;
    }

    @Override
    public void removeUserFromSession(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
    }
}

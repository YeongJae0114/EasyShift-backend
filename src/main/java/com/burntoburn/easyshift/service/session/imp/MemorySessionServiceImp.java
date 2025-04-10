package com.burntoburn.easyshift.service.session.imp;

import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.service.login.CustomUserDetails;
import com.burntoburn.easyshift.service.session.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MemorySessionServiceImp implements SessionService {

    private static final String USER_SESSION_KEY = "user";
    private static final String SPRING_SECURITY_CONTEXT_KEY = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

    @Override
    public void saveUserToSession(HttpSession session, Object user) {
        if (!(user instanceof User)) {
            throw new IllegalArgumentException("User 객체만 저장할 수 있습니다.");
        }

        CustomUserDetails userDetails = new CustomUserDetails((User) user);

        // ✅ Authentication 객체 생성
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // ✅ SecurityContext 새로 생성해서 등록 (핵심!)
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);

        // ✅ 세션에도 저장 (Spring Security가 인식 가능하게)
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);
        session.setAttribute(USER_SESSION_KEY, userDetails);

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

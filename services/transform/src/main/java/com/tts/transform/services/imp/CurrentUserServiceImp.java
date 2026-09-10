package com.tts.transform.services.imp;

import java.util.Collection;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.platform.security.context.AuthenticationContext;
import com.platform.security.model.AuthenticatedUser;
import com.platform.web.exception.SecurityExceptions;
import com.tts.transform.exceptions.BusinessException;
import com.tts.transform.services.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImp implements CurrentUserService {

    private final AuthenticationContext authenticationContext;

    @Override
    public AuthenticatedUser currentUser() {
        return authenticationContext.getCurrentUser()
                .orElseThrow(() -> new BusinessException(SecurityExceptions.FORBIDDEN_ACCESS, "No authenticated user"));
    }

    @Override
    public UUID userId() {
        return UUID.fromString(currentUser().getUserId());
    }

    @Override
    public String username() {
        return currentUser().getUsername();
    }

    @Override
    public String email() {
        return currentUser().getEmail();
    }

    @Override
    public Collection<String> authorities() {
        return currentUser().getAuthorities();
    }

    @Override
    public boolean isAdmin() {
        return currentUser().getAuthorities().contains("ROLE_ADMIN");
    }

    @Override
    public boolean isUser() {
        return currentUser().getAuthorities().contains("ROLE_USER");
    }

}

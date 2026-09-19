package com.tts.transform.services.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.platform.security.context.AuthenticationContext;
import com.platform.security.model.AuthenticatedUser;
import com.platform.web.exception.SecurityExceptions;
import com.tts.transform.exceptions.BusinessException;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceImpTest {

    @Mock
    private AuthenticationContext authenticationContext;

    @Mock
    private AuthenticatedUser authenticatedUser;

    private CurrentUserServiceImp currentUserService;

    private UUID userId;
    private Collection<String> authorities;

    @BeforeEach
    void setUp() {
        currentUserService = new CurrentUserServiceImp(authenticationContext);

        userId = UUID.randomUUID();
        authorities = List.of("ROLE_USER");
    }

    @Test
    void currentUser_shouldReturnAuthenticatedUser() {
        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(authenticatedUser));

        AuthenticatedUser result = currentUserService.currentUser();

        assertSame(authenticatedUser, result);

        verify(authenticationContext).getCurrentUser();
    }

    @Test
    void currentUser_shouldThrowWhenNoAuthenticatedUserExists() {
        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> currentUserService.currentUser());

        assertEquals(
                SecurityExceptions.FORBIDDEN_ACCESS,
                exception.getDefinition());

        verify(authenticationContext).getCurrentUser();
    }

    @Test
    void userId_shouldReturnCurrentUserId() {
        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(authenticatedUser));
        when(authenticatedUser.getUserId())
                .thenReturn(userId.toString());

        assertEquals(userId, currentUserService.userId());

        verify(authenticationContext).getCurrentUser();
        verify(authenticatedUser).getUserId();
    }

    @Test
    void username_shouldReturnCurrentUsername() {
        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(authenticatedUser));
        when(authenticatedUser.getUsername())
                .thenReturn("test-user");

        assertEquals("test-user", currentUserService.username());

        verify(authenticatedUser).getUsername();
    }

    @Test
    void email_shouldReturnCurrentEmail() {
        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(authenticatedUser));
        when(authenticatedUser.getEmail())
                .thenReturn("test@example.com");

        assertEquals(
                "test@example.com",
                currentUserService.email());

        verify(authenticatedUser).getEmail();
    }

    @Test
    void authorities_shouldReturnCurrentAuthorities() {
        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(authenticatedUser));
        when(authenticatedUser.getAuthorities())
                .thenReturn(authorities);

        assertSame(authorities, currentUserService.authorities());

        verify(authenticatedUser).getAuthorities();
    }

    @Test
    void isAdmin_shouldReturnTrueWhenUserHasAdminRole() {
        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(authenticatedUser));
        when(authenticatedUser.getAuthorities())
                .thenReturn(List.of("ROLE_USER", "ROLE_ADMIN"));

        assertTrue(currentUserService.isAdmin());
    }

    @Test
    void isAdmin_shouldReturnFalseWhenUserDoesNotHaveAdminRole() {
        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(authenticatedUser));
        when(authenticatedUser.getAuthorities())
                .thenReturn(authorities);

        assertFalse(currentUserService.isAdmin());
    }

    @Test
    void isUser_shouldReturnTrueWhenUserHasUserRole() {
        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(authenticatedUser));
        when(authenticatedUser.getAuthorities())
                .thenReturn(authorities);

        assertTrue(currentUserService.isUser());
    }

    @Test
    void isUser_shouldReturnFalseWhenUserDoesNotHaveUserRole() {
        when(authenticationContext.getCurrentUser())
                .thenReturn(Optional.of(authenticatedUser));
        when(authenticatedUser.getAuthorities())
                .thenReturn(List.of("ROLE_ADMIN"));

        assertFalse(currentUserService.isUser());
    }
}
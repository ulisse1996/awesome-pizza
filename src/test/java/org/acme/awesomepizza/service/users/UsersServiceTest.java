package org.acme.awesomepizza.service.users;

import org.acme.awesomepizza.data.users.User;
import org.acme.awesomepizza.data.users.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock private UsersRepository usersRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private Authentication authentication;
    @InjectMocks private UsersService usersService;

    @Test
    void should_create_user() {
        assertDoesNotThrow(() -> usersService.createUser("user", "password"));
    }

    @Test
    void should_return_user_with_matched_password() {
        User user = new User();
        user.setPassword("password");
        when(usersRepository.findByUsername("name")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", user.getPassword())).thenReturn(true);
        Optional<User> userByUsernameAndPassword = usersService.getUserByUsernameAndPassword("name", "password");
        assertTrue(userByUsernameAndPassword.isPresent());
    }

    @Test
    void should_return_empty_user_for_wrong_password() {
        User user = new User();
        user.setPassword("p");
        when(usersRepository.findByUsername("name")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", user.getPassword())).thenReturn(false);
        Optional<User> userByUsernameAndPassword = usersService.getUserByUsernameAndPassword("name", "password");
        assertTrue(userByUsernameAndPassword.isEmpty());
    }

    @Test
    void should_return_current_user() {
        try {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            when(authentication.getName()).thenReturn("name");
            when(usersRepository.findByUsername("name")).thenReturn(Optional.of(new User()));
            assertNotNull(usersService.getCurrentUser());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
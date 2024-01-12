package org.acme.awesomepizza.web.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.acme.awesomepizza.data.users.User;
import org.acme.awesomepizza.service.security.JwtService;
import org.acme.awesomepizza.service.users.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private UsersService usersService;
    @Mock private JwtService jwtService;
    @Mock private HttpServletResponse response;
    @InjectMocks private AuthController authController;

    @Test
    void should_return_401_for_invalid_user() {
        authController.login("user", "password");
        verify(response).setStatus(401);
    }

    @Test
    void should_return_200_for_valid_user() {
        User user = new User();
        user.setUsername("user");

        when(usersService.getUserByUsernameAndPassword("user", "password")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("user")).thenReturn("token");
        when(jwtService.generateCookie("token")).thenReturn(new Cookie("awesome-pizza-jwt", "token"));

        authController.login("user", "password");

        verify(response).addHeader("awesome-pizza-jwt", "Bearer token");
        verify(response).addCookie(any(Cookie.class));
    }
}
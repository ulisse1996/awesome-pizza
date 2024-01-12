package org.acme.awesomepizza.service.security;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.util.FieldUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks private JwtService jwtService;

    @BeforeEach
    void setUp() {
        FieldUtils.setProtectedFieldValue("secret", jwtService, "9a4f2c8d3b7a1e6f45c8a0b3f267d8b1d4e6f3c8a9d2b5f8e3a9c8b5f6v8a3d9");
    }

    @Test
    void should_generate_new_token() {
        String token = jwtService.generateToken("user");
        assertNotNull(token);
    }

    @Test
    void should_validate_token() {
        String token = jwtService.generateToken("user");
        Optional<String> subject = jwtService.validateToken(token);
        assertTrue(subject.isPresent());
        assertEquals("user", subject.get());
    }

    @Test
    void should_generate_cookie() {
        Cookie cookie = jwtService.generateCookie("token");
        assertEquals("awesome-pizza-jwt", cookie.getName());
        assertEquals("token", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertEquals(3600, cookie.getMaxAge());
    }
}
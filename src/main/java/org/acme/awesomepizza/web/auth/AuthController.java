package org.acme.awesomepizza.web.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.acme.awesomepizza.data.users.User;
import org.acme.awesomepizza.service.security.JwtService;
import org.acme.awesomepizza.service.users.UsersService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsersService usersService;
    private final JwtService jwtService;
    private final HttpServletResponse response;

    @PostMapping(value = "/login", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void login(@RequestParam("username") String username, @RequestParam("password") String password) {
        Optional<User> user = usersService.getUserByUsernameAndPassword(username, password);
        if (user.isEmpty()) {
            response.setStatus(401);
            return;
        }
        String token = jwtService.generateToken(user.get().getUsername());
        response.addHeader("awesome-pizza-jwt", "Bearer " + token);
        response.addCookie(jwtService.generateCookie(token));
    }
}

package org.acme.awesomepizza.service.users;

import lombok.RequiredArgsConstructor;
import org.acme.awesomepizza.data.users.Role;
import org.acme.awesomepizza.data.users.User;
import org.acme.awesomepizza.data.users.UsersRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createUser(String username, String password) {
        User user = new User();
        user.setEnabled(true);
        user.setRole(Role.ADMIN);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        usersRepository.save(user);
    }

    public Optional<User> getUserByUsernameAndPassword(String username, String password) {
        return usersRepository.findByUsername(username)
                .flatMap(user -> {
                    boolean matchedPassword = passwordEncoder.matches(password, user.getPassword());
                    return matchedPassword ? Optional.of(user) : Optional.empty();
                });
    }

    private User getUserByUsername(String username) {
        return usersRepository.findByUsername(username)
                .orElseThrow();
    }

    public User getCurrentUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserByUsername(name);
    }
}

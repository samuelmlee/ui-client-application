package platform.codingnomads.co.uiclientapplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import platform.codingnomads.co.uiclientapplication.exception.UserAlreadyExistsException;
import platform.codingnomads.co.uiclientapplication.model.Role;
import platform.codingnomads.co.uiclientapplication.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserServiceClient userServiceClient;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userServiceClient.fetchUserByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username : {}" + username);
        }

        List<GrantedAuthority> authorities = optionalUser.get().getAuthorities().stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList());

        return optionalUser.map(user -> new org.springframework.security.core.userdetails.User(
                username, user.getPassword(),
                true, true, true, true, authorities)).orElse(null);
    }


    public User createNewUser(User user) throws UserAlreadyExistsException {

        checkUsername(user);

        checkPassword(user.getPassword());

        User newUser = User.builder()
                .id(null)
                .username(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .email(user.getEmail())
                .fullName(user.getFullName())
                .authorities(Collections.singletonList(Role.ROLE_USER))
                .build();

        try {
            return userServiceClient.createNewUser(newUser).orElse(null);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    private void checkUsername(User user) throws UserAlreadyExistsException {
        Optional<User> optionalUser = userServiceClient.fetchUserByUsername(user.getUsername());

        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("User already exists for username : " + user.getUsername());
        }
    }

    private void checkPassword(String password) {
        if (password == null) {
            throw new IllegalStateException("You must set a password");
        }
        if (password.length() < 4) {
            throw new IllegalStateException("Password is too short. Must be longer than 4 characters");
        }
    }
}

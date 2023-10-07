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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserServiceClient userServiceClient;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userServiceClient.fetchUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username : {}" + username);
        }

        List<GrantedAuthority> authorities = user.getAuthorities().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                username, user.getPassword(),
                true, true, true, true, authorities);
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
                .authorities(Collections.singletonList(Role.builder().role("ROLE_USER").build()))
                .build();

        try {
            return userServiceClient.createNewUser(newUser);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    private void checkUsername(User user) throws UserAlreadyExistsException {
        User userFound = userServiceClient.fetchUserByUsername(user.getUsername());

        if (userFound == null) {
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

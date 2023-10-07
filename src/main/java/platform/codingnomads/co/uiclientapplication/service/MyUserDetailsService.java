package platform.codingnomads.co.uiclientapplication.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import platform.codingnomads.co.uiclientapplication.exception.UserAlreadyExistsException;
import platform.codingnomads.co.uiclientapplication.exception.UserCreationFailedException;
import platform.codingnomads.co.uiclientapplication.model.CustomUserDetails;
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
    private final Logger LOGGER = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userServiceClient.fetchUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username : {}" + username);
        }

        List<GrantedAuthority> authorities = user.getAuthorities().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());

        return CustomUserDetails.builder().id(user.getId()).username(user.getUsername()).password(user.getPassword()).authorities(authorities).build();
    }


    public void createNewUser(User user) throws UserAlreadyExistsException,
            RestClientException, UserCreationFailedException {

        if (checkIsExistingUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Existing User found for username : " + user.getUsername());
        }

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
            userServiceClient.createNewUser(newUser);
        } catch (RestClientException | UserCreationFailedException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    private boolean checkIsExistingUsername(String username) throws UserAlreadyExistsException {
        try {
            User userFound = userServiceClient.fetchUserByUsername(username);
            return userFound != null;

        } catch (UsernameNotFoundException e) {
            return false;
        } catch (RestClientException e) {
            LOGGER.error("Error checking for the existence of user with username: {}. Error: {}", username, e.getMessage());
            throw new RuntimeException("An error occurred while checking the username: " + e.getMessage());
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

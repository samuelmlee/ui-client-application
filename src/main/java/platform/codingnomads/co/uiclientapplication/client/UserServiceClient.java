package platform.codingnomads.co.uiclientapplication.client;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import platform.codingnomads.co.uiclientapplication.exception.UserCreationFailedException;
import platform.codingnomads.co.uiclientapplication.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    @LoadBalanced
    private final RestTemplate restTemplate;

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceClient.class);

    private static final String USER_MICROSERVICE_URL = "http://USER-MICROSERVICE/user";


    public User fetchUserByUsername(String username) throws RestClientException, UsernameNotFoundException {
        try {
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("username", username);

            ResponseEntity<User> response = restTemplate.getForEntity(
                    USER_MICROSERVICE_URL + "/username/{username}", User.class, uriVariables);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new UsernameNotFoundException("User not found with username : " + username);
            }
            return response.getBody();

        } catch (RestClientException e) {
            LOGGER.error("Error fetching user by username : {}, {}", username, e.getMessage());
            throw e;
        }
    }

    public void createNewUser(User user) throws RestClientException, UserCreationFailedException {
        try {
            ResponseEntity<User> response = restTemplate.postForEntity(
                    USER_MICROSERVICE_URL + "/user", user, User.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new UserCreationFailedException("User creation failed for User : " + user);
            }

        } catch (RestClientException e) {
            LOGGER.error("Error while creating user: {}", e.getMessage());
            throw e;
        }
    }
}

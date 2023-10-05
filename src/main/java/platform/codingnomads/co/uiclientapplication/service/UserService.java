package platform.codingnomads.co.uiclientapplication.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import platform.codingnomads.co.uiclientapplication.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @LoadBalanced
    private final RestTemplate restTemplate;

    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private static final String USER_MICROSERVICE_URL = "http://USER-MICROSERVICE";

    public Optional<User> fetchUserByUsername(String username) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("username", username);
        try {

            ResponseEntity<User> response = restTemplate.getForEntity(
                    USER_MICROSERVICE_URL + "/user/username/{username}", User.class, uriVariables);
            if (response.getStatusCode().is2xxSuccessful()) {
                return Optional.ofNullable(response.getBody());
            }
        } catch (ResourceAccessException e) {
            LOGGER.error("Network error while fetching User with username {} : {}", username, e.getMessage());
        } catch (RestClientException e) {
            LOGGER.error("Error while fetching User with username {} : {} : ", username, e.getMessage());
        }
        return Optional.empty();
    }
}

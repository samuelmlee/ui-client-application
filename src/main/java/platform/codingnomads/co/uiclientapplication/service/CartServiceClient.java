package platform.codingnomads.co.uiclientapplication.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import platform.codingnomads.co.uiclientapplication.exception.CartAddItemFailedException;
import platform.codingnomads.co.uiclientapplication.model.Cart;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartServiceClient {


    @LoadBalanced
    private final RestTemplate restTemplate;

    private final Logger LOGGER = LoggerFactory.getLogger(CartServiceClient.class);

    private final String CART_SERVICE_URL = "http://CART-MICROSERVICE";

    public Cart addNewCartItem(Long itemId, Long userId) throws CartAddItemFailedException, RestClientException {
        try {
            Map<String, Long> uriVariables = new HashMap<>();
            uriVariables.put("userId", userId);
            uriVariables.put("item-id", itemId);

            ResponseEntity<Cart> response = restTemplate.postForEntity(CART_SERVICE_URL + "/cart/{userId}?item-id={item-id}",
                    null, Cart.class, uriVariables);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new CartAddItemFailedException("Failed to add item to cart with itemId : " + itemId + " and userId : " + userId);
            }
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.error("Error sending request to add item with id : {},  userId {}", itemId, e.getMessage());
            throw e;
        }
    }


}
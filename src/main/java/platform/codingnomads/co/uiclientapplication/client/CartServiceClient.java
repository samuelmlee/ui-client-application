package platform.codingnomads.co.uiclientapplication.client;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import platform.codingnomads.co.uiclientapplication.exception.CartAddItemFailedException;
import platform.codingnomads.co.uiclientapplication.exception.CartItemAmountUpdateException;
import platform.codingnomads.co.uiclientapplication.exception.CartNotFoundException;
import platform.codingnomads.co.uiclientapplication.exception.CartRemoveItemException;
import platform.codingnomads.co.uiclientapplication.model.Cart;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CartServiceClient {


    @LoadBalanced
    private final RestTemplate restTemplate;

    private final Logger LOGGER = LoggerFactory.getLogger(CartServiceClient.class);

    private final String CART_SERVICE_URL = "http://CART-MICROSERVICE/cart";

    public Cart addNewCartItem(Long itemId, Long userId) throws CartAddItemFailedException, RestClientException {
        try {
            Map<String, Long> uriVariables = new HashMap<>();
            uriVariables.put("userId", userId);
            uriVariables.put("itemId", itemId);

            ResponseEntity<Cart> response = restTemplate.postForEntity(CART_SERVICE_URL + "/{userId}?item-id={itemId}",
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

    public Cart removeCartItem(Long itemId, Long userId) throws RestClientException, CartRemoveItemException {
        try {
            Map<String, Long> uriVariables = new HashMap<>();
            uriVariables.put("itemId", itemId);
            uriVariables.put("userId", userId);

            ResponseEntity<Cart> response = restTemplate.exchange(
                    CART_SERVICE_URL + "/{userId}/{itemId}",
                    HttpMethod.DELETE,
                    null,
                    Cart.class,
                    uriVariables);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new CartRemoveItemException(String.format("Removing cart item id : %d for userid : %s failed", itemId, userId));
            }

            return response.getBody();

        } catch (RestClientException e) {
            LOGGER.error("Technical error removing cart item with id {} , for userId : {}", itemId, userId);
            throw e;
        }
    }

    public Cart changeItemAmount(Long userId, Long itemId, Integer amountChange) throws CartItemAmountUpdateException, RestClientException {
        try {
            Map<String, Object> uriVariables = new HashMap<>();
            uriVariables.put("userId", userId);
            uriVariables.put("itemId", itemId);
            uriVariables.put("amountChange", amountChange);

            ResponseEntity<Cart> response = restTemplate.exchange(
                    CART_SERVICE_URL + "/{userId}/{itemId}?amount-change={amountChange}",
                    HttpMethod.PATCH,
                    null,
                    Cart.class,
                    uriVariables);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new CartItemAmountUpdateException(String.format("Updating cart item : %s for userid : %s for the amount : %d failed", itemId, userId, amountChange));
            }
            return response.getBody();

        } catch (RestClientException e) {
            LOGGER.error("Error updating item : {} for userId : {} with amount : {}", itemId, userId, amountChange, e);
            throw e;
        }
    }

    public Cart fetchCartByUserId(Long userId) throws CartNotFoundException, RestClientException {
        try {
            Map<String, Long> uriVariables = new HashMap<>();
            uriVariables.put("userId", userId);

            ResponseEntity<Cart> response = restTemplate.getForEntity(CART_SERVICE_URL + "/{userId}", Cart.class, uriVariables);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new CartNotFoundException("Error fetching cart for userId :" + userId);
            }
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.error("Error sending request to fetch cart for userId : {} , {}", userId, e.getMessage());
            throw e;
        }
    }


    public int fetchTotalItemsForUser(Long userId) throws RestClientException {
        try {
            Map<String, Long> uniVariables = new HashMap<>();
            uniVariables.put("userId", userId);

            ResponseEntity<Integer> response = restTemplate.getForEntity(CART_SERVICE_URL + "/{userId}/items-count", Integer.class, uniVariables);

            if (response.getBody() == null) {
                LOGGER.warn("Received null body in response for userId: {}", userId);
                return 0;
            }
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.error("Error getting total items count in cart for userId : {}, {}", userId, e.getMessage());
            throw e;
        }
    }


}

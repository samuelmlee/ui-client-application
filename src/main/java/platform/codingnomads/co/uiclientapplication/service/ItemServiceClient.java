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
import platform.codingnomads.co.uiclientapplication.model.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceClient {

    @LoadBalanced
    private final RestTemplate restTemplate;

    private final Logger LOGGER = LoggerFactory.getLogger(ItemServiceClient.class);

    private static final String ITEM_MICROSERVICE_URL = "http://ITEM-MICROSERVICE/item";

    public List<Item> fetchAllItems() {
        try {
            ResponseEntity<Item[]> response = restTemplate.getForEntity(
                    ITEM_MICROSERVICE_URL, Item[].class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            }
        } catch (ResourceAccessException e) {
            LOGGER.error("Network error while fetching all items : " + e.getMessage());
        } catch (RestClientException e) {
            LOGGER.error("Error while fetching all items : " + e.getMessage());
        }
        return new ArrayList<>();
    }
}

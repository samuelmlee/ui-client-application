package platform.codingnomads.co.uiclientapplication.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import platform.codingnomads.co.uiclientapplication.exception.ItemFetchingException;
import platform.codingnomads.co.uiclientapplication.model.BatchItemRequest;
import platform.codingnomads.co.uiclientapplication.model.Item;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemServiceClient {

    @LoadBalanced
    private final RestTemplate restTemplate;

    private final Logger LOGGER = LoggerFactory.getLogger(ItemServiceClient.class);

    private static final String ITEM_MICROSERVICE_URL = "http://ITEM-MICROSERVICE/item";

    public List<Item> fetchAllItems() throws ItemFetchingException, RestClientException {
        try {
            ResponseEntity<Item[]> response = restTemplate.getForEntity(
                    ITEM_MICROSERVICE_URL, Item[].class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new ItemFetchingException("Failed to load all items from service");
            }
            return Arrays.asList(response.getBody());
        } catch (RestClientException e) {
            LOGGER.error("Error while fetching all items", e);
            throw e;
        }
    }

    public Item fetchItemById(Long itemId) throws ItemFetchingException {
        try {
            Map<String, Long> uriVariables = new HashMap<>();
            uriVariables.put("itemId", itemId);
            ResponseEntity<Item> response = restTemplate.getForEntity(ITEM_MICROSERVICE_URL + "/{itemId}",
                    Item.class, uriVariables);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new ItemFetchingException("Failed to get item for item id : " + itemId);
            }
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.error("Error while fetching item with itemId : {}", itemId, e);
            throw new ItemFetchingException("Technical failure to get item for item id : " + itemId);
        }
    }

    public Map<Long, Item> fetchItemsByIdList(List<Long> itemIds) throws ItemFetchingException {
        try {
            BatchItemRequest batchItemRequest = new BatchItemRequest(itemIds);
            HttpEntity<BatchItemRequest> requestEntity = new HttpEntity<>(batchItemRequest);

            ResponseEntity<Map<Long, Item>> response = restTemplate.exchange(
                    ITEM_MICROSERVICE_URL + "/batch",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {
                    }
            );
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new ItemFetchingException("Failed to get item for item ids : " + itemIds);
            }
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.error("Error while fetching items with itemIds : {}", itemIds, e);
            throw new ItemFetchingException("Technical failure to get item for item ids : " + itemIds);
        }
    }
}

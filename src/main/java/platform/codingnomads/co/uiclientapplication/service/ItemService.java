package platform.codingnomads.co.uiclientapplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import platform.codingnomads.co.uiclientapplication.model.Item;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    @LoadBalanced
    private final RestTemplate restTemplate;

    public List<Item> getAllItems() {
        ResponseEntity<Item[]> response = restTemplate.getForEntity("http://ITEM-MICROSERVICE/item", Item[].class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return Arrays.asList(response.getBody());
        } else {
            return null;
        }
    }
}

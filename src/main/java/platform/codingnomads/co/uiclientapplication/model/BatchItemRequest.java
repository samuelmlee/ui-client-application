package platform.codingnomads.co.uiclientapplication.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchItemRequest {
    private List<Long> itemIds;
}
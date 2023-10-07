package platform.codingnomads.co.uiclientapplication.model;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {

    private Long id;

    private Long itemId;

    private Integer amount;
}

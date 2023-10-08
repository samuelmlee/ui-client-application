package platform.codingnomads.co.uiclientapplication.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    private Long id;

    private Long userId;

    private List<CartItem> items;

    public static int getCartItemsCount(Cart cart) {
        return cart.getItems().stream().mapToInt(CartItem::getAmount).sum();
    }
}

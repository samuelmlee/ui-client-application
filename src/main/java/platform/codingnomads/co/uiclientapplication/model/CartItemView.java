package platform.codingnomads.co.uiclientapplication.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemView {
    private CartItem cartItem;
    private String name;
    private String description;

    public static int getCartItemsViewsCount(List<CartItemView> cartItemViews) {
        return cartItemViews.stream().map(CartItemView::getCartItem).mapToInt(CartItem::getAmount).sum();
    }
}

package platform.codingnomads.co.uiclientapplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.codingnomads.co.uiclientapplication.client.CartServiceClient;
import platform.codingnomads.co.uiclientapplication.client.ItemServiceClient;
import platform.codingnomads.co.uiclientapplication.exception.CartNotFoundException;
import platform.codingnomads.co.uiclientapplication.exception.ItemFetchingException;
import platform.codingnomads.co.uiclientapplication.model.Cart;
import platform.codingnomads.co.uiclientapplication.model.CartItem;
import platform.codingnomads.co.uiclientapplication.model.CartItemView;
import platform.codingnomads.co.uiclientapplication.model.Item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartServiceClient cartServiceClient;

    private final ItemServiceClient itemServiceClient;

    public List<CartItemView> getUserCartItems(Long userId) throws CartNotFoundException, ItemFetchingException {
        Cart cart = cartServiceClient.fetchCartByUserId(userId);
        List<CartItem> cartItems = cart.getItems();

        List<Long> itemIds = cartItems.stream().map(CartItem::getItemId).collect(Collectors.toList());

        Map<Long, Item> itemDetails = itemServiceClient.fetchItemsByIdList(itemIds);

        return cartItems.stream().map(item -> {
            Item itemDetail = itemDetails.get(item.getItemId());

            return CartItemView.builder()
                    .cartItem(item)
                    .name(itemDetail.getName())
                    .description(itemDetail.getDescription())
                    .build();

        }).collect(Collectors.toList());
    }


}

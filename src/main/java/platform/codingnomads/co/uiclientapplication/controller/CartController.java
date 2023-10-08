package platform.codingnomads.co.uiclientapplication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import platform.codingnomads.co.uiclientapplication.client.CartServiceClient;
import platform.codingnomads.co.uiclientapplication.exception.CartAddItemFailedException;
import platform.codingnomads.co.uiclientapplication.exception.CartNotFoundException;
import platform.codingnomads.co.uiclientapplication.exception.ItemFetchingException;
import platform.codingnomads.co.uiclientapplication.model.CartItemView;
import platform.codingnomads.co.uiclientapplication.model.CustomUserDetails;
import platform.codingnomads.co.uiclientapplication.service.CartService;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartServiceClient cartServiceClient;

    private final CartService cartService;

    @PostMapping("/{itemId}")
    public String addToCart(@PathVariable("itemId") Long itemId, Authentication authentication, Model model) {
        try {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            cartServiceClient.addNewCartItem(itemId, userId);

        } catch (CartAddItemFailedException | RestClientException e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }

        return "redirect:/item-list";
    }

    @GetMapping
    public String showCart(Authentication authentication, Model model) {
        try {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            List<CartItemView> cartItems = cartService.getUserCartItems(userId);

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("cartItemsCount", CartItemView.getCartItemsViewsCount(cartItems));

        } catch (CartNotFoundException | ItemFetchingException e) {
            model.addAttribute("message", "Error fetching user cart");
        }
        return "cart";
    }
}

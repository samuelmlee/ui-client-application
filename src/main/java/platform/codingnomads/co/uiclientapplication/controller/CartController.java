package platform.codingnomads.co.uiclientapplication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import platform.codingnomads.co.uiclientapplication.exception.*;
import platform.codingnomads.co.uiclientapplication.model.CartItemView;
import platform.codingnomads.co.uiclientapplication.model.CustomUserDetails;
import platform.codingnomads.co.uiclientapplication.service.CartService;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

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

    @PostMapping("/add/{itemId}")
    public String addItemToCart(@PathVariable("itemId") Long itemId,
                                Authentication authentication,
                                Model model) {
        try {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();

            List<CartItemView> cartItems = cartService.addNewCartItem(itemId, userId);

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("cartItemsCount", CartItemView.getCartItemsViewsCount(cartItems));

        } catch (CartAddItemFailedException | ItemFetchingException | RestClientException e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }

        return "item-list";
    }

    @PostMapping("/remove/{itemId}")
    public String removeItemFromCart(@PathVariable("itemId") Long itemId, Authentication authentication, Model model) {
        try {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();

            List<CartItemView> cartItems = cartService.removeCartItem(itemId, userId);

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("cartItemsCount", CartItemView.getCartItemsViewsCount(cartItems));

        } catch (CartRemoveItemException | ItemFetchingException | RestClientException e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }


        return "cart";
    }

    @PostMapping("/update-amount/{itemId}")
    public String updateCartItemAmount(@PathVariable("itemId") Long itemId, @RequestParam(name = "amount-update") Integer amountUpdate,
                                       Authentication authentication,
                                       Model model) {
        try {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();

            List<CartItemView> cartItems = cartService.changeItemAmount(userId, itemId, amountUpdate);

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("cartItemsCount", CartItemView.getCartItemsViewsCount(cartItems));

        } catch (CartItemAmountUpdateException | ItemFetchingException | RestClientException e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }

        return "cart";
    }
}

package platform.codingnomads.co.uiclientapplication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import platform.codingnomads.co.uiclientapplication.client.CartServiceClient;
import platform.codingnomads.co.uiclientapplication.client.ItemServiceClient;
import platform.codingnomads.co.uiclientapplication.exception.ItemFetchingException;
import platform.codingnomads.co.uiclientapplication.model.CustomUserDetails;

import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemServiceClient itemServiceClient;

    private final CartServiceClient cartServiceClient;

    @GetMapping("/item-list")
    public String showItems(Authentication authentication, Model model) {
        try {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            int cartItemsCount = cartServiceClient.fetchTotalItemsForUser(userId);

            model.addAttribute("cartItemsCount", cartItemsCount);
            model.addAttribute("items", itemServiceClient.fetchAllItems());
        } catch (ItemFetchingException | RuntimeException e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("items", new ArrayList<>());

        }
        return "item-list";
    }
}

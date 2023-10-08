package platform.codingnomads.co.uiclientapplication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClientException;
import platform.codingnomads.co.uiclientapplication.exception.CartAddItemFailedException;
import platform.codingnomads.co.uiclientapplication.exception.UserAlreadyExistsException;
import platform.codingnomads.co.uiclientapplication.exception.UserCreationFailedException;
import platform.codingnomads.co.uiclientapplication.model.CustomUserDetails;
import platform.codingnomads.co.uiclientapplication.model.User;
import platform.codingnomads.co.uiclientapplication.service.CartServiceClient;
import platform.codingnomads.co.uiclientapplication.service.ItemServiceClient;
import platform.codingnomads.co.uiclientapplication.service.MyUserDetailsService;


@Controller
@RequiredArgsConstructor
public class ViewController {


    private final ItemServiceClient itemServiceClient;

    private final CartServiceClient cartServiceClient;

    private final MyUserDetailsService myUserDetailsService;

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterUser(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String createNewUser(User user, Model model) {
        try {
            myUserDetailsService.createNewUser(user);
        } catch (UserAlreadyExistsException |
                 RestClientException | UserCreationFailedException e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
        return "redirect:/login";
    }

    @GetMapping("/item-list")
    public String showItems(Authentication authentication, Model model) {
        try {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            int cartItemsCount = cartServiceClient.fetchTotalItemsForUser(userId);

            model.addAttribute("cartItemsCount", cartItemsCount);
        } catch (RestClientException ignored) {
        }
        model.addAttribute("items", itemServiceClient.fetchAllItems());

        return "item-list";
    }

    @PostMapping("/cart/{itemId}")
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


}

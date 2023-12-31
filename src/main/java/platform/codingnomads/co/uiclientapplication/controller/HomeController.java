package platform.codingnomads.co.uiclientapplication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClientException;
import platform.codingnomads.co.uiclientapplication.exception.UserAlreadyExistsException;
import platform.codingnomads.co.uiclientapplication.exception.UserCreationFailedException;
import platform.codingnomads.co.uiclientapplication.model.User;
import platform.codingnomads.co.uiclientapplication.service.MyUserDetailsService;


@Controller
@RequiredArgsConstructor
public class HomeController {


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


}

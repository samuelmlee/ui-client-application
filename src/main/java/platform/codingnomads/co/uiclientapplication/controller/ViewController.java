package platform.codingnomads.co.uiclientapplication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import platform.codingnomads.co.uiclientapplication.service.ItemService;

@Controller
@RequiredArgsConstructor
public class ViewController {


    private final ItemService itemService;

    @GetMapping("/items")
    public String displayItems(Model model) {
        model.addAttribute("items", itemService.getAllItems());
        return "item-list";
    }
}

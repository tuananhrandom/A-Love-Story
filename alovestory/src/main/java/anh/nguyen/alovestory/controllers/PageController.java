package anh.nguyen.alovestory.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
@RequestMapping("")
public class PageController {
    @GetMapping("/home")
    public String getHomePage() {
        return "home";
    }
    
}

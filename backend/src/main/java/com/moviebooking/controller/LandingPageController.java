package com.moviebooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

/**
 * LandingPageController - Handles initial landing page and redirects
 *
 * ALL users must login/register first before accessing the application
 */
@Controller
public class LandingPageController {

    /**
     * Root path - Redirect to home if authenticated, else to login
     */
    @GetMapping("/")
    public String root(HttpSession session) {
        Long customerId = (Long) session.getAttribute("customerId");
        Long adminId = (Long) session.getAttribute("adminId");

        // If authenticated, go to home
        if (customerId != null || adminId != null) {
            return "redirect:/home";
        }

        // Otherwise show login
        return "redirect:/login-page";
    }

    /**
     * Show login page - First page users see
     */
    @GetMapping("/login-page")
    public String showLoginPage(Model model,
                               @RequestParam(required = false) String error) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "login";
    }
}

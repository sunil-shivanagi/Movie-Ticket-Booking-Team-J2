package com.moviebooking.controller;

import com.moviebooking.model.Admin;
import com.moviebooking.service.UserService;
import com.moviebooking.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

/**
 * AdminAuthController - Handles admin authentication
 *
 * This controller manages:
 * - Admin login page display
 * - Admin credential verification
 * - Admin session management
 * - Admin logout
 *
 * All admin actions are logged to the audit table via AuditLogService
 */
@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuditLogService auditLogService;

    /**
     * Show admin login page
     */
    @GetMapping("/login-page")
    public String showAdminLoginPage(Model model,
                                    @RequestParam(required = false) String error) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "admin-login";
    }

    /**
     * Handle admin login
     *
     * Verifies admin credentials and creates admin session
     * Logs the login attempt to audit table
     */
    @PostMapping("/login")
    public String adminLogin(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            // Verify admin credentials
            Admin admin = userService.loginAdmin(email, password);

            if (admin == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid admin credentials!");
                return "redirect:/admin/login-page";
            }

            // Create admin session
            session.setAttribute("adminId", admin.getId());
            session.setAttribute("adminName", admin.getName());
            session.setAttribute("adminEmail", admin.getEmail());
            session.setAttribute("userType", "ADMIN");

            // Log successful login
            auditLogService.logLogin("ADMIN", admin.getId(), email);

            redirectAttributes.addFlashAttribute("success",
                    "Welcome, " + admin.getName() + "!");
            return "redirect:/admin";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Login failed: " + e.getMessage());
            return "redirect:/admin/login-page";
        }
    }

    /**
     * Handle admin logout
     *
     * Clears admin session and logs the logout action
     */
    @GetMapping("/logout")
    public String adminLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        Long adminId = (Long) session.getAttribute("adminId");

        if (adminId != null) {
            // Log the logout action
            auditLogService.logLogout("ADMIN", adminId);
        }

        // Invalidate session
        session.invalidate();

        redirectAttributes.addFlashAttribute("success",
                "You have been logged out successfully!");
        return "redirect:/login-page";
    }
}

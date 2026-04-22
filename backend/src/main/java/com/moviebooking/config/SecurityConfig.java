package com.moviebooking.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Security Configuration - Implements authentication and authorization
 *
 * This intercepts requests to protected URLs and checks for:
 * - Admin routes: require admin authentication
 * - Customer routes that need login
 */
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Admin authentication interceptor
        registry.addInterceptor(new AdminAuthInterceptor())
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login", "/admin/login-page");

        // Customer authentication interceptor (for all customer routes)
        registry.addInterceptor(new CustomerAuthInterceptor())
                .addPathPatterns("/movies", "/movie/**", "/show/**", "/booking/**", "/my-bookings", "/home")
                .excludePathPatterns("/login", "/register", "/logout", "/login-page");
    }

    /**
     * Interceptor for admin routes
     */
    public class AdminAuthInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            HttpSession session = request.getSession();
            Long adminId = (Long) session.getAttribute("adminId");

            if (adminId == null) {
                // Not logged in as admin, redirect to admin login
                response.sendRedirect("/admin/login-page?error=Please login as admin");
                return false;
            }

            return true;
        }
    }

    /**
     * Interceptor for customer protected routes
     */
    public class CustomerAuthInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            HttpSession session = request.getSession();
            Long customerId = (Long) session.getAttribute("customerId");

            if (customerId == null) {
                // Not logged in as customer, redirect to home with error
                response.sendRedirect("/?error=Please login first");
                return false;
            }

            return true;
        }
    }
}

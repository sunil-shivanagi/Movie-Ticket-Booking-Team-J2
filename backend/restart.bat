@echo off
REM ============================================================================
REM Movie Ticket Booking System - Quick Restart Script
REM ============================================================================
echo.
echo ========================================
echo  Movie Ticket Booking System
echo  Quick Restart Script
echo ========================================
echo.

cd /d c:\Users\Admin\Desktop\6th\OOAD\mini_project\movie-ticket-booking

echo [1/3] Cleaning old build artifacts...
call mvn clean
echo.

echo [2/3] Compiling project...
call mvn compile
echo.

echo [3/3] Starting Spring Boot application...
echo.
echo ========================================
echo  Application Starting...
echo  URL: http://localhost:8080
echo  Admin: admin@moviebooking.com / admin@123
echo ========================================
echo.
call mvn spring-boot:run

pause

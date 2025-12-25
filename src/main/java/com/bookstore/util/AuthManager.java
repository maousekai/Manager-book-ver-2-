package com.bookstore.util;

public class AuthManager {
    private static String currentUserId;
    private static int currentRole;
    private static long lastActivityTime = System.currentTimeMillis();

    public static void login(String userId, int role) {
        currentUserId = userId;
        currentRole = role;
        updateActivity();
    }

    public static void logout() {
        currentUserId = null;
        currentRole = 0; 
        lastActivityTime = 0;
    }

    public static boolean isLoggedIn() { // Kiểm tra có còn đăng nhập
        if (System.currentTimeMillis() - lastActivityTime > 30 * 60 * 1000) { 
            logout();
            return false;
        }
        return currentUserId != null;
    }

    public static void updateActivity() {
        lastActivityTime = System.currentTimeMillis();
    }

    public static String getCurrentUserId() { return currentUserId; }
    public static int getCurrentRole() { return currentRole; }
}
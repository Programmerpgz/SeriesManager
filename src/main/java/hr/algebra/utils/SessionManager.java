package hr.algebra.utils;

import hr.algebra.models.User;

public class SessionManager {
    private static User currentUser;
    private SessionManager() {}
    public static void login(User user) {
        currentUser = user;
    }
    public static void logout() {
        currentUser = null;
    }
    public static User getCurrentUser() {
        return currentUser;
    }
}

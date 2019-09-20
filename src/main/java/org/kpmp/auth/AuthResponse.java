package org.kpmp.auth;

public class AuthResponse {
    private String token;
    private UserAuth user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserAuth getUser() {
        return user;
    }

    public void setUser(UserAuth user) {
        this.user = user;
    }
}

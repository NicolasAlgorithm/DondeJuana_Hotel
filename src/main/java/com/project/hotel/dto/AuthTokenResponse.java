package com.project.hotel.dto;

public class AuthTokenResponse {

    private String tokenType;
    private String accessToken;
    private long expiresInSeconds;

    public AuthTokenResponse(String tokenType, String accessToken, long expiresInSeconds) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }
}

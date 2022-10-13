package com.clankalliance.backbeta.request;

public class CommonDeleteRequest {

    private String token;

    private long id;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CommonDeleteRequest{" +
                "token='" + token + '\'' +
                ", id=" + id +
                '}';
    }
}

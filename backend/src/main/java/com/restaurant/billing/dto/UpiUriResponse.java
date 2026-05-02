package com.restaurant.billing.dto;

public class UpiUriResponse {

    private String upiUri;

    public UpiUriResponse() {}

    public UpiUriResponse(String upiUri) {
        this.upiUri = upiUri;
    }

    public String getUpiUri() {
        return upiUri;
    }

    public void setUpiUri(String upiUri) {
        this.upiUri = upiUri;
    }
}

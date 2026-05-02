package com.restaurant.billing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CheckoutRequest {

    @NotEmpty
    @Valid
    private List<CheckoutLineRequest> lines;

    public List<CheckoutLineRequest> getLines() {
        return lines;
    }

    public void setLines(List<CheckoutLineRequest> lines) {
        this.lines = lines;
    }
}

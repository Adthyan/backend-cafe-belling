package com.restaurant.billing.service;

import com.restaurant.billing.entity.ShopSettings;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;

@Service
public class UpiPaymentService {

    public String buildUpiUri(ShopSettings settings, BigDecimal amountInInr, String transactionNote) {
        String pa = encode(settings.getMerchantVpa());
        String pn = encode(settings.getMerchantName());
        String am = amountInInr.setScale(2, RoundingMode.HALF_UP).toPlainString();
        String cu = encode(settings.getCurrency() != null ? settings.getCurrency() : "INR");
        String tn = encode(transactionNote != null ? transactionNote : "");
        return "upi://pay?pa="
                + pa
                + "&pn="
                + pn
                + "&am="
                + am
                + "&cu="
                + cu
                + "&tn="
                + tn;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}

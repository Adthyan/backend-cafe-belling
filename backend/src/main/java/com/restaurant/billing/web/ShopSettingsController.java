package com.restaurant.billing.web;

import com.restaurant.billing.dto.ShopSettingsDto;
import com.restaurant.billing.service.ShopSettingsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class ShopSettingsController {

    private final ShopSettingsService shopSettingsService;

    public ShopSettingsController(ShopSettingsService shopSettingsService) {
        this.shopSettingsService = shopSettingsService;
    }

    @GetMapping
    public ShopSettingsDto get() {
        return shopSettingsService.getDto();
    }

    @PatchMapping
    public ShopSettingsDto patch(@Valid @RequestBody ShopSettingsDto body) {
        return shopSettingsService.update(body);
    }
}

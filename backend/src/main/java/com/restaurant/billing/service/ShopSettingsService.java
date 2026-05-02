package com.restaurant.billing.service;

import com.restaurant.billing.dto.ShopSettingsDto;
import com.restaurant.billing.entity.ShopSettings;
import com.restaurant.billing.repository.ShopSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopSettingsService {

    private final ShopSettingsRepository repository;

    public ShopSettingsService(ShopSettingsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public ShopSettings getOrCreate() {
        return repository.findById(1L).orElseGet(this::createDefault);
    }

    private ShopSettings createDefault() {
        ShopSettings s = new ShopSettings();
        return repository.save(s);
    }

    @Transactional(readOnly = true)
    public ShopSettingsDto getDto() {
        ShopSettings s = getOrCreate();
        ShopSettingsDto dto = new ShopSettingsDto();
        dto.setMerchantVpa(s.getMerchantVpa());
        dto.setMerchantName(s.getMerchantName());
        dto.setCurrency(s.getCurrency());
        return dto;
    }

    @Transactional
    public ShopSettingsDto update(ShopSettingsDto dto) {
        ShopSettings s = getOrCreate();
        s.setMerchantVpa(dto.getMerchantVpa().trim());
        s.setMerchantName(dto.getMerchantName().trim());
        if (dto.getCurrency() != null && !dto.getCurrency().isBlank()) {
            s.setCurrency(dto.getCurrency().trim().toUpperCase());
        }
        repository.save(s);
        return getDto();
    }
}

package com.restaurant.billing.repository;

import com.restaurant.billing.entity.ShopSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopSettingsRepository extends JpaRepository<ShopSettings, Long> {
}

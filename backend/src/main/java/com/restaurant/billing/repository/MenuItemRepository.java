package com.restaurant.billing.repository;

import com.restaurant.billing.entity.MenuItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByActiveTrueOrderBySortOrderAsc();
}

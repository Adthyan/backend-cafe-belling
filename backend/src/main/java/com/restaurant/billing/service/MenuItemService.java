package com.restaurant.billing.service;

import com.restaurant.billing.dto.MenuItemDto;
import com.restaurant.billing.dto.MenuItemWriteDto;
import com.restaurant.billing.entity.MenuItem;
import com.restaurant.billing.repository.MenuItemRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MenuItemService {

    private final MenuItemRepository repository;

    public MenuItemService(MenuItemRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MenuItemDto> findActive() {
        return repository.findByActiveTrueOrderBySortOrderAsc().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<MenuItemDto> findAll() {
        return repository.findAll().stream().sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder())).map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public MenuItemDto findById(Long id) {
        return repository.findById(id).map(this::toDto).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public MenuItemDto create(MenuItemWriteDto w) {
        MenuItem e = fromWrite(w);
        e.setCreatedAt(Instant.now());
        e.setUpdatedAt(Instant.now());
        return toDto(repository.save(e));
    }

    @Transactional
    public MenuItemDto update(Long id, MenuItemWriteDto w) {
        MenuItem e =
                repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        e.setName(w.getName().trim());
        e.setDescription(w.getDescription() != null ? w.getDescription().trim() : null);
        e.setPrice(w.getPrice());
        e.setImageUrl(w.getImageUrl().trim());
        e.setActive(w.isActive());
        e.setSortOrder(w.getSortOrder());
        e.setUpdatedAt(Instant.now());
        return toDto(repository.save(e));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }

    private MenuItemDto toDto(MenuItem e) {
        MenuItemDto d = new MenuItemDto();
        d.setId(e.getId());
        d.setName(e.getName());
        d.setDescription(e.getDescription());
        d.setPrice(e.getPrice());
        d.setImageUrl(e.getImageUrl());
        d.setActive(e.isActive());
        d.setSortOrder(e.getSortOrder());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        return d;
    }

    private MenuItem fromWrite(MenuItemWriteDto w) {
        MenuItem e = new MenuItem();
        e.setName(w.getName().trim());
        e.setDescription(w.getDescription() != null ? w.getDescription().trim() : null);
        e.setPrice(w.getPrice());
        e.setImageUrl(w.getImageUrl().trim());
        e.setActive(w.isActive());
        e.setSortOrder(w.getSortOrder());
        return e;
    }
}

package com.restaurant.billing.web;

import com.restaurant.billing.dto.MenuItemDto;
import com.restaurant.billing.dto.MenuItemWriteDto;
import com.restaurant.billing.service.MenuItemService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping("/active")
    public List<MenuItemDto> listActive() {
        return menuItemService.findActive();
    }

    @GetMapping
    public List<MenuItemDto> listAll() {
        return menuItemService.findAll();
    }

    @GetMapping("/{id}")
    public MenuItemDto get(@PathVariable Long id) {
        return menuItemService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemDto create(@Valid @RequestBody MenuItemWriteDto body) {
        return menuItemService.create(body);
    }

    @PutMapping("/{id}")
    public MenuItemDto update(@PathVariable Long id, @Valid @RequestBody MenuItemWriteDto body) {
        return menuItemService.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        menuItemService.delete(id);
    }
}

package org.acme.awesomepizza.web.items;

import lombok.RequiredArgsConstructor;
import org.acme.awesomepizza.service.items.ItemsService;
import org.acme.awesomepizza.web.items.models.ItemModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemsController {

    private final ItemsService itemsService;

    @GetMapping
    public List<ItemModel> getAllItems() {
        return itemsService.findAllItems();
    }
}

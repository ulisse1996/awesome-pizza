package org.acme.awesomepizza.web.items;

import org.acme.awesomepizza.service.items.ItemsService;
import org.acme.awesomepizza.web.items.models.ItemModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemsControllerTest {

    @Mock private ItemsService itemsService;
    @InjectMocks private ItemsController itemsController;

    @Test
    void should_return_all_valid_items() {
        ItemModel model = ItemModel.builder()
                .id(1L)
                .name("Pizza")
                .build();
        when(itemsService.findAllItems()).thenReturn(List.of(model));
        assertEquals(List.of(model), itemsController.getAllItems());
    }
}
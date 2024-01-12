package org.acme.awesomepizza.service.items;

import org.acme.awesomepizza.data.items.Item;
import org.acme.awesomepizza.data.items.ItemsRepository;
import org.acme.awesomepizza.web.items.models.ItemModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemsServiceTest {

    @Spy private ItemsMapper itemsMapper = new ItemsMapperImpl();
    @Mock private ItemsRepository itemsRepository;
    @InjectMocks private ItemsService itemsService;

    @Test
    void should_return_all_items() {
        Item item = new Item(1L, "Pizza Margherita");
        Item item2 = new Item(2L, "Pizza Marinara");
        when(itemsRepository.findAll()).thenReturn(List.of(item, item2));

        List<ItemModel> items = itemsService.findAllItems();
        assertEquals(2, items.size());
        assertEquals(1L, items.get(0).getId());
        assertEquals("Pizza Margherita", items.get(0).getName());
        assertEquals(2L, items.get(1).getId());
        assertEquals("Pizza Marinara", items.get(1).getName());
    }
}
package org.acme.awesomepizza.service.items;

import lombok.RequiredArgsConstructor;
import org.acme.awesomepizza.data.items.ItemsRepository;
import org.acme.awesomepizza.web.items.models.ItemModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemsService {

    private final ItemsMapper itemsMapper;
    private final ItemsRepository itemsRepository;

    public List<ItemModel> findAllItems() {
        return itemsRepository.findAll()
                .stream()
                .map(itemsMapper::toModel)
                .toList();
    }
}

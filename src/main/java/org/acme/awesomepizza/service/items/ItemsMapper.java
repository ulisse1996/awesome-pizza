package org.acme.awesomepizza.service.items;

import org.acme.awesomepizza.data.items.Item;
import org.acme.awesomepizza.web.items.models.ItemModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ItemsMapper {

    @Mapping(target = "id", source = "itemId")
    ItemModel toModel(Item item);
}

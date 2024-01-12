package org.acme.awesomepizza.web.items.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemModel {

    private Long id;
    private String name;
}

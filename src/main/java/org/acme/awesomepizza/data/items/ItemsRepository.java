package org.acme.awesomepizza.data.items;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemsRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i.itemId FROM Item i WHERE i.itemId IN :ids")
    List<Long> findAllItemsIds(Collection<Long> ids);
}

package org.acme.awesomepizza.data.orders;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acme.awesomepizza.data.users.User;

import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long orderId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "locked_by", insertable = false, updatable = false)
    private Long lockedId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locked_by", referencedColumnName = "user_id")
    private User lockedUser;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;
}

package org.acme.awesomepizza.data.orders;

public enum OrderStatus {
    RECEIVED,
    IN_PROGRESS,
    DELIVERED;

    public static OrderStatus findNext(OrderStatus status) {
        return switch (status) {
            case RECEIVED -> IN_PROGRESS;
            case IN_PROGRESS -> DELIVERED;
            default -> throw new IllegalArgumentException("Can't find order status next to " + status);
        };
    }
}

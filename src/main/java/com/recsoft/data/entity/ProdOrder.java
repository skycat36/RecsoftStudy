package com.recsoft.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "prod_order")
public class ProdOrder {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer count;


}

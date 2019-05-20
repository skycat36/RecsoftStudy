package com.recsoft.data.entity;

import javax.persistence.*;


@Embeddable
@Table(name = "prod_order")
public class ProdOrder {

//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinColumn(name = "order", referencedColumnName = "id")
//    private Order order;
//
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinColumn(name = "product", referencedColumnName = "id")
//    private Product product;

    private Integer count;


    public ProdOrder() {
    }

    public ProdOrder(Integer count) {
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}

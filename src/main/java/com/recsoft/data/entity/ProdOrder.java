package com.recsoft.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "prod_order")
public class ProdOrder {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(table = "order", name = "id")
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(table = "product", name = "id")
    private Product product;

    private Integer count;


    public ProdOrder() {
    }

    public ProdOrder(Order order, Product product, Integer count) {
        this.order = order;
        this.product = product;
        this.count = count;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}

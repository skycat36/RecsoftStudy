package com.recsoft.data.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
@Table(name = "order_product")
public class OrderProduct {

    @ApiModelProperty(notes = "Идентификатор обьекта.", name="id", required=true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_order",
            referencedColumnName = "id")
    private Order order;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_prod",
            referencedColumnName = "id")
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_size",
            referencedColumnName = "id")
    private SizeUser sizeUser;

    private Integer count;

    public OrderProduct(Order order, Product product, SizeUser sizeUser, Integer count) {
        this.order = order;
        this.product = product;
        this.sizeUser = sizeUser;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public SizeUser getSizeUser() {
        return sizeUser;
    }

    public void setSizeUser(SizeUser sizeUser) {
        this.sizeUser = sizeUser;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}

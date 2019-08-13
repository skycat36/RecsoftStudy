package com.recsoft.data.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "order_m")
@Api(description = "Заказ на товар.")
public class Order {

    @ApiModelProperty(notes = "Идентификатор обьекта.", name="id", required=true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty(notes = "Ссылка на пользователя сделавшего заказ.", name="user", required=true)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private User user;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<OrderProduct> orderProducts;


    @ApiModelProperty(notes = "Ссылка на статус заказа.", name="status", required=true)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Status status;

    @ApiModelProperty(notes = "Адресс доставки товара.", name="adress", required=true)
    @Length(max = 255)
    private String adress;


    @ApiModelProperty(notes = "Флаг оплаты товара.", name="pay", required=true)
    @Column(name = "pay")
    private Boolean pay;

    public Order() {
    }

    public Order(User user, Set<OrderProduct> orderProducts, Status status, @Length(max = 255) String adress, Boolean pay) {
        this.user = user;
        this.orderProducts = orderProducts;
        this.status = status;
        this.adress = adress;
        this.pay = pay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<OrderProduct> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(Set<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Boolean getPay() {
        return pay;
    }

    public void setPay(Boolean pay) {
        this.pay = pay;
    }
}

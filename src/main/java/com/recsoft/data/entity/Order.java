package com.recsoft.data.entity;

import javax.persistence.*;
import java.util.Set;

/* Заказ на товар.
 * @author Евгений Попов */
@Entity
@Table(name = "order_m")
public class Order {

    /*Идентификатор обьекта*/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* Ссылка на продукты с таким заказом. */
    @ManyToMany
    @JoinTable (name="prod_order",
            joinColumns=@JoinColumn (name="order_id"),
            inverseJoinColumns=@JoinColumn(name="prod_id"))
    private Set<Product> products;

    /* Ссылка на пользователя сделавшего заказ. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private User user;

    /* Ссылка на статус заказа. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Status status;

    /* Адресс доставки товара*/
    private String adress;

    public Order() {
    }

    public Order(Set<Product> products, User user, Status status, String adress) {
        this.products = products;
        this.user = user;
        this.status = status;
        this.adress = adress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}

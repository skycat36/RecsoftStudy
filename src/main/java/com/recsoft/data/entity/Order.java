package com.recsoft.data.entity;

import org.springframework.beans.factory.annotation.Value;

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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Product product;

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

    /* Количество выбранных заказов */
    @Column(name = "count_p")
    private Integer count;

    public Order() {
    }

    public Order(Product product, User user, Status status, String adress, Integer count) {
        this.product = product;
        this.user = user;
        this.status = status;
        this.adress = adress;
        this.count = count;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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

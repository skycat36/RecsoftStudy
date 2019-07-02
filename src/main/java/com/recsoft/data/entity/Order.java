package com.recsoft.data.entity;

import io.swagger.annotations.ApiModel;
import net.bytebuddy.implementation.bind.annotation.Default;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/* Заказ на товар.
 * @author Евгений Попов */
@Entity
@Table(name = "order_m")
@ApiModel(description = "Заказ на товар.")
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

    @Length(max = 255, message = "Длинна поля превышена.")
    //@NotBlank(message = "Поле адресс не может быть пустым")
    private String adress;

    /* Количество выбранных заказов */
    @Column(name = "count_p")
    //@NotBlank(message = "Поле количество товаров не может быть пустым")
    private Integer count;

    @Column(name = "pay")
    private Boolean pay;

    public Order() {
    }

    public Order(Product product, User user, Status status, @Length(max = 255, message = "Длинна поля превышена.") String adress, Integer count, Boolean pay) {
        this.product = product;
        this.user = user;
        this.status = status;
        this.adress = adress;
        this.count = count;
        this.pay = pay;
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

    public Boolean getPay() {
        return pay;
    }

    public void setPay(Boolean pay) {
        this.pay = pay;
    }
}

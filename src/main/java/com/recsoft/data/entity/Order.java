package com.recsoft.data.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

/* Заказ на товар.
 * @author Евгений Попов */
@Entity
@Table(name = "order_m")
@Api(description = "Заказ на товар.")
public class Order {

    @ApiModelProperty(notes = "Идентификатор обьекта.", name="id", required=true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty(notes = "Ссылка на продукты с таким заказом.", name="product", required=true)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Product product;

    @ApiModelProperty(notes = "Ссылка на пользователя сделавшего заказ.", name="user", required=true)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private User user;

    @ApiModelProperty(notes = "Ссылка на статус заказа.", name="status", required=true)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Status status;

    @ApiModelProperty(notes = "Адресс доставки товара.", name="adress", required=true)
    @Length(max = 255)
    private String adress;

    @ApiModelProperty(notes = "Количество выбранных заказов.", name="count", required=true)
    @Column(name = "count_p")
    private Integer count;

    @ApiModelProperty(notes = "Флаг оплаты товара.", name="pay", required=true)
    @Column(name = "pay")
    private Boolean pay;

    public Order() {
    }

    public Order(Product product, User user, Status status, @Length(max = 255) String adress, Integer count, Boolean pay) {
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

package com.recsoft.data.entity;

import io.swagger.annotations.ApiModel;

import javax.persistence.*;

/* Выбранные пользователем товары и их количество.
 * @author Евгений Попов */
@Embeddable
@Table(name = "prod_order")
@ApiModel(description = "Выбранные пользователем товары и их количество.")
public class ProdOrder {

    /* Количество выбранных товаров. */
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

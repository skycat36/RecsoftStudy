package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/* Хранимые фотографии
 * @author Евгений Попов */
@Entity
@Table(name = "photo_m")
public class Photo {

    /*Идентификатор обьекта*/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* Путь хранимой фотографии. */
    private String name;

    /* Ссылка на продукт с хранимой фотографией. */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Product product;

    public Photo(String name, Product product) {
        this.name = name;
        this.product = product;
    }

    public Photo(String name) {
        this.name = name;
    }

    public Photo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}

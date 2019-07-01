package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/* Хранимые фотографии
 * @author Евгений Попов */
@Entity
@Table(name = "photo_m")
@ApiModel(description = "Фотографии товаров.")
public class PhotoProduct {

    /*Идентификатор обьекта*/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* Путь хранимой фотографии. */
    @Length(max = 255, message = "Длинна поля превышена.")
    @NotBlank(message = "Название изображения не может быть пустым")
    private String name;

    /* Ссылка на продукт с хранимой фотографией. */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Product product;

    public PhotoProduct(@Length(max = 255, message = "Длинна поля превышена.") @NotBlank(message = "Название изображения не может быть пустым") String name, Product product) {
        this.name = name;
        this.product = product;
    }

    public PhotoProduct(String name) {
        this.name = name;
    }

    public PhotoProduct() {
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

package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/* Хранимые фотографии
 * @author Евгений Попов */
@Entity
@Table(name = "photo_m")
@ApiModel(description = "Фотографии товаров.")
public class PhotoProduct {

    @ApiModelProperty(notes = "Идентификатор обьекта.", name="id", required=true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty(notes = "Путь хранимой фотографии.", name="name", required=true)
    @Length(max = 255)
    @NotBlank
    private String name;

    @ApiModelProperty(notes = "Ссылка на продукт с хранимой фотографией.", name="product", required=true)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Product product;

    public PhotoProduct(@Length(max = 255) @NotBlank String name, Product product) {
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

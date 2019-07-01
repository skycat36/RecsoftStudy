package com.recsoft.data.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/* Категория товара.
 * @author Евгений Попов */
@Entity
@Table(name = "category")
@ApiModel(description = "Категория товара.")
public class Category {

    /* Идентификатор обьекта. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /*Название категории*/
    @NotBlank(message = "Name category cannot be empty")
    @Length(max = 255, message = "Длинна поля превышена.")
    private String name;

    /* Ссылка на продукты с такой категорией. */
    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Product> products;

    public Category() {
    }

    public Category(@NotBlank(message = "Name category cannot be empty") @Length(max = 255, message = "Длинна поля превышена.") String name, Set<Product> products) {
        this.name = name;
        this.products = products;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}

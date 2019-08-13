package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "category")
@ApiModel(description = "Категория товара.")
public class Category {

    @ApiModelProperty(notes = "Идентификатор обьекта.",name="id", required=true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty(notes = "Название категории", name="name", required=true)
    @NotBlank
    @Length(max = 255)
    private String name;

    @ApiModelProperty(notes = "Ссылка на продукты с такой категорией.", name="products", required=true)
    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Product> products;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable (name="categ_size",
            joinColumns=@JoinColumn (name="id_category"),
            inverseJoinColumns=@JoinColumn(name="id_size"))
    private Set<SizeUser> sizeUsers;


    public Category() {
    }

    public Category(@NotBlank @Length(max = 255) String name, Set<Product> products, Set<SizeUser> sizeUsers) {
        this.name = name;
        this.products = products;
        this.sizeUsers = sizeUsers;
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

    public Set<SizeUser> getSizeUsers() {
        return sizeUsers;
    }

    public void setSizeUsers(Set<SizeUser> sizeUsers) {
        this.sizeUsers = sizeUsers;
    }
}

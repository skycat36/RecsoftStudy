package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "product")
@ApiModel(description = "Данные о продукте.")
public class Product {

    @ApiModelProperty(notes = "Идентификатор обьекта.", name="id", required=true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty(notes = "Название продукта.", name="name", required=true)
    @NotBlank
    @Length(max = 255)
    private String name;

    @ApiModelProperty(notes = "Описание продукта.", name="description", required=true)
    @NotBlank
    @Length(max = 255)
    private String description;

    @ApiModelProperty(notes = "Список пользователей оставивших коментарии.", name="users", required=true)
    @ManyToMany
    @JoinTable (name="user_prod_com",
                joinColumns=@JoinColumn (name="prod_id"),
                inverseJoinColumns=@JoinColumn(name="user_id"))
    private Set<User> users;

    @ApiModelProperty(notes = "Ссылка на категорию товара.", name="category", required=true)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(referencedColumnName = "id")
    private Category category;

    @ApiModelProperty(notes = "Ссылки на коментарии пользователя.", name="coments", required=true)
    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserProdCom> coments;

    @ApiModelProperty(notes = "Список ссылок на имеющиеся размеры товара.", name="sizeUsers", required=true)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ProdSize> prodSizes;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OrderProduct> orderProducts;


    @ApiModelProperty(notes = "Цена за товар.", name="price", required=true)
    @Min(value = 0)
    @NotNull
    private Double price;

    @ApiModelProperty(notes = "Скидка на товар.", name="discount", required=true)
    @Min(value = 0)
    @NotNull
    private Integer discount;

    @ApiModelProperty(notes = "Фотографии товара.", name="photoProducts", required=true)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<PhotoProduct> photoProducts;

    public Product() {
    }

    public Product(@NotBlank @Length(max = 255) String name, @NotBlank @Length(max = 255) String description, Set<User> users, Category category, Set<UserProdCom> coments, Set<ProdSize> prodSizes, Set<OrderProduct> orderProducts, @Min(value = 0) @NotNull Double price, @Min(value = 0) @NotNull Integer discount, Set<PhotoProduct> photoProducts) {
        this.name = name;
        this.description = description;
        this.users = users;
        this.category = category;
        this.coments = coments;
        this.prodSizes = prodSizes;
        this.orderProducts = orderProducts;
        this.price = price;
        this.discount = discount;
        this.photoProducts = photoProducts;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Set<PhotoProduct> getPhotoProducts() {
        return photoProducts;
    }

    public void setPhotoProducts(Set<PhotoProduct> photoProducts) {
        this.photoProducts = photoProducts;
    }

    public Set<UserProdCom> getComents() {
        return coments;
    }

    public void setComents(Set<UserProdCom> coments) {
        this.coments = coments;
    }

    public Set<OrderProduct> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(Set<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public Set<ProdSize> getProdSizes() {
        return prodSizes;
    }

    public void setProdSizes(Set<ProdSize> prodSizes) {
        this.prodSizes = prodSizes;
    }
}

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

/* Информация о товаре
 * @author Евгений Попов */
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

    @ApiModelProperty(notes = "Список сделанных заказов на товар.", name="orders", required=true)
    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Order> orders;

    @ApiModelProperty(notes = "Ссылка на категорию товара.", name="category", required=true)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(referencedColumnName = "id")
    private Category category;

    @ApiModelProperty(notes = "Ссылки на коментарии пользователя.", name="coments", required=true)
    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserProdCom> coments;

    @ApiModelProperty(notes = "Список ссылок на имеющиеся размеры товара.", name="sizeUsers", required=true)
    @ManyToMany
    @JoinTable (name="prod_size",
            joinColumns=@JoinColumn (name="id_prod"),
            inverseJoinColumns=@JoinColumn(name="id_size"))
    private Set<SizeUser> sizeUsers;

    @ApiModelProperty(notes = "Цена за товар.", name="price", required=true)
    @Min(value = 0)
    @NotNull
    private Double price;

    @ApiModelProperty(notes = "Скидка на товар.", name="discount", required=true)
    @Min(value = 0)
    @NotNull
    private Integer discount;

    @ApiModelProperty(notes = "Лайки.", name="like", required=true)
    @Column(name = "like_p")
    private Integer like;

    @ApiModelProperty(notes = "Дизлайки.", name="dislike", required=true)
    @Column(name = "dislike_p")
    private Integer dislike;

    @ApiModelProperty(notes = "Количество имеюихся товаров.", name="count", required=true)
    @Min(value = 0)
    @NotNull
    private Integer count;

    @ApiModelProperty(notes = "Фотографии товара.", name="photoProducts", required=true)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<PhotoProduct> photoProducts;

    public Product() {
    }

    public Product(@NotBlank @Length(max = 255) String name, @NotBlank @Length(max = 255) String description, Set<User> users, Set<Order> orders, Category category, Set<UserProdCom> coments, Set<SizeUser> sizeUsers, @Min(value = 0) @NotNull Double price, @Min(value = 0) @NotNull Integer discount, Integer like, Integer dislike, @Min(value = 0) @NotNull Integer count, Set<PhotoProduct> photoProducts) {
        this.name = name;
        this.description = description;
        this.users = users;
        this.orders = orders;
        this.category = category;
        this.coments = coments;
        this.sizeUsers = sizeUsers;
        this.price = price;
        this.discount = discount;
        this.like = like;
        this.dislike = dislike;
        this.count = count;
        this.photoProducts = photoProducts;
    }

    public Set<SizeUser> getSizeUsers() {
        return sizeUsers;
    }

    public void setSizeUsers(Set<SizeUser> sizeUsers) {
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

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
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

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public Integer getDislike() {
        return dislike;
    }

    public void setDislike(Integer dislike) {
        this.dislike = dislike;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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
}

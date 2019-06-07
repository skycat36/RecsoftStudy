package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/* Информация о товаре
 * @author Евгений Попов */
@Entity
@Table(name = "product")
public class Product {

    /*Идентификатор обьекта*/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* Название продукта. */
    @NotBlank(message = "Имя продукта не может быть пустым")
    private String name;

    /* Описание продукта. */
    @NotBlank(message = "Описание продукта не может быть пустым")
    private String description;

    /* Список пользователей оставивших коментарии. */
    @ManyToMany
    @JoinTable (name="user_prod_com",
                joinColumns=@JoinColumn (name="prod_id"),
                inverseJoinColumns=@JoinColumn(name="user_id"))
    private Set<User> users;

    /* Список сделанных заказов на товар. */
    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Order> orders;


    /* Ссылка на категорию товара. */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(referencedColumnName = "id")
    private Category category;


    /* Список ссылок на имеющиеся размеры товара. */
    @ManyToMany
    @JoinTable (name="prod_size",
            joinColumns=@JoinColumn (name="id_prod"),
            inverseJoinColumns=@JoinColumn(name="id_size"))
    private Set<SizeUser> sizeUsers;

    /* Цена за товар. */
    @Min(value = 0, message = "Цена за товар не может быть отрицательной")
    private Double price;

    /* Скидка на товар. */
    @Min(value = 0, message = "Скидка на товар не может быть отрицательной")
    private Integer discount;

    /* Лайки. */
    @Min(value = 0, message = "Количество лайков на товар не может быть отрицательным")
    private Integer like_p;

    /* Дизлайки. */
    @Min(value = 0, message = "Количество дизлайков на товар не может быть отрицательным")
    private Integer dislike_p;

    /* Количество имеюихся товаров. */
    @Min(value = 0, message = "Количество товаров не может быть отрицательной")
    private Integer count;

    /* Фотографии товара. */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Photo> photos;

    public Product() {
    }

    public Product(@NotBlank(message = "Имя продукта не может быть пустым") String name, @NotBlank(message = "Описание продукта не может быть пустым") String description, Set<User> users, Set<Order> orders, Category category, Set<SizeUser> sizeUsers, @Min(value = 0, message = "Цена за товар не может быть отрицательной") Double price, @Min(value = 0, message = "Скидка на товар не может быть отрицательной") Integer discount, @Min(value = 0, message = "Количество лайков на товар не может быть отрицательным") Integer like_p, @Min(value = 0, message = "Количество дизлайков на товар не может быть отрицательным") Integer dislike_p, @Min(value = 0, message = "Количество товаров не может быть отрицательной") Integer count, Set<Photo> photos) {
        this.name = name;
        this.description = description;
        this.users = users;
        this.orders = orders;
        this.category = category;
        this.sizeUsers = sizeUsers;
        this.price = price;
        this.discount = discount;
        this.like_p = like_p;
        this.dislike_p = dislike_p;
        this.count = count;
        this.photos = photos;
    }

    public Set<SizeUser> getSizeUsers() {
        return sizeUsers;
    }

    public void setSizeUsers(Set<SizeUser> sizeUsers) {
        this.sizeUsers = sizeUsers;
    }

    public Integer getLike_p() {
        return like_p;
    }

    public void setLike_p(Integer like_p) {
        this.like_p = like_p;
    }

    public Integer getDislike_p() {
        return dislike_p;
    }

    public void setDislike_p(Integer dislike_p) {
        this.dislike_p = dislike_p;
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
        return like_p;
    }

    public void setLike(Integer like_p) {
        this.like_p = like_p;
    }

    public Integer getDislike() {
        return dislike_p;
    }

    public void setDislike(Integer dislike_p) {
        this.dislike_p = dislike_p;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Set<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<Photo> photos) {
        this.photos = photos;
    }
}

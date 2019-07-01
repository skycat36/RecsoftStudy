package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/* Информация о товаре
 * @author Евгений Попов */
@Entity
@Table(name = "product")
@ApiModel(description = "Данные о продукте.")
public class Product {

    /*Идентификатор обьекта*/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* Название продукта. */
    @NotBlank(message = "Имя продукта не может быть пустым")
    @Length(max = 255, message = "Длинна поля превышена.")
    private String name;

    /* Описание продукта. */
    @NotBlank(message = "Описание продукта не может быть пустым")
    @Length(max = 255, message = "Длинна поля превышена.")
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

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserProdCom> coments;

    /* Список ссылок на имеющиеся размеры товара. */
    @ManyToMany
    @JoinTable (name="prod_size",
            joinColumns=@JoinColumn (name="id_prod"),
            inverseJoinColumns=@JoinColumn(name="id_size"))
    private Set<SizeUser> sizeUsers;

    /* Цена за товар. */
    @Min(value = 0, message = "Цена за товар не может быть отрицательной")
    @NotBlank(message = "Цена за товар не может быть пустым")
    private Double price;

    /* Скидка на товар. */
    @Min(value = 0, message = "Скидка на товар не может быть отрицательной")
    @NotBlank(message = "Скидка не может быть пустой")
    private Integer discount;

    /* Лайки. */
    @Min(value = 0, message = "Количество лайков на товар не может быть отрицательным")
    @Column(name = "like_p")
    @NotBlank(message = "Количество лайков не может быть пустым")
    private Integer like;

    /* Дизлайки. */
    @Min(value = 0, message = "Количество дизлайков на товар не может быть отрицательным")
    @Column(name = "dislike_p")
    @NotBlank(message = "Количество дизлайков не может быть пустым")
    private Integer dislike;

    /* Количество имеюихся товаров. */
    @Min(value = 0, message = "Количество товаров не может быть отрицательной")
    @NotBlank(message = "Количество товаров не может быть пустым")
    private Integer count;

    /* Фотографии товара. */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<PhotoProduct> photoProducts;

    public Product() {
    }

    public Product(@NotBlank(message = "Имя продукта не может быть пустым") @Length(max = 255, message = "Длинна поля превышена.") String name, @NotBlank(message = "Описание продукта не может быть пустым") @Length(max = 255, message = "Длинна поля превышена.") String description, Set<User> users, Set<Order> orders, Category category, Set<UserProdCom> coments, Set<SizeUser> sizeUsers, @Min(value = 0, message = "Цена за товар не может быть отрицательной") @NotBlank(message = "Цена за товар не может быть пустым") Double price, @Min(value = 0, message = "Скидка на товар не может быть отрицательной") @NotBlank(message = "Скидка не может быть пустой") Integer discount, @Min(value = 0, message = "Количество лайков на товар не может быть отрицательным") @NotBlank(message = "Количество лайков не может быть пустым") Integer like, @Min(value = 0, message = "Количество дизлайков на товар не может быть отрицательным") @NotBlank(message = "Количество дизлайков не может быть пустым") Integer dislike, @Min(value = 0, message = "Количество товаров не может быть отрицательной") @NotBlank(message = "Количество товаров не может быть пустым") Integer count, Set<PhotoProduct> photoProducts) {
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

package com.recsoft.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

//TODO доделать связи

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name product cannot be empty")
    private String name;

    @NotBlank(message = "Description product cannot be empty")
    private String description;

    @ManyToMany
    @JoinTable (name="user_prod_com",
                joinColumns=@JoinColumn (name="prod_id"),
                inverseJoinColumns=@JoinColumn(name="user_id"))
    private Set<User> users;

    @ManyToMany
    @JoinTable (name="prod_order",
            joinColumns=@JoinColumn (name="prod_id"),
            inverseJoinColumns=@JoinColumn(name="order_id"))
    private Set<Order> orders;

    //@ManyToMany (mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    //private Set<ProdOrder> prodOrders;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(referencedColumnName = "id")
    private Category category;

    private Double price;

    private Integer discount;

    private Integer like;

    private Integer dislike;

    private Integer count;

    private String filename;

    public Product() {
    }

    public Product(@NotBlank(message = "Name product cannot be empty") String name, @NotBlank(message = "Description product cannot be empty") String description, Set<User> users, Set<Order> orders, Category category, Double price, Integer discount, Integer like, Integer dislike, Integer count, String filename) {
        this.name = name;
        this.description = description;
        this.users = users;
        this.orders = orders;
        this.category = category;
        this.price = price;
        this.discount = discount;
        this.like = like;
        this.dislike = dislike;
        this.count = count;
        this.filename = filename;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}

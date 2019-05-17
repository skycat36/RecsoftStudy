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

    @ManyToMany (mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserProdCom> userProdComs;

    @ManyToMany (mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ProdOrder> prodOrders;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id")
    private Category category;

    private Double price;

    private Integer discount;

    private Integer like;

    private Integer dislike;

    private Integer count;

    private String filename;

    public Product() {
    }

    public Product(@NotBlank(message = "Name product cannot be empty") String name, @NotBlank(message = "Description product cannot be empty") String description, Set<UserProdCom> userProdComs, Set<ProdOrder> prodOrders, Category category, Double price, Integer discount, Integer like, Integer dislike, Integer count, String filename) {
        this.name = name;
        this.description = description;
        this.userProdComs = userProdComs;
        this.prodOrders = prodOrders;
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

    public Set<UserProdCom> getUserProdComs() {
        return userProdComs;
    }

    public void setUserProdComs(Set<UserProdCom> userProdComs) {
        this.userProdComs = userProdComs;
    }

    public Set<ProdOrder> getProdOrders() {
        return prodOrders;
    }

    public void setProdOrders(Set<ProdOrder> prodOrders) {
        this.prodOrders = prodOrders;
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

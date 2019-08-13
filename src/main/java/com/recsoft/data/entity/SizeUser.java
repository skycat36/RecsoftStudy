package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "size_usr")
@ApiModel(description = "Размер продукта.")
public class SizeUser implements Comparable<SizeUser> {

    @ApiModelProperty(notes = "Идентификатор обьекта.", name="id", required=true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty(notes = "Название размера.", name="nameSize", required=true)
    @Column(name = "name_size")
    @NotBlank
    @Length(max = 50)
    private String nameSize;

    @JsonIgnore
    @OneToMany(mappedBy = "sizeUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OrderProduct> orderProducts;

    @ApiModelProperty(notes = "Ссылка на продукты с таким размером.", name="products", required=true)
    @JsonIgnore
    @OneToMany(mappedBy = "sizeUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProdSize> products;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable (name="categ_size",
            joinColumns=@JoinColumn (name="id_size"),
            inverseJoinColumns=@JoinColumn(name="id_category"))
    private Set<Category> categories;

    public SizeUser() {
    }

    public SizeUser(@NotBlank @Length(max = 50) String nameSize, Set<OrderProduct> orderProducts, Set<ProdSize> products, Set<Category> categories) {
        this.nameSize = nameSize;
        this.orderProducts = orderProducts;
        this.products = products;
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameSize() {
        return nameSize;
    }

    public void setNameSize(String nameSize) {
        this.nameSize = nameSize;
    }

    public Set<ProdSize> getProducts() {
        return products;
    }

    public void setProducts(Set<ProdSize> products) {
        this.products = products;
    }

    public Set<OrderProduct> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(Set<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Override
    public int compareTo(SizeUser o) {
        return this.getId().compareTo(o.getId());
    }
}

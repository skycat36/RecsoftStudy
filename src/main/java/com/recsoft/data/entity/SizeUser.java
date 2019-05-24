package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "size_usr")
public class SizeUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name size cannot be empty")
    @Column(name = "name_size")
    private String nameSize;

    @JsonIgnore
    @ManyToMany
    @JoinTable (name="prod_size",
            joinColumns=@JoinColumn (name="id_size"),
            inverseJoinColumns=@JoinColumn(name="id_prod"))
    private Set<Product> products;

    public SizeUser() {
    }

    public SizeUser(@NotBlank(message = "Name size cannot be empty") String nameSize, Set<Product> products) {
        this.nameSize = nameSize;
        this.products = products;
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

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}

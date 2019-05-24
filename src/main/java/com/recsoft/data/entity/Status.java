package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "status")
public class Status  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Order> orders;

    public Status() {
    }

    public Status(@NotBlank(message = "Name cannot be empty") String name, Set<Order> orders) {
        this.name = name;
        this.orders = orders;
    }

    public Status(@NotBlank(message = "Name cannot be empty") String name) {
        this.name = name;
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

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }
}

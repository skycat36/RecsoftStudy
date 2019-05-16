package com.recsoft.data.entity;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "category")
public class Category {
//    JACKET, JEANS, SHIRTS, SHELL, GLOVES, HEMLET, ACCESSORIES;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name category cannot be empty")
    private String name;

    public Category() {
    }

    public Category(@NotBlank(message = "Name category cannot be empty") String name) {
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
}

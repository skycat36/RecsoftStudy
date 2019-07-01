package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/* Размеры продукта.
 * @author Евгений Попов */
@Entity
@Table(name = "size_usr")
@ApiModel(description = "Размер продукта.")
public class SizeUser {

    /*Идентификатор обьекта*/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* Название размера*/
    @NotBlank(message = "Название размера не может быть пустым")
    @Length(max = 50, message = "Длинна поля превышена.")
    @Column(name = "name_size")
    private String nameSize;

    /* Ссылка на продукты с таким размером. */
    @JsonIgnore
    @ManyToMany
    @JoinTable (name="prod_size",
            joinColumns=@JoinColumn (name="id_size"),
            inverseJoinColumns=@JoinColumn(name="id_prod"))
    private Set<Product> products;

    public SizeUser() {
    }

    public SizeUser(@NotBlank(message = "Название размера не может быть пустым") @Length(max = 50, message = "Длинна поля превышена.") String nameSize, Set<Product> products) {
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

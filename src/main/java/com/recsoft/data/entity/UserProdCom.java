package com.recsoft.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "user_prod_com")
@ApiModel(description = "Комментарий пользователя для продукта.")
public class UserProdCom implements Comparable<UserProdCom> {

    @ApiModelProperty(notes = "Идентификатор обьекта.", name="id", required=true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty(notes = "Комментарий на товар.", name="comment", required=true)
    @Length(max = 255)
    @NotBlank
    private String comment;

    @ApiModelProperty(notes = "Ссылка на пользователя сделавшего заказ.", name="user", required=true)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",
            referencedColumnName = "id")
    private User user;

    @ApiModelProperty(notes = "Ссылка на комментируемый продукт", name="product", required=true)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prod_id",
            referencedColumnName = "id")
    private Product product;

    public UserProdCom() {
    }

    public UserProdCom(@Length(max = 255) @NotBlank String comment, User user, Product product) {
        this.comment = comment;
        this.user = user;
        this.product = product;
    }

    public UserProdCom(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


    @Override
    public int compareTo(UserProdCom o) {
        return this.getId().compareTo(o.getId());
    }
}

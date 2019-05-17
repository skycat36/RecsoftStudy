package com.recsoft.data.entity;

import javax.persistence.*;

@Embeddable
@Table(name = "user_prod_com")
public class UserProdCom {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private Product product;

    private String comment;

    public UserProdCom(User author, Product product, String comment) {
        this.author = author;
        this.product = product;
        this.comment = comment;
    }

    public UserProdCom() {
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

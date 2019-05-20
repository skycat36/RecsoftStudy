package com.recsoft.data.entity;

import javax.persistence.*;

@Embeddable
@Table(name = "user_prod_com")
public class UserProdCom {

    private String comment;

    public UserProdCom() {
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
}

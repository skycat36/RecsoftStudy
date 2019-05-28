package com.recsoft.data.entity;

import javax.persistence.*;

/* Коментарии к товару.
 * @author Евгений Попов */
@Embeddable
@Table(name = "user_prod_com")
public class UserProdCom {

    /* Комментарий на товар. */
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

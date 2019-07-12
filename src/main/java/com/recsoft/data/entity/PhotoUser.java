package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "photo_u")
@ApiModel(description = "Фотографии пользователей.")
public class PhotoUser implements Serializable {

    @ApiModelProperty(notes = "Идентификатор обьекта.", name="id", required=true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty(notes = "Ссылка на пользователя.", name="user", required=true)
    @JsonIgnore
    @OneToOne
    @JoinColumn(referencedColumnName = "id", name = "id_user")
    private User user;

    @ApiModelProperty(notes = "Путь хранимой фотографии.", name="name", required=true)
    @Length(max = 255)
    @NotBlank
    private String name;

    public PhotoUser(User user, @Length(max = 255) @NotBlank String name) {
        this.user = user;
        this.name = name;
    }

    public PhotoUser() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

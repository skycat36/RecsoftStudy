package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "photo_u")
@ApiModel(description = "Фотографии пользователей.")
public class PhotoUser implements Serializable {

    /*Идентификатор обьекта*/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id", name = "id_user")
    private User user;

    /* Путь хранимой фотографии. */
    @Length(max = 255, message = "Длинна поля превышена.")
    @NotBlank(message = "Название изображения не может быть пустым")
    private String name;

    public PhotoUser(User user, @Length(max = 255, message = "Длинна поля превышена.") @NotBlank(message = "Название изображения не может быть пустым") String name) {
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

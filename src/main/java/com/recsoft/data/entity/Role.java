package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/* Роль пользователя.
 * @author Евгений Попов */
@Entity
@Table(name = "role")
@ApiModel(description = "Определяет роль пользователя.")
public class Role implements GrantedAuthority {

    /*Идентификатор обьекта*/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* Название роли. */
    @NotBlank(message = "Название роли не может быть пустой.")
    @Column(name = "name")
    @Length(max = 50, message = "Длинна поля превышена.")
    private String name;

    /* Список пользователей имеющих эту роль*/
    @JsonIgnore
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<User> users;

    public Role() {
    }

    public Role(@NotBlank(message = "Название роли не может быть пустой.") @Length(max = 50, message = "Длинна поля превышена.") String name, Set<User> users) {
        this.name = name;
        this.users = users;
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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }



    @Override
    public String getAuthority() {
        return this.name;
    }
}

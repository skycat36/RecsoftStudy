package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
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

    @ApiModelProperty(notes = "Константы названия имеющихся ролей.", required=true)
    @Ignore
    @Transient
    public static final String ADMIN = "admin", SELLER = "seller", USER = "user";

    /*Идентификатор обьекта*/
    @ApiModelProperty(notes = "Идентификатор обьекта.", name="id", required=true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* Название роли. */
    @ApiModelProperty(notes = "Название роли.", name="name", required=true)
    @Column(name = "name")
    @NotBlank
    @Length(max = 50)
    private String name;

    /* Список пользователей имеющих эту роль*/
    @ApiModelProperty(notes = "Список пользователей имеющих эту роль.", name="users", required=true)
    @JsonIgnore
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<User> users;

    public Role() {
    }

    public Role(@NotBlank @Length(max = 50) String name, Set<User> users) {
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

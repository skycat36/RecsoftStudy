package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "language")
@Api(description = "Язык интерфейса пользователя.")
public class Language implements Serializable {

    @ApiModelProperty(notes = "Идентификатор обьекта.",name="id", required=true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty(notes = "Путь до файла где хранятся ошибки валидации.", name="pathToValidationErrors", required=true)
    @Column(name = "path_to_validation_errors")
    private String pathToValidationErrors;

    @ApiModelProperty(notes = "Путь до файла где хранится названия полей.", name="pathToValidationErrors", required=true)
    @Column(name = "path_to_text_field")
    private String pathToTextField;

    @ApiModelProperty(notes = "Человеко-читабельное название языка.", name="readbleName", required=true)
    @Column(name = "readable_name")
    @Length(max = 2)
    private String readbleName;

    @ApiModelProperty(notes = "Ссылки на пользователей выбравших такойже язык.", name="users", required=true)
    @JsonIgnore
    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<User> users;

    public Language() {
    }

    public Language(String pathToValidationErrors, String pathToTextField, String readbleName, Set<User> users) {
        this.pathToValidationErrors = pathToValidationErrors;
        this.pathToTextField = pathToTextField;
        this.readbleName = readbleName;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReadbleName() {
        return readbleName;
    }

    public void setReadbleName(String readbleName) {
        this.readbleName = readbleName;
    }

    public String getPathToValidationErrors() {
        return pathToValidationErrors;
    }

    public void setPathToValidationErrors(String pathToValidationErrors) {
        this.pathToValidationErrors = pathToValidationErrors;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getPathToTextField() {
        return pathToTextField;
    }

    public void setPathToTextField(String pathToTextField) {
        this.pathToTextField = pathToTextField;
    }
}

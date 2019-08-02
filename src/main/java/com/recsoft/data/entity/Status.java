package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/* Статус заказа.
 * @author Евгений Попов */
@Entity
@Table(name = "status")
@ApiModel(description = "Статус заказа.")
public class Status  {

    @ApiModelProperty(notes = "Константы названия имеющихся ролей.", required=true)
    @Ignore
    @Transient
    public static final String DONE = "done", NOT_DONE = "not_done", IN_PROCESS = "in_process";

    @ApiModelProperty(notes = "Идентификатор обьекта.", name="id", required=true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty(notes = "Название статуса заказа.", name="name", required=true)
    @NotBlank
    @Length(max = 50)
    private String name;

    @ApiModelProperty(notes = "Список заказов имеющих этот статус.", name="orders", required=true)
    @JsonIgnore
    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Order> orders;

    public Status() {
    }

    public Status(@NotBlank @Length(max = 50) String name, Set<Order> orders) {
        this.name = name;
        this.orders = orders;
    }

    public Status(@NotBlank String name) {
        this.name = name;
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

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

}

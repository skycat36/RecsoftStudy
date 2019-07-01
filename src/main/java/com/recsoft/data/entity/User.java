package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Set;

/* Информация о пользователе.
 * @author Евгений Попов */
@Entity
@Table(name = "usr")
public class User implements UserDetails {

    /*Идентификатор обьекта*/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* Имя. */
    @NotBlank(message = "Поле Имя не может быть пустым")
    @Length(max = 50, message = "Длинна поля превышена.")
    private String name;

    /* Фамилия. */
    @NotBlank(message = "Поле Фамилия не может быть пустым")
    @Length(max = 50, message = "Длинна поля превышена.")
    private String fam;

    /* Отчество. */
    @NotBlank(message = "Поле Отчество не может быть пустым")
    @Length(max = 50, message = "Длинна поля превышена.")
    @Column(name = "sec_name")
    private String secName;

    /* Логин пользователя. */
    @NotBlank(message = "Поле Логин не может быть пустым")
    @Column(nullable = false, unique = true)
    @Length(max = 50, message = "Длинна поля превышена.")
    private String login;

    /* Пароль пользователя. */
    @NotBlank(message = "Поле Пароль не может быть пустым")
    @Length(max = 50, message = "Длинна поля превышена.")
    private String password;

    /* Имеющаяся деньги на кошельке. */
    @NotBlank(message = "Поле деньги на кошельке не может быть пустым")
    private Integer cash;

    /* Рейтинг пользователя. */
    @NotBlank(message = "Поле рейтинг не может быть пустым")
    private Integer rating;

    /* email пользователя. */
    @Email(message = "Поле Email введено некорректно")
    @NotBlank(message = "Поле Email не может быть пустым")
    private String email;

    /* Ссылка на роль. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private PhotoUser photoUser;

    /* Список сделанных заказов. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserProdCom> coments;

    /* Ссылка на продукты к которым пользователь оставил коментарии. */
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable (name="user_prod_com",
            joinColumns=@JoinColumn (name="user_id"),
            inverseJoinColumns=@JoinColumn(name="prod_id"))
    private Set<Product> products;

    /* Активирован ли аккаунт пользователя. */
    private Boolean activity;

    public User() {
    }

    public User(@NotBlank(message = "Поле Имя не может быть пустым") @Length(max = 50, message = "Длинна поля превышена.") String name, @NotBlank(message = "Поле Фамилия не может быть пустым") @Length(max = 50, message = "Длинна поля превышена.") String fam, @NotBlank(message = "Поле Отчество не может быть пустым") @Length(max = 50, message = "Длинна поля превышена.") String secName, @NotBlank(message = "Поле Логин не может быть пустым") @Length(max = 50, message = "Длинна поля превышена.") String login, @NotBlank(message = "Поле Пароль не может быть пустым") @Length(max = 50, message = "Длинна поля превышена.") String password, @NotBlank(message = "Поле деньги на кошельке не может быть пустым") Integer cash, @NotBlank(message = "Поле рейтинг не может быть пустым") Integer rating, @Email(message = "Поле Email введено некорректно") @NotBlank(message = "Поле Email не может быть пустым") String email, Role role, PhotoUser photoUser, Set<Order> orders, Set<UserProdCom> coments, Set<Product> products, Boolean activity) {
        this.name = name;
        this.fam = fam;
        this.secName = secName;
        this.login = login;
        this.password = password;
        this.cash = cash;
        this.rating = rating;
        this.email = email;
        this.role = role;
        this.photoUser = photoUser;
        this.orders = orders;
        this.coments = coments;
        this.products = products;
        this.activity = activity;
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

    public String getFam() {
        return fam;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public void setFam(String fam) {
        this.fam = fam;
    }

    public String getSecName() {
        return secName;
    }

    public void setSecName(String secName) {
        this.secName = secName;
    }

    public Integer getCash() {
        return cash;
    }

    public void setCash(Integer cash) {
        this.cash = cash;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getBlock() {
        return activity;
    }

    public void setBlock(Boolean activity) {
        this.activity = activity;
    }

    public Boolean getActivity() {
        return activity;
    }

    public void setActivity(Boolean activity) {
        this.activity = activity;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Set<UserProdCom> getComents() {
        return coments;
    }

    public void setComents(Set<UserProdCom> coments) {
        this.coments = coments;
    }

    public PhotoUser getPhotoUser() {
        return photoUser;
    }

    public void setPhotoUser(PhotoUser photoUser) {
        this.photoUser = photoUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

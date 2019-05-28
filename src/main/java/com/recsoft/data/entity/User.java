package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;
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
    @NotBlank(message = "Name cannot be empty")
    private String name;

    /* Фамилия. */
    @NotBlank(message = "Family cannot be empty")
    private String fam;

    /* Отчество. */
    @NotBlank(message = "Second name cannot be empty")
    private String sec_name;

    /* Логин пользователя. */
    @NotBlank(message = "Поле Логин не может быть пустым")
    @Column(nullable = false, unique = true)
    @Length(max = 50, message = "Login too long")
    private String login;

    /* Пароль пользователя. */
    @NotBlank(message = "Поле Пароль не может быть пустым")
    @Length(max = 50, message = "Password too long")
    private String password;

    /* Имеющаяся деньги на кошельке. */
    private Double cash;

    /* Рейтинг пользователя. */
    private Integer rating;

    /* email пользователя. */
    @Email(message = "Email is not correct")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    /* Ссылка на роль. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Role role;

    /* Список сделанных заказов. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Order> orders;

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

    public User(@NotBlank(message = "Name cannot be empty") String name, @NotBlank(message = "Family cannot be empty") String fam, @NotBlank(message = "Second name cannot be empty") String sec_name, @NotBlank(message = "Поле Логин не может быть пустым") @Length(max = 50, message = "Login too long") String login, @NotBlank(message = "Поле Пароль не может быть пустым") @Length(max = 50, message = "Password too long") String password, Double cash, Integer rating, @Email(message = "Email is not correct") @NotBlank(message = "Email cannot be empty") String email, Role role, Set<Order> orders, Set<Product> products, Boolean activity) {
        this.name = name;
        this.fam = fam;
        this.sec_name = sec_name;
        this.login = login;
        this.password = password;
        this.cash = cash;
        this.rating = rating;
        this.email = email;
        this.role = role;
        this.orders = orders;
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

    public String getSec_name() {
        return sec_name;
    }

    public void setSec_name(String sec_name) {
        this.sec_name = sec_name;
    }

    public Double getCash() {
        return cash;
    }

    public void setCash(Double cash) {
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

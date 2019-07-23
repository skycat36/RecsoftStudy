package com.recsoft.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.*;

@Entity
@Table(name = "usr")
@Api(description = "Информация о пользователе.")
public class User implements UserDetails {

    @ApiModelProperty(notes = "Идентификатор обьекта.", name="id", required=true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* Имя. */
    @ApiModelProperty(notes = "Имя.", name="name", required=true)
    @NotBlank
    @Length(max = 50)
    private String name;

    @ApiModelProperty(notes = "Фамилия.", name="fam", required=true)
    @NotBlank
    @Length(max = 50)
    private String fam;

    @ApiModelProperty(notes = "Отчество.", name="secName", required=true)
    @Column(name = "sec_name")
    @NotBlank
    @Length(max = 50)
    private String secName;

    @ApiModelProperty(notes = "Логин пользователя.", name="login", required=true)
    @Column(nullable = false, unique = true)
    @NotBlank
    @Length(max = 50)
    private String login;

    @ApiModelProperty(notes = "Пароль пользователя.", name="password", required=true)
    @NotBlank
    @Length(max = 50)
    private String password;

    //@NotBlank(message = "Поле деньги на кошельке не может быть пустым")
    @ApiModelProperty(notes = "Имеющаяся деньги на кошельке.", name="cash", required=true)
    private Integer cash;

    //@NotBlank(message = "Поле рейтинг не может быть пустым")
    @ApiModelProperty(notes = "Рейтинг пользователя.", name="rating", required=true)
    private Integer rating;

    @ApiModelProperty(notes = "email пользователя.", name="email", required=true)
    @Email
    @NotBlank
    private String email;

    @ApiModelProperty(notes = "Ссылка на роль.", name="role", required=true)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Role role;

    @ApiModelProperty(notes = "Выбранный язык", name="language", required=true)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Language language;

    @ApiModelProperty(notes = "Фотография пользователя", name="photoUser", required=true)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private PhotoUser photoUser;

    @ApiModelProperty(notes = "Список сделанных заказов.", name="orders", required=true)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Order> orders;

    @ApiModelProperty(notes = "Ссылки на коментарии пользователя.", name="coments", required=true)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserProdCom> coments;

    @ApiModelProperty(notes = "Ссылка на продукты к которым пользователь оставил коментарии.", name="products", required=true)
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable (name="user_prod_com",
            joinColumns=@JoinColumn (name="user_id"),
            inverseJoinColumns=@JoinColumn(name="prod_id"))
    private Set<Product> products;

    @ApiModelProperty(notes = "Активирован ли аккаунт пользователя.", name="activity", required=true)
    private Boolean activity;

    public User() {
    }


    public User(@NotBlank @Length(max = 50) String name, @NotBlank @Length(max = 50) String fam, @NotBlank @Length(max = 50) String secName, @NotBlank @Length(max = 50) String login, @NotBlank @Length(max = 50) String password, Integer cash, Integer rating, @Email @NotBlank String email, Role role, Language language, PhotoUser photoUser, Set<Order> orders, Set<UserProdCom> coments, Set<Product> products, Boolean activity) {
        this.name = name;
        this.fam = fam;
        this.secName = secName;
        this.login = login;
        this.password = password;
        this.cash = cash;
        this.rating = rating;
        this.email = email;
        this.role = role;
        this.language = language;
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

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getName()));
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

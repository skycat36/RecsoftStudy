package com.recsoft.data.entity;

import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "usr")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Family cannot be empty")
    private String fam;

    @NotBlank(message = "Second name cannot be empty")
    private String sec_name;

    @NotBlank(message = "Поле Логин не может быть пустым")
    @Column(nullable = false, unique = true)
    @Length(max = 50, message = "Login too long")
    private String login;       //Логин

    @NotBlank(message = "Поле Пароль не может быть пустым")
    @Length(max = 50, message = "Password too long")
    private String password;    //Пароль

    private Double cash;

    private Integer rating;

    @Email(message = "Email is not correct")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Order> orders;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable (name="user_prod_com",
            joinColumns=@JoinColumn (name="user_id"),
            inverseJoinColumns=@JoinColumn(name="prod_id"))
    private Set<Product> products;


    private Boolean activity;

    private String filename;

    public User() {
    }

    public User(@NotBlank(message = "Name cannot be empty") String name, @NotBlank(message = "Family cannot be empty") String fam, @NotBlank(message = "Second name cannot be empty") String sec_name, @NotBlank(message = "Поле Логин не может быть пустым") @Length(max = 50, message = "Login too long") String login, @NotBlank(message = "Поле Пароль не может быть пустым") @Length(max = 50, message = "Password too long") String password, Double cash, Integer rating, @Email(message = "Email is not correct") @NotBlank(message = "Email cannot be empty") String email, Role role, Set<Order> orders, Set<Product> products, Boolean activity, String filename) {
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
        this.filename = filename;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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

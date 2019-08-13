package com.recsoft.data.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "prod_size")
public class ProdSize implements Comparable<ProdSize> {

    @ApiModelProperty(notes = "Идентификатор обьекта.", name="id", required=true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "count")
    @Min(value = 0)
    @NotNull
    private Integer count;

    @ApiModelProperty(notes = "Ссылка на продукт с имеющимся размером", name="product", required=true)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_prod",
            referencedColumnName = "id")
    private Product product;


    @ApiModelProperty(notes = "Ссылка на имеющийся размер", name="product", required=true)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_size",
            referencedColumnName = "id")
    private SizeUser sizeUser;

    public ProdSize() {
    }

    public ProdSize(@Min(value = 0) @NotNull Integer count, Product product, SizeUser sizeUser) {
        this.count = count;
        this.product = product;
        this.sizeUser = sizeUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public SizeUser getSizeUser() {
        return sizeUser;
    }

    public void setSizeUser(SizeUser sizeUser) {
        this.sizeUser = sizeUser;
    }

    @Override
    public int compareTo(ProdSize o) {
        return this.sizeUser.getId().compareTo(o.sizeUser.getId());
    }

}

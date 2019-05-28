package com.recsoft.data.repository;

import com.recsoft.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/* Репозиторий для работы с продуктами
* @author Evgeny Popov */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findProductByName(String name);
}

package com.recsoft.data.repository;

import com.recsoft.data.entity.Category;
import com.recsoft.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/* Репозиторий для работы с продуктами
* @author Evgeny Popov */
@Repository
@Transactional
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findProductByName(String name);

    List<Product> findAllByCategory(Category category);
}

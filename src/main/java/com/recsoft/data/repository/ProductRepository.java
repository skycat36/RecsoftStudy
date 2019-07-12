package com.recsoft.data.repository;

import com.recsoft.data.entity.Category;
import com.recsoft.data.entity.Product;
import io.swagger.annotations.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Api(value = "Репозиторий продуктов",
        description = "Репозиторий для работы с базой продуктов")
@Repository
@Transactional
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findProductByName(String name);

    List<Product> findAllByCategory(Category category);
}

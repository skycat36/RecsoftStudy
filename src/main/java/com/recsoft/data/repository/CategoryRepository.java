package com.recsoft.data.repository;

import com.recsoft.data.entity.Category;
import io.swagger.annotations.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springfox.documentation.annotations.Cacheable;

import java.util.List;

@Api(value = "Репозиторий категорий",
    description = "Репозиторий для работы с базой категорий товара")
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}

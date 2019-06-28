package com.recsoft.data.repository;

import com.recsoft.data.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springfox.documentation.annotations.Cacheable;

import java.util.List;

/* Репозиторий для работы с базой категорий товара */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

//    @Cacheable("categories")
//    @Override
//    List<Category> findAll();
}

package com.recsoft.data.repository;

import com.recsoft.data.entity.Status;
import io.swagger.annotations.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springfox.documentation.annotations.Cacheable;

import java.util.List;

/* Репозиторий для работы с таблицей статусов заказов
* @author Evgeny Popov */
@Api(value = "Репозиторий категорий",
        description = "Репозиторий для работы с базой категорий товара")
@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

//    @Cacheable("statuses")
//    @Override
//    List<Status> findAll();

    Status findFirstByName(String name);

}

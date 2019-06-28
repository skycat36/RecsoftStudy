package com.recsoft.data.repository;

import com.recsoft.data.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springfox.documentation.annotations.Cacheable;

import java.util.List;

/* Репозиторий для работы с таблицей статусов заказов
* @author Evgeny Popov */
@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

//    @Cacheable("statuses")
//    @Override
//    List<Status> findAll();

    Status findFirstByName(String name);

}

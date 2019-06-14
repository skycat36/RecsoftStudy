package com.recsoft.data.repository;

import com.recsoft.data.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/* Репозиторий для работы с таблицей статусов заказов
* @author Evgeny Popov */
@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

    Status findFirstByName(String name);

}

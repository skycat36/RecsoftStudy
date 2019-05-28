package com.recsoft.data.repository;

import com.recsoft.data.entity.SizeUser;
import org.springframework.data.jpa.repository.JpaRepository;

/*  Репозиторий для работы с базой размеров продуктов
* @author Evgeny Popov */
public interface SizeUserRepository extends JpaRepository<SizeUser, Long> {
}

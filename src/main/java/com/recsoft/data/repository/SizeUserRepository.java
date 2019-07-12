package com.recsoft.data.repository;

import com.recsoft.data.entity.SizeUser;
import io.swagger.annotations.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Api(value = "Репозиторий размеров товара",
        description = "Репозиторий для работы с базой размеров товара")
@Repository
public interface SizeUserRepository extends JpaRepository<SizeUser, Long> {
}

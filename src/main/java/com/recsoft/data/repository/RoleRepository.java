package com.recsoft.data.repository;

import com.recsoft.data.entity.Role;
import io.swagger.annotations.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Api(value = "Репозиторий ролей",
        description = "Репозиторий для работы с базой ролей")
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findFirstByName(String name);
}

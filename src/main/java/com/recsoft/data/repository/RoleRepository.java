package com.recsoft.data.repository;

import com.recsoft.data.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/* Репозиторий для работы с ролями пользователей
* @author Evgeny Popov */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}

package com.recsoft.data.repository;

import com.recsoft.data.entity.Role;
import com.recsoft.data.entity.User;
import io.swagger.annotations.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Api(value = "Репозиторий пользователей",
        description = "Репозиторий для работы с базой пользователей")
@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    User findByLogin(String login);

    List<User> findAllByRole(Role role);
}

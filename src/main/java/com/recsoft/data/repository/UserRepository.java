package com.recsoft.data.repository;

import com.recsoft.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/* Репозиторий для работы с пользователями
* @author Evgeny Popov
* */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);
}

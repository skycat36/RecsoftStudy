package com.recsoft.data.repository;

import com.recsoft.data.entity.Role;
import com.recsoft.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/* Репозиторий для работы с пользователями
* @author Evgeny Popov
* */
@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    User findByLogin(String login);

//    @Override
//    @Query(value = "select * from usr u where u.id =:idUser", nativeQuery = true)
//    Optional<User> findById(@Param("idUser") Long aLong);

    List<User> findAllByRole(Role role);
}

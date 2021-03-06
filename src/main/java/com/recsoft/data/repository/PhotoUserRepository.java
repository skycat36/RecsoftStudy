package com.recsoft.data.repository;

import com.recsoft.data.entity.PhotoUser;
import io.swagger.annotations.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Api(value = "Репозиторий фотографий пользователя.",
        description = "Репозиторий для работы с базой фотографиями пользователя.")
@Repository
@Transactional
public interface PhotoUserRepository extends JpaRepository<PhotoUser, Long> {

    @Modifying
    @Query(value = "DELETE from photo_u p where p.id_user = :idUser", nativeQuery = true)
    void deleteByUser(@Param("idUser") Long idUser);
}

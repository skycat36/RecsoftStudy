package com.recsoft.data.repository;

import com.recsoft.data.entity.PhotoProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/*  Репозиторий для работы с хранимыми фотографиями
* @author Evgeny Popov */
@Repository
@Transactional
public interface PhotoProductRepository extends JpaRepository<PhotoProduct, Long> {

    @Modifying
    @Query(value = "DELETE from photo_m p where p.product_id = :idProd", nativeQuery = true)
    int deleteByProduct(@Param("idProd") Long idProd);
}

package com.recsoft.data.repository;

import com.recsoft.data.entity.ProdSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ProdSizeRepository extends JpaRepository<ProdSize, Long> {

    @Modifying
    @Query(value = "DELETE from prod_size p_s where p_s.id_prod = :idProduct", nativeQuery = true)
    void deleteAllByIdProduct(@Param("idProduct") Long idProduct);



    @Query(value = "select * from prod_size p_s where p_s.id_prod = :idProduct and p_s.id_size = :idSizeUser", nativeQuery = true)
    ProdSize findByProductAndSizeUser(@Param("idProduct")Long idProduct, @Param("idSizeUser")Long idSizeUser);
}

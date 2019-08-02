package com.recsoft.data.repository;

import com.recsoft.data.entity.ProdSize;
import com.recsoft.data.entity.Product;
import com.recsoft.data.entity.SizeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProdSizeRepository extends JpaRepository<ProdSize, Long> {

    ProdSize findAllByProductAndSizeUser(Product product, SizeUser sizeUser);
}

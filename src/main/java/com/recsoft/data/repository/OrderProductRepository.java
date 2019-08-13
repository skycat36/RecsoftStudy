package com.recsoft.data.repository;

import com.recsoft.data.entity.Order;
import com.recsoft.data.entity.OrderProduct;
import com.recsoft.data.entity.Product;
import com.recsoft.data.entity.SizeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    OrderProduct getOrderProductByOrderAndProductAndSizeUser(Order order, Product product, SizeUser sizeUser);

    @Modifying
    @Query(value = "DELETE from order_product o_p where o_p.id_prod = :idProduct", nativeQuery = true)
    void deleteAllByIdProduct(@Param("idProduct") Long idProduct);

    @Modifying
    @Query(value = "DELETE from order_product o_p where o_p.id_order = :idOrder", nativeQuery = true)
    void deleteAllByIdOrder(@Param("idOrder") Long idOrder);
}

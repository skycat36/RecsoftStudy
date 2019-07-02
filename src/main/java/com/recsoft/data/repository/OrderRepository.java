package com.recsoft.data.repository;

import com.recsoft.data.entity.Order;
import com.recsoft.data.entity.Role;
import com.recsoft.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/* Репозиторий для работы с корзиной товаров */
@Repository
@Transactional
public interface OrderRepository extends JpaRepository<Order, Long> {

    //@Query(value = "select * from order_m ord where ord.user_id =:idUser and ord.pay = false", nativeQuery = true)
    List<Order> findAllByUserAndPayFalse(User user);

    List<Order> findAllByUserAndPayTrue(User user);

    List<Order> findAllByUser(User user);

    @Modifying
    @Query(value = "DELETE from order_m ord_m where ord_m.id = :idOrder", nativeQuery = true)
    void deleteOrderById(@Param("idOrder") Long idOrder);


    @Modifying
    @Query(value = "DELETE from order_m ord_m where ord_m.user_id = :idUser and ord_m.pay = false", nativeQuery = true)
    void deleteAllByIdUserNotPay(@Param("idUser") Long idUser);

}

package com.recsoft.data.repository;

import com.recsoft.data.entity.Order;
import com.recsoft.data.entity.User;
import io.swagger.annotations.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Api(value = "Репозиторий категорий",
        description = "Репозиторий для работы с базой заказами")
@Repository
@Transactional
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUserAndPayTrue(User user);

    List<Order> findAllByUser(User user);

    Order findOrderByPayFalseAndUser(User user);

    @Modifying
    @Query(value = "DELETE from order_m ord_m where ord_m.id = :idOrder", nativeQuery = true)
    void deleteOrderById(@Param("idOrder") Long idOrder);

    @Modifying
    @Query(value = "DELETE from order_m ord_m where ord_m.user_id = :idUser and ord_m.pay = true", nativeQuery = true)
    void deleteAllByIdUserPay(@Param("idUser") Long idUser);

}

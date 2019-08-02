package com.recsoft.data.repository;

import com.recsoft.data.entity.Order;
import com.recsoft.data.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    void deleteAllByOrder(Order order);
}

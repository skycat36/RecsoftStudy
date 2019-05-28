package com.recsoft.data.repository;

import com.recsoft.data.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/* Репозиторий для работы с корзиной товаров */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}

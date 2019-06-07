package com.recsoft.service;

import com.recsoft.data.entity.*;
import com.recsoft.data.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final Long DEFAULT_ORDER = new Long(1);

    private Logger log = LoggerFactory.getLogger(ProductService.class.getName());

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private RoleRepository roleRepository;

    public void createOrder(Long idProduct, String adress, Integer countProd, User user){
        Product product = productRepository.findById(idProduct).get();

        Status status = statusRepository.findById(DEFAULT_ORDER).get();

        Order order = new Order(product, user, status, adress, countProd);

        order = orderRepository.save(order);
        log.info("Заказ " + order.getId() + " добавлен в корзину");
    }

    public void deleteOrder(Long idOrder){
        orderRepository.deleteOrderById(idOrder);
        log.info("Заказ с id = " + idOrder + " удален.");
    }

    public Role getRoleByName(String nameRole){
        return roleRepository.findFirstByName(nameRole);
    }

    public List<Order> getOrderUser(User user){
        return orderRepository.findAllByUser(user);
    }


}

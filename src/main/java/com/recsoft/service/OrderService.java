package com.recsoft.service;

import com.recsoft.data.entity.*;
import com.recsoft.data.repository.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Api(value = "Сервис заказов",
        description = "Класс-сервис выполняет операции связанные с заказами, " +
                "отвечающий за целостность базы данных заказов")
public class OrderService {

    private final Long DEFAULT_STATUS_ORDER = new Long(1);

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

    @ApiOperation(value = "Создать заказ")
    public void createOrder(
            @ApiParam(value = "Id выбранного продукта.", required = true) Long idProduct,
            @ApiParam(value = "Адрес куда отправлять продукт.", required = true) String adress,
            @ApiParam(value = "Колличество выбранных продуктов.", required = true) Integer countProd,
            @ApiParam(value = "Выбранный пользователь.", required = true) User user){
        Product product = productRepository.findById(idProduct).get();

        Status status = statusRepository.findById(DEFAULT_STATUS_ORDER).get();

        Order order = new Order(product, user, status, adress, countProd);

        product.setCount(product.getCount() - countProd);
        productRepository.save(product);

        order = orderRepository.save(order);
        log.info("Заказ " + order.getId() + " добавлен в корзину");
    }

    @ApiOperation(value = "Удалить заказ.")
    public void deleteOrder(
            @ApiParam(value = "Id продукта который надо удалить", required = true) Long idOrder){
        orderRepository.deleteOrderById(idOrder);
        log.info("Заказ с id = " + idOrder + " удален.");
    }

    @ApiOperation(value = "Возвращает роль по названию.")
    public Role getRoleByName(
            @ApiParam(value = "Имя роли.", required = true) String nameRole){
        return roleRepository.findFirstByName(nameRole);
    }

    @ApiOperation(value = "Заказы сделанные пользователем.")
    public List<Order> getOrderUser(
            @ApiParam(value = "Пользователь у которого необходимо вернуть заказы", required = true) User user){
        return orderRepository.findAllByUser(user);
    }

    @ApiOperation(value = "Обновить заказы пользователя")
    public void updateOrderList(
            @ApiParam(value = "Обновляемые заказы", required = true) List<Order> orderList){
        orderRepository.saveAll(orderList);
    }


}

package com.recsoft.service;

import com.recsoft.data.entity.*;
import com.recsoft.data.exeption.UserExeption;
import com.recsoft.data.repository.*;
import com.recsoft.utils.ControllerUtils;
import com.recsoft.utils.ReadbleUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Api(value = "Сервис заказов",
        description = "Класс-сервис выполняет операции связанные с заказами, " +
                "отвечающий за целостность базы данных заказов")
public class OrderService {

    private final long DEFAULT_STATUS_ORDER = 1;

    private Logger log = LoggerFactory.getLogger(ProductService.class.getName());

    private final UserService userService;

    private final ProductService productService;

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final StatusRepository statusRepository;

    private final RoleRepository roleRepository;

    @Autowired
    public OrderService(UserService userService, ProductService productService, ProductRepository productRepository, OrderRepository orderRepository, UserRepository userRepository, StatusRepository statusRepository, RoleRepository roleRepository) {
        this.userService = userService;
        this.productService = productService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.statusRepository = statusRepository;
        this.roleRepository = roleRepository;
    }

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
            @ApiParam(value = "Id продукта который надо удалить", required = true) Long idOrder,
            @ApiParam(value = "Id продукта который надо удалить", required = true) Long idUser){

        Order order = orderRepository.findById(idOrder).get();

        Product product = order.getProduct();

        Integer addCash = Integer.parseInt(String.valueOf(Math.round(product.getPrice() * order.getCount())));

        product.setCount(product.getCount() + order.getCount());

        productRepository.save(product);

        userService.addCashUser(idUser, addCash);

        orderRepository.deleteOrderById(idOrder);
        log.info("Заказ с id = " + idOrder + " удален.");
    }

    @ApiOperation(value = "Удалить заказы пользователя.")
    public void deleteAllOrdersUser(
            @ApiParam(value = "Id продукта который надо удалить", required = true) Long idUser){

        User user = userService.getUserById(idUser);

        List<Order> ordersUser = new ArrayList<>(user.getOrders());
        List<Product> productWhoNeedUpdete = new ArrayList<>();

        double addCash = 0.0;

        for (Order order: ordersUser){
            Product product = order.getProduct();
            product.setCount(product.getCount() + order.getCount());
            productWhoNeedUpdete.add(product);
            addCash += order.getCount() * order.getProduct().getPrice();
        }



        productRepository.saveAll(productWhoNeedUpdete);

        userService.addCashUser(idUser, Integer.parseInt(String.valueOf(Math.round(addCash))));

        orderRepository.deleteAllByIdUser(user.getId());
        log.info("Заказ пользователя с id = " + user.getUsername() + " удалены.");
    }

    @ApiOperation(value = "Возвращает роль по названию.")
    public Role getRoleByName(
            @ApiParam(value = "Имя роли.", required = true) String nameRole){
        return roleRepository.findFirstByName(nameRole);
    }

    @ApiOperation(value = "Возвращает статус по названию.")
    public Status getStatusByName(
            @ApiParam(value = "Имя статуса.", required = true) String nameStatus){
        return statusRepository.findFirstByName(nameStatus);
    }

    @ApiOperation(value = "Заказы сделанные пользователем.")
    public List<Order> getOrderUser(
            @ApiParam(value = "Пользователь у которого необходимо вернуть заказы", required = true) User user){
        return orderRepository.findAllByUser(user);
    }

    @ApiOperation(value = "Создание читаемых статусов заказа.")
    public List<String> createListReadbleStatusOrders(
            @ApiParam(value = "Список заказов пользователя.", required = true) List<Order> orders){
        List<String> listReadbleStatus = new ArrayList<>();

        for (Order order: orders){
            listReadbleStatus.add(ReadbleUtils.createReadableStatusOrder(order.getStatus().getName()));
        }
        return listReadbleStatus;
    }
    @ApiOperation(value = "Обновить заказы пользователя")
    public void updateOrderList(
            @ApiParam(value = "Пользователь системы.", required = true) User user,
            @ApiParam(value = "Информация о количестве товаров.", required = true) List<String> countProducts,
            @ApiParam(value = "Информация об ошибках.", required = true) Map<String, String> errors){

        List<Order> ordersUser = this.getOrderUser(userService.getUserById(user.getId()));
        List<Order> orderWhoNeedUpdate = new ArrayList<>();
        List<Product> productListWhoNeedUpdate = new ArrayList<>();
        double realPriceUser = this.calculetePriseForUser(ordersUser);


        for (int i = 0; i < ordersUser.size(); i++) {
            Integer realSelectProd = Integer.parseInt(countProducts.get(i));
            if (!ordersUser.get(i).getCount().equals(realSelectProd)) {
                Product productOrd = ordersUser.get(i).getProduct();
                productOrd.setCount(productOrd.getCount() + ordersUser.get(i).getCount());
                productOrd.setCount(productOrd.getCount() - realSelectProd);
                ordersUser.get(i).setCount(realSelectProd);
                orderWhoNeedUpdate.add(ordersUser.get(i));
                productListWhoNeedUpdate.add(productOrd);
            }
        }

        double newPriceUser = this.calculetePriseForUser(ordersUser);
        realPriceUser -= newPriceUser;

        if (realPriceUser < 0){
            try {
                userService.subtractCashUser(user.getId(), Math.abs(this.roundPriseForUser(realPriceUser)));
            } catch (UserExeption userExeption) {
                errors.put(ControllerUtils.constructError("price"), userExeption.getMessage());
            }
        }else {
            userService.addCashUser(user.getId(), this.roundPriseForUser(realPriceUser));
        }

        if (errors.isEmpty()){
            productService.updateProductList(productListWhoNeedUpdate);
            orderRepository.saveAll(orderWhoNeedUpdate);
        }
    }

    @ApiOperation(value = "Сумма цен сделаных пользователем заказов.")
    public double calculetePriseForUser(
            @ApiParam(value = "Список заказов пользователя.", required = true) List<Order> ordersUser){
        double prise = 0.0;

        for (Order order: ordersUser){
            prise += order.getCount() * order.getProduct().getPrice();
        }

        return prise;
    }

    @ApiOperation(value = "Сумма округленная.")
    public Integer roundPriseForUser(
            @ApiParam(value = "Сумма пользователя.", required = true) Double prise){

        return Integer.parseInt(String.valueOf(Math.round(prise)));
    }

    @ApiOperation(value = "Обновить статус списка переданных заказов.")
    public void updateStatusOrders(
            @ApiParam(value = "Пользователь системы.", required = true) User user,
            @ApiParam(value = "Информация о статусе товаров.", required = true) List<String> statusOrd
            ){
        List<Order> ordersUser = this.getOrderUser(user);
        List<Order> orderWhoNeedUpdate = new ArrayList<>();

        for (int i = 0; i < ordersUser.size(); i++) {
            Status statusRealOrder = ordersUser.get(i).getStatus();
            String newStatusName = ReadbleUtils.createStatusOrderFromReadable(statusOrd.get(i));
            if (!statusRealOrder.getName().equals(newStatusName)) {
                ordersUser.get(i).setStatus(this.getStatusByName(newStatusName));
                orderWhoNeedUpdate.add(ordersUser.get(i));
            }
        }
        orderRepository.saveAll(orderWhoNeedUpdate);

    }

}

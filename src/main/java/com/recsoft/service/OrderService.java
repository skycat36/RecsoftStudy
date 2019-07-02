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

    private UserService userService;

    private ProductService productService;

    private ProductRepository productRepository;

    private OrderRepository orderRepository;

    private UserRepository userRepository;

    private StatusRepository statusRepository;

    private RoleRepository roleRepository;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setStatusRepository(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
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

        Order order = new Order(product, user, status, adress, countProd, false);

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

    @ApiOperation(value = "Удалить заказ.")
    public void deleteOrderWhoNotPay(
            @ApiParam(value = "Id продукта который надо удалить", required = true) Long idOrder){

        Order order = orderRepository.findById(idOrder).get();

        Product product = order.getProduct();

        product.setCount(product.getCount() + order.getCount());

        productRepository.save(product);

        orderRepository.deleteOrderById(idOrder);
        log.info("Заказ с id = " + idOrder + " удален.");
    }

    @ApiOperation(value = "Удалить заказы пользователя.")
    public void deleteAllOrdersNotPayUser(
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

        orderRepository.deleteAllByIdUserNotPay(user.getId());
        log.info("Неоплаченные заказы пользователя с id = " + user.getUsername() + " удалены.");
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

    @ApiOperation(value = "Заказы сделанные пользователем не оплаченные.")
    public List<Order> getOrderUserNotPay(
            @ApiParam(value = "Пользователь у которого необходимо вернуть неоплаченные заказы", required = true) User user){
        return orderRepository.findAllByUserAndPayFalse(user);
    }

    @ApiOperation(value = "Заказы сделанные пользователем.")
    public List<Order> getOrderUser(
            @ApiParam(value = "Пользователь у которого необходимо вернуть заказы", required = true) User user){
        return orderRepository.findAllByUserAndPayTrue(user);
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
    public void updateOrderListWhoNotPay(
            @ApiParam(value = "Пользователь системы.", required = true) User user,
            @ApiParam(value = "Адрес куда отправлять продукт.", required = true) String adress,
            @ApiParam(value = "Информация об ошибках.", required = true) Map<String, String> errors){

        List<Order> ordersUser = this.getOrderUserNotPay(userService.getUserById(user.getId()));
        double realPriceOrder = this.calculetePriseForUser(ordersUser);

        if (user.getCash() < realPriceOrder){
            errors.put(ControllerUtils.constructError("price"), "Недостаточно средств");
        }

        if (adress.equals("")){
            errors.put(ControllerUtils.constructError("adress"), "Поле адреса пустое");
        }

        if (errors.isEmpty()){
            for (Order order: ordersUser) {
                order.setAdress(adress);
                order.setPay(true);
            }

            try {
                userService.subtractCashUser(user.getId(), Math.abs(this.roundPriseForUser(realPriceOrder)));
                orderRepository.saveAll(ordersUser);
            } catch (UserExeption userExeption) {
                errors.put(ControllerUtils.constructError("price"), userExeption.getMessage());
            }
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

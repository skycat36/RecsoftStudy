package com.recsoft.service;

import com.recsoft.data.entity.*;
import com.recsoft.data.exeption.UserExeption;
import com.recsoft.data.repository.*;
import com.recsoft.utils.ConfigureErrors;
import com.recsoft.utils.ControllerUtils;
import com.recsoft.utils.ReadbleUtils;
import com.recsoft.validation.MessageGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(notes = "Константа для выбора статуса заказа по умолчанию", name="DEFAULT_STATUS_ORDER")
    private final long DEFAULT_STATUS_ORDER = 1;

    @ApiModelProperty(notes = "Записывает логи сделанных действий и ошибок.", name="log", value="ProductController")
    private Logger log = LoggerFactory.getLogger(ProductService.class.getName());

    private UserService userService;

    private ProductRepository productRepository;

    private OrderRepository orderRepository;

    private StatusRepository statusRepository;

    private RoleRepository roleRepository;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
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

    @ApiOperation(value = "Удалить оплаченный заказ.")
    public void deletePayOrder(
            @ApiParam(value = "Id заказа который надо удалить", required = true) Long idOrder,
            @ApiParam(value = "Id пользователя у которого удаляют заказ", required = true) Long idUser){

        Order order = orderRepository.findById(idOrder).get();

        Product product = order.getProduct();

        Integer addCash = Integer.parseInt(String.valueOf(Math.round(product.getPrice() * order.getCount())));

        product.setCount(product.getCount() + order.getCount());

        productRepository.save(product);

        userService.addCashUser(idUser, addCash);

        orderRepository.deleteOrderById(idOrder);
        log.info("Заказ с id = " + idOrder + " удален.");
    }

    @ApiOperation(value = "Удалить неоплаченный заказ.")
    public void deleteOrderWhoNotPay(
            @ApiParam(value = "Id заказа который надо удалить", required = true) Long idOrder){

        Order order = orderRepository.findById(idOrder).get();

        Product product = order.getProduct();

        product.setCount(product.getCount() + order.getCount());

        productRepository.save(product);

        orderRepository.deleteOrderById(idOrder);
        log.info("Заказ с id = " + idOrder + " удален.");
    }

    @ApiOperation(value = "Обновить количество сделанных заказов заказ.")
    public void updateCountOrderWhoNotPay(
            @ApiParam(value = "Id заказа который надо обновить.", required = true) long idOrder,
            @ApiParam(value = "Обновленное количество товаров.", required = true) int countNewProd){

        Order order = orderRepository.findById(idOrder).get();
        Product product = order.getProduct();

        int realCount = product.getCount() + order.getCount();

        product.setCount(realCount - countNewProd);
        order.setCount(countNewProd);

        productRepository.save(product);
        orderRepository.save(order);

        log.info("Заказ с id = " + idOrder + " обновлен.");
    }

    @ApiOperation(value = "Проверить правильность выбора количества товаров.")
    public boolean proveCountOrderedProduct(
            @ApiParam(value = "Id заказа у которого проверяется количество товара.", required = true) long idOrder,
            @ApiParam(value = "Обновленное количество товаров.", required = true) int countProd){

        Order order = orderRepository.findById(idOrder).get();
        Product product = order.getProduct();

        if (product.getCount() + order.getCount() < countProd || countProd < 0){
            return false;
        }
        return true;
    }

    @ApiOperation(value = "Удалить все неоплаченные заказы.")
    public void deleteAllOrdersNotPayUser(
            @ApiParam(value = "Id пользователя у которого удаляют неоплаченные заказы", required = true) Long idUser){

        User user = userService.getUserById(idUser);

        List<Order> ordersNotPayUser = orderRepository.findAllByUserAndPayFalse(user);
        List<Product> productWhoNeedUpdate = new ArrayList<>();

        for (Order order: ordersNotPayUser){
            Product product = order.getProduct();
            product.setCount(product.getCount() + order.getCount());
            productWhoNeedUpdate.add(product);
        }

        productRepository.saveAll(productWhoNeedUpdate);

        orderRepository.deleteAllByIdUserNotPay(user.getId());

        log.info("Заказ с id = " + user.getId() + " удален.");
    }

    @ApiOperation(value = "Удалить оплаченные заказы пользователя.")
    public void deleteAllOrdersWhoPayUser(
            @ApiParam(value = "Id пользователя у которого удаляют оплаченные заказы", required = true) Long idUser){

        User user = userService.getUserById(idUser);

        List<Order> ordersUser = new ArrayList<>(user.getOrders());
        List<Product> productWhoNeedUpdate = new ArrayList<>();

        double addCash = 0.0;

        for (Order order: ordersUser){
            Product product = order.getProduct();
            product.setCount(product.getCount() + order.getCount());
            productWhoNeedUpdate.add(product);
            addCash += order.getCount() * order.getProduct().getPrice();
        }

        productRepository.saveAll(productWhoNeedUpdate);

        userService.addCashUser(idUser, Integer.parseInt(String.valueOf(Math.round(addCash))));

        orderRepository.deleteAllByIdUserPay(user.getId());
        log.info("Оплаченные заказы пользователя с id = " + user.getUsername() + " удалены.");
    }

    @ApiOperation(value = "Возвращает роль по названию.")
    public Role getRoleByName(
            @ApiParam(value = "Имя роли.", required = true) String nameRole){
        return roleRepository.findFirstByName(nameRole);
    }

    @ApiOperation(value = "Заказы сделанные пользователем не оплаченные.")
    public List<Order> getOrderUserNotPay(
            @ApiParam(value = "Пользователь у которого необходимо вернуть неоплаченные заказы", required = true) User user){
        return orderRepository.findAllByUserAndPayFalse(user);
    }

    @ApiOperation(value = "Заказы сделанные пользователем оплаченные.")
    public List<Order> getOrderUserPay(
            @ApiParam(value = "Пользователь у которого необходимо вернуть оплаченные заказы", required = true) User user){
        return orderRepository.findAllByUserAndPayTrue(user);
    }

    @ApiOperation(value = "Оплаченные заказы сделанные пользователем.")
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
            @ApiParam(value = "Генератор сообщений пользователя.", required = true) MessageGenerator messageGenerator,
            @ApiParam(value = "Пользователь системы.", required = true) User user,
            @ApiParam(value = "Адрес куда отправлять продукт.", required = true) String adress,
            @ApiParam(value = "Информация об ошибках.", required = true) Map<String, String> errors){

        List<Order> ordersUser = this.getOrderUserNotPay(userService.getUserById(user.getId()));
        double realPriceOrder = this.calculetePriseForUser(ordersUser);
        adress = adress.trim();

        user = userService.getUserById(user.getId());

        if (user.getCash() < realPriceOrder){
            errors.put(ControllerUtils.constructError("price"), ControllerUtils.getMessageProperty(ConfigureErrors.NEED_MORE_CASH.toString(), "updateOrderListWhoNotPay", messageGenerator));
        }

        if (adress.equals("")){
            errors.put(ControllerUtils.constructError("adress"), ControllerUtils.getMessageProperty(ConfigureErrors.ADRESS_EMPTY.toString(), "updateOrderListWhoNotPay", messageGenerator));
        }

        if (errors.isEmpty()){
            for (Order order: ordersUser) {
                order.setAdress(adress);
                order.setPay(true);
            }

            try {
                userService.subtractCashUser(messageGenerator, user, Math.abs(this.roundPriseForUser(realPriceOrder)));
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
    private Integer roundPriseForUser(
            @ApiParam(value = "Сумма пользователя.", required = true) Double prise){

        return Integer.parseInt(String.valueOf(Math.round(prise)));
    }

    @ApiOperation(value = "Обновить статус заказа.")
    public boolean updateStatusOrders(
            @ApiParam(value = "Id заказа у которого проверяется количество товара.", required = true) long idOrder,
            @ApiParam(value = "Информация о статусе товаров.", required = true) String statusOrd
    ){

            Status status = statusRepository.findFirstByName(ReadbleUtils.createStatusOrderFromReadable(statusOrd));
            if (status != null) {
                 Order order = orderRepository.findById(idOrder).get();
                 order.setStatus(status);

                 orderRepository.save(order);
                 return true;
            }else {
                return false;
            }
    }

}

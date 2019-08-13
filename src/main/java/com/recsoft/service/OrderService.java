package com.recsoft.service;

import com.recsoft.data.entity.*;
import com.recsoft.data.exeption.OrderExeption;
import com.recsoft.data.exeption.ProductExeption;
import com.recsoft.data.exeption.UserException;
import com.recsoft.data.repository.*;
import com.recsoft.utils.ControllerUtils;
import com.recsoft.utils.ReadbleUtils;
import com.recsoft.utils.constants.ConfigureErrors;
import com.recsoft.validation.MessageGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private MessageGenerator messageGenerator;

    private SizeUserRepository sizeUserRepository;

    private ProdSizeRepository prodSizeRepository;

    private OrderProductRepository orderProductRepository;

    @Autowired
    public void setOrderProductRepository(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }

    @Autowired
    public void setProdSizeRepository(ProdSizeRepository prodSizeRepository) {
        this.prodSizeRepository = prodSizeRepository;
    }

    @Autowired
    public void setSizeUserRepository(SizeUserRepository sizeUserRepository) {
        this.sizeUserRepository = sizeUserRepository;
    }

    @Autowired
    public void setMessageGenerator(MessageGenerator messageGenerator) {
        this.messageGenerator = messageGenerator;
    }

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


    public Order getUserCart(User user){

        Order order = orderRepository.findOrderByPayFalseAndUser(user);

        if (order == null){
            Status defStatus = statusRepository.getOne(this.DEFAULT_STATUS_ORDER);
            order = orderRepository.save(new Order(user, new HashSet<>(), defStatus, "", false));
        }

        return order;
    }

    public ProdSize getProductWithSize(Product product, SizeUser sizeUser){
        return prodSizeRepository.findByProductAndSizeUser(product.getId(), sizeUser.getId());
    }

    @ApiOperation(value = "Создать заказ")
    public void addProductInCart(
            @ApiParam(value = "Id выбранного продукта.", required = true) Long idProduct,
            @ApiParam(value = "Адрес куда отправлять продукт.", required = true) Long sizeUser,
            @ApiParam(value = "Колличество выбранных продуктов.", required = true) Integer countProd,
            @ApiParam(value = "Выбранный пользователь.", required = true) User user) throws OrderExeption {

        Product product = productRepository.findById(idProduct).orElse(null);

        SizeUser sUser = sizeUserRepository.getOne(sizeUser);

        Order order = this.getUserCart(user);

        ProdSize prodSize = prodSizeRepository.findByProductAndSizeUser(product.getId(), sUser.getId());

        if (product == null || sUser == null || order == null || prodSize == null){
            throw new OrderExeption("Одна из сущностей для добавления продукта в корзину пустая.");
        }

        OrderProduct orderProduct = orderProductRepository.getOrderProductByOrderAndProductAndSizeUser(order, product, sUser);

        if (orderProduct == null) {
            orderProduct = new OrderProduct(order, product, sUser, countProd);
        } else {
            orderProduct.setCount(orderProduct.getCount() + countProd);
        }

        order.getOrderProducts().add(orderProduct);

        prodSize.setCount(prodSize.getCount() - countProd);



        prodSizeRepository.save(prodSize);

        productRepository.save(product);

        order = orderRepository.save(order);

        orderProductRepository.save(orderProduct);
        log.info("Заказ " + order.getId() + " добавлен в корзину");
    }

    @ApiOperation(value = "Удалить оплаченный заказы.")
    public void deletePayOrder(
            @ApiParam(value = "Id заказа который надо удалить", required = true) Long idOrder,
            @ApiParam(value = "Id пользователя у которого удаляют заказ", required = true) Long idUser) throws OrderExeption{

        Order order = orderRepository.findById(idOrder).orElse(null);

        if (order == null){
            throw new OrderExeption("Заказа с id " + idOrder + " нет");
        }

        List<OrderProduct> products = new ArrayList<>(order.getOrderProducts());

        if (order.getStatus().getName().equals(Status.NOT_DONE)) {
            int addCash = 0;
            for (OrderProduct orderProduct: products){
                addCash += Integer.parseInt(String.valueOf(Math.round(orderProduct.getProduct().getPrice() * orderProduct.getCount())));

                ProdSize prodSize = this.getProductWithSize(orderProduct.getProduct(), orderProduct.getSizeUser());
                prodSize.setCount(prodSize.getCount() + orderProduct.getCount());

                prodSizeRepository.save(prodSize);
            }

            userService.addCashUser(idUser, addCash);
        }


        orderProductRepository.deleteAllByIdOrder(order.getId());
        orderRepository.deleteOrderById(idOrder);
        log.info("Заказ с id = " + idOrder + " удален.");
    }

    public void deletePayProductInOrder(Long idOrderProduct, Long idUser) throws OrderExeption {
        OrderProduct orderProduct = orderProductRepository.getOne(idOrderProduct);

        if (orderProduct == null){
            throw new OrderExeption("Продукта в корзине с id " + idOrderProduct + " нет");
        }

        ProdSize prodSize = this.getProductWithSize(orderProduct.getProduct(), orderProduct.getSizeUser());

        prodSize.setCount(prodSize.getCount() + orderProduct.getCount());

        int addCash = 0;

        addCash = Integer.parseInt(String.valueOf(Math.round(orderProduct.getProduct().getPrice() * orderProduct.getCount())));

        userService.addCashUser(idUser, addCash);

        prodSizeRepository.save(prodSize);

        orderProductRepository.deleteById(idOrderProduct);
        log.info("Продукт в корзине с id = " + idOrderProduct + " удален.");
    }

    public void deleteNotPayProductInOrder(Long idOrderProduct) throws OrderExeption {
        OrderProduct orderProduct = orderProductRepository.getOne(idOrderProduct);

        if (orderProduct == null){
            throw new OrderExeption("Продукта в корзине с id " + idOrderProduct + " нет");
        }

        ProdSize prodSize = this.getProductWithSize(orderProduct.getProduct(), orderProduct.getSizeUser());

        prodSize.setCount(prodSize.getCount() + orderProduct.getCount());

        prodSizeRepository.save(prodSize);

        orderProductRepository.deleteById(idOrderProduct);
        log.info("Продукт в корзине с id = " + idOrderProduct + " удален.");
    }



    @ApiOperation(value = "Удалить неоплаченный заказы.")
    public void deleteOrderWhoNotPay(
            @ApiParam(value = "Id заказа который надо удалить", required = true) Long idOrder) throws OrderExeption{

        Order order = orderRepository.findById(idOrder).orElse(null);

        if (order == null){
            throw new OrderExeption("Заказа с id " + idOrder + " нет");
        }

        List<OrderProduct> products = new ArrayList<>(order.getOrderProducts());

        for (OrderProduct orderProduct: products){

            ProdSize prodSize = this.getProductWithSize(orderProduct.getProduct(), orderProduct.getSizeUser());
            prodSize.setCount(prodSize.getCount() + orderProduct.getCount());

            prodSizeRepository.save(prodSize);
        }

        orderProductRepository.deleteAllByIdOrder(order.getId());
        log.info("Заказы карзины с id = " + idOrder + " удалены.");
    }

    @ApiOperation(value = "Обновить количество сделанных заказов.")
    public void updateCountOrderWhoNotPay(
            @ApiParam(value = "Id заказа который надо обновить.", required = true) long idOrderProduct,
            @ApiParam(value = "Обновленное количество товаров.", required = true) int countNewProd) throws OrderExeption{

        OrderProduct orderProduct = orderProductRepository.findById(idOrderProduct).orElse(null);

        if (orderProduct == null){
            throw new OrderExeption("Продукта в корзине с id " + idOrderProduct + " нет");
        }

        ProdSize prodSize = prodSizeRepository.findByProductAndSizeUser(orderProduct.getProduct().getId(), orderProduct.getSizeUser().getId());

        int realCount = prodSize.getCount() + orderProduct.getCount();

        prodSize.setCount(realCount - countNewProd);
        orderProduct.setCount(countNewProd);

        prodSizeRepository.save(prodSize);
        orderProductRepository.save(orderProduct);

        log.info("Продукта в корзине с id = " + orderProduct.getId() + " обновлен.");
    }

    @ApiOperation(value = "Проверить правильность выбора количества товаров.")
    public boolean proveCountOrderedProduct(
            @ApiParam(value = "Id заказа у которого проверяется количество товара.", required = true) long idOrderProduct,
            @ApiParam(value = "Обновленное количество товаров.", required = true) int countProd) throws OrderExeption{

        OrderProduct orderProduct = orderProductRepository.findById(idOrderProduct).orElse(null);

        if (orderProduct == null){
            throw new OrderExeption("Продукта в корзине с id " + idOrderProduct + " нет");
        }

        SizeUser sizeUser = orderProduct.getSizeUser();

        ProdSize prodSize = prodSizeRepository.findByProductAndSizeUser(orderProduct.getProduct().getId(), orderProduct.getSizeUser().getId());

        if (prodSize.getCount() + orderProduct.getCount() < countProd || countProd < 0){
            return false;
        }
        return true;
    }

    @ApiOperation(value = "Удалить все неоплаченные заказы.")
    public void deleteAllOrderNotPayUser(
            @ApiParam(value = "Id пользователя у которого удаляют неоплаченные заказы", required = true) Long idUser) throws OrderExeption{

        User user = userService.getUserById(idUser);

        Order orderNotPayUser = orderRepository.findOrderByPayFalseAndUser(user);

        if (orderNotPayUser == null){
            throw new OrderExeption("Неоплаченных заказов у пользователя с id " + idUser + " нет");
        }

        List<ProdSize> productWhoNeedUpdate = new ArrayList<>();

        for (OrderProduct orderProduct: orderNotPayUser.getOrderProducts()){
            ProdSize prodSize = prodSizeRepository.findByProductAndSizeUser(orderProduct.getProduct().getId(), orderProduct.getSizeUser().getId());
            prodSize.setCount(prodSize.getCount() + orderProduct.getCount());
            productWhoNeedUpdate.add(prodSize);
        }

        prodSizeRepository.saveAll(productWhoNeedUpdate);

        orderProductRepository.deleteAllByIdOrder(orderNotPayUser.getId());
//!!!!!!!!!!!!!!!!!!!!
        orderRepository.deleteById(orderNotPayUser.getId());

        log.info("Заказ с id = " + user.getId() + " удален.");
    }

    @ApiOperation(value = "Возвращает роль по названию.")
    public Role getRoleByName(
            @ApiParam(value = "Имя роли.", required = true) String nameRole){
        return roleRepository.findFirstByName(nameRole);
    }

    @ApiOperation(value = "Заказы сделанные пользователем не оплаченные.")
    public Order getOrderUserNotPay(
            @ApiParam(value = "Пользователь у которого необходимо вернуть неоплаченные заказы", required = true) User user){
        return orderRepository.findOrderByPayFalseAndUser(user);
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
            @ApiParam(value = "Генератор сообщений пользователя.", required = true) Language language,
            @ApiParam(value = "Пользователь системы.", required = true) User user,
            @ApiParam(value = "Адрес куда отправлять продукт.", required = true) String adress,
            @ApiParam(value = "Информация об ошибках.", required = true) Map<String, String> errors){

        Order orderUser = this.getOrderUserNotPay(userService.getUserById(user.getId()));
        double realPriceOrder = this.calculetePriseForUser(orderUser);
        adress = adress.trim();

        user = userService.getUserById(user.getId());

        if (user.getCash() < realPriceOrder){
            errors.put(
                    ControllerUtils.constructError("price"),
                    messageGenerator.getMessageErrorProperty(
                            MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                            ConfigureErrors.NEED_MORE_CASH.toString(),
                            "updateOrderListWhoNotPay",
                            language
                    )
            );
        }

        if (adress.equals("")){
            errors.put(
                    ControllerUtils.constructError("adress"),
                    messageGenerator.getMessageErrorProperty(
                            MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                            ConfigureErrors.ADRESS_EMPTY.toString(),
                            "updateOrderListWhoNotPay",
                            language
                    )
            );
        }

        if (errors.isEmpty()){
            orderUser.setAdress(adress);
            orderUser.setPay(true);

            try {
                userService.subtractCashUser(language, user, Math.abs(this.roundPriseForUser(realPriceOrder)));
                orderRepository.save(orderUser);
            } catch (UserException userException) {
                errors.put(ControllerUtils.constructError("price"), userException.getMessage());
            }
        }

    }

    @ApiOperation(value = "Сумма цен сделаных пользователем заказов.")
    public double calculetePriseForUser(
            @ApiParam(value = "Список заказов пользователя.", required = true) Order orderUser){
        double prise = 0.0;

        for (OrderProduct orderProduct: orderUser.getOrderProducts()){
            prise += orderProduct.getCount() * orderProduct.getProduct().getPrice();
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

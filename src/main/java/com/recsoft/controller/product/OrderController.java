package com.recsoft.controller.product;

import com.recsoft.aspect.ProveRole;
import com.recsoft.data.entity.*;
import com.recsoft.data.exeption.OrderExeption;
import com.recsoft.service.OrderService;
import com.recsoft.service.ProductService;
import com.recsoft.service.UserService;
import com.recsoft.utils.ControllerUtils;
import com.recsoft.utils.ReadbleUtils;
import com.recsoft.utils.constants.ConfigureErrors;
import com.recsoft.validation.MessageGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Api(value = "Контроллер заказов",
        description = "Класс-контроллер отвечающий за работу с заказами.")
public class OrderController {

    @ApiModelProperty(
            notes = "Записывает логи сделанных действий и ошибок.",
            name="log", required=true,
            value="OrderController.class")
    private Logger log = LoggerFactory.getLogger(OrderController.class.getName());

    private OrderService orderService;

    private ProductService productService;

    private UserService userService;

    private MessageGenerator messageGenerator;

    @Autowired
    public void setMessageGenerator(MessageGenerator messageGenerator) {
        this.messageGenerator = messageGenerator;
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/add_product_in_cart/{idProduct}")
    @ApiOperation(value = "Отобразить страницу создания заказа")
    public ModelAndView showAddProductInCart(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Id продукта который заказывают.", required = true) @PathVariable String idProduct
    ) {
        ModelAndView mav = new ModelAndView("/pages/for_order/createOrder");
        user = userService.getUserById(user.getId());

        mav.addAllObjects(messageGenerator.getAllValueForPage("createOrder", user.getLanguage()));

        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "createOrder", mav);

        Product product = productService.getProductById(idProduct);

        mav.addObject("product", product);

        mav.addObject("productNotZero", productService.getRealProductWhatCountNotZero(product));

        Order order = orderService.getUserCart(user);

        this.constructPageCartUser(mav, order, user);

        return mav;
    }

    @PostMapping("/add_product_in_cart/{idProduct}")
    @ApiOperation(value = "Создать заказ пользователя")
    public ModelAndView addProductInCart(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Id продукта который заказывают.", required = true) @PathVariable String idProduct,
            @RequestParam Long sizeProd,
            @ApiParam(value = "Колличество выбранных продуктов.", required = true) @RequestParam Integer count
    ) {
        ModelAndView mav = new ModelAndView("redirect:/product/product_list");
        Map<String, String> errors = new HashMap<>();

        Product product = productService.getProductById(idProduct);

        ProdSize prodSize = orderService.getProductWithSize(product, productService.getSizeUserById(sizeProd));

        user = userService.getUserById(user.getId());

        if (count == null){
            errors.put(
                    ControllerUtils.constructError("count"),
                    messageGenerator.getMessageErrorProperty(
                            MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                            ConfigureErrors.FIELD_CAN_NOT_BE_EMPTY.toString(),
                            "createNewOrder",
                            user.getLanguage()
                    )
            );
        } else {

            if (count > prodSize.getCount()){
                errors.put(
                        ControllerUtils.constructError("count"),
                        messageGenerator.getMessageErrorProperty(
                                MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                                ConfigureErrors.COUNT_BEGER_THEN_THIS.toString(),
                                "createNewOrder",
                                user.getLanguage()
                        )
                );
            }

            if (count <= 0){
                errors.put(
                        ControllerUtils.constructError("count"),
                        messageGenerator.getMessageErrorProperty(
                                MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                                ConfigureErrors.COUNT_LESS_ZERO.toString(),
                                "createNewOrder",
                                user.getLanguage()
                        )
                );
            }
        }

        if (errors.isEmpty()) {
            try {
                orderService.addProductInCart(Long.parseLong(idProduct), sizeProd, count, user);
            } catch (OrderExeption orderExeption) {
                log.error(orderExeption.getMessage());
                return messageGenerator.createMessageForHacker(user.getLanguage());
            }
        }else{
            ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "createOrder", mav);
            mav.addAllObjects(errors);
            mav.addObject("product", productService.getProductById(idProduct));
            mav.addObject("count", count);
            mav.addObject("productNotZero", productService.getRealProductWhatCountNotZero(product));
            mav.setViewName("/pages/for_order/createOrder");
            Order order = orderService.getUserCart(user);
            this.constructPageCartUser(mav, order, user);
        }
        return mav;
    }

    @GetMapping("/cart")
    @ApiOperation(value = "Отображает корзину для покупателей или страницу продавца для работы с заказами пользователей.")
    public ModelAndView showCart(
            @ApiParam(value = "Выдергивает пользователя авторизованного") @AuthenticationPrincipal User user
    ) {
        ModelAndView mav = new ModelAndView();
        Map<String, String> errors = new HashMap<>();

        user = userService.getUserById(user.getId());



        switch (user.getRole().getName()){
            case Role.SELLER:{
                ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "showCartSeller", mav);
                mav.setViewName("/pages/for_order/showCartSeller");
                this.constructPageCartSeller(mav);
                break;
            }

            case Role.USER:{
                ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "showCartUser", mav);
                mav.setViewName("/pages/for_order/showCartUser");
                Order order = orderService.getUserCart(user);
                this.constructPageCartUser(mav, order, user);

                break;
            }

            default:{
                mav = new ModelAndView("/pages/for_menu/greeting");
                mav.addAllObjects(messageGenerator.getAllValueForPage("navbar", user.getLanguage()));
                errors.put(
                        "error",
                        messageGenerator.getMessageErrorProperty(
                                MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                                ConfigureErrors.THIS_USER_NO.toString(),
                                "showCart",
                                user.getLanguage()
                        )
                );
                mav.addAllObjects(errors);
                break;
            }
        }
        return mav;
    }

    @ApiOperation(value = "Добавляет параметры для отображения корзины покупателя.")
    private void constructPageCartUser(
            @ApiParam(value = "Модель хранящая параметры для передачи на экран.", required = true)  ModelAndView mav,
            Order order,
            @ApiParam(value = "Данные пользователя.", required = true) User user){

        if (!order.getOrderProducts().isEmpty()) {
            mav.addObject("productInCartList", ControllerUtils.sortOrderProducts(order.getOrderProducts()));
            mav.addObject("priceUser", orderService.calculetePriseForUser(order));
        }
    }

    @ApiOperation(value = "Добавляет параметры для отображения корзины продавца.")
    private void constructPageCartSeller(
            @ApiParam(value = "Модель хранящая параметры для передачи на экран.", required = true) ModelAndView mav){

        Role role = orderService.getRoleByName(Role.USER);
        mav.addObject("userList", userService.getAllUserWithRoleUser(role));
    }

    @PostMapping("/cart/create_list_order")
    @ApiOperation(value = "Оплатить сделанные заказы")
    public ModelAndView createOrderListUser(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Адрес куда отправлять продукт.", required = true) @RequestParam String adress,
            @ApiParam(value = "Список количества обновленных товаров.", required = true)  @RequestParam(value = "count_p[]", required = true) Integer[] countProducts
    ) throws OrderExeption {
        Map<String, String> errors = new HashMap<>();
        ModelAndView mav = new ModelAndView("redirect:/order/orders_user");
        user = userService.getUserById(user.getId());

        Order order = orderService.getUserCart(user);

        List<OrderProduct> orderProductList = ControllerUtils.sortOrderProducts(order.getOrderProducts());

        if (orderProductList.size() != countProducts.length){
            return messageGenerator.createMessageForHacker(user.getLanguage());
        }

        for (int i = 0; i < orderProductList.size(); i++){
            if (!orderService.proveCountOrderedProduct(orderProductList.get(i).getId(), countProducts[i])){
                return messageGenerator.createMessageForHacker(user.getLanguage());
            }
        }


        orderService.updateOrderListWhoNotPay(user.getLanguage(), user, adress, errors);

        if (!errors.isEmpty()){
            mav.setViewName("/pages/for_order/showCartUser");
            this.constructPageCartUser(mav, order, user);
            ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "showCartUser", mav);
            mav.addAllObjects(errors);
        }

        return mav;
    }


    @PostMapping(value = "/cart/delete_not_pay/{idOrderProduct}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Удалить неоплаченный заказ пользователя")
    public ResponseEntity<String> deleteOrderUserWhoNotPay(
            @ApiParam(value = "Id заказа который удаляют.", required = true)  @PathVariable String idOrderProduct,
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ) throws OrderExeption {
        orderService.deleteNotPayProductInOrder(Long.parseLong(idOrderProduct));

        user = userService.getUserById(user.getId());

        Order orderNotPay = orderService.getOrderUserNotPay(user);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("priseOrders", orderService.calculetePriseForUser(orderNotPay));

        return new ResponseEntity<>(
                JSONObject.quote(jsonObject.toString()),
                HttpStatus.OK);
    }

    @PostMapping("/cart/delete_all")
    @ApiOperation(value = "Удалить все неоплаченные заказы пользователя")
    public ModelAndView deleteOrderUserWhoNotPay(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ) throws OrderExeption {
        Order order = orderService.getUserCart(user);
        orderService.deleteOrderWhoNotPay(order.getId());

        return new ModelAndView("redirect:/order/cart");
    }

    @PostMapping(value = "/cart/change_count_prod/{idOrderProduct}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновить количество сделанных неоплаченных заказов")
    public ResponseEntity<String> changeCountOrderInCart(
            @ApiParam(value = "Id заказа который изменяют.", required = true)  @PathVariable String idOrderProduct,
            @ApiParam(value = "Обновленное количество товара.", required = true) @RequestBody  String newCountData,
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ) throws OrderExeption {

        if (orderService.proveCountOrderedProduct(Long.parseLong(idOrderProduct), Integer.parseInt(newCountData))) {
            orderService.updateCountOrderWhoNotPay(Long.parseLong(idOrderProduct), Integer.parseInt(newCountData));
            user = userService.getUserById(user.getId());

            Order orderNotPay = orderService.getOrderUserNotPay(user);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("newCountData", Integer.parseInt(newCountData));
            jsonObject.put("priseOrders", orderService.calculetePriseForUser(orderNotPay));

            return new ResponseEntity<>(
                    JSONObject.quote(jsonObject.toString()),
                    HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(
                    messageGenerator.getMessageErrorProperty(
                            MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                            ConfigureErrors.SELECT_BAD.toString(),
                            "changeCountOrderInCart",
                            user.getLanguage()
                    ),
                    HttpStatus.BAD_REQUEST);
        }
    }


    @ProveRole(nameRole = {Role.USER})
    @GetMapping("/orders_user")
    @ApiOperation(value = "Отображает корзину для покупателей или страницу продавца для работы с заказами пользователей.")
    public ModelAndView showOrdersUser(
            @ApiParam(value = "Выдергивает пользователя авторизованного") @AuthenticationPrincipal User user
    ) {
        ModelAndView mav = new ModelAndView();

        user = userService.getUserById(user.getId());

        List<Order> ordersUser = orderService.getOrderUser(user);

        mav.setViewName("/pages/for_order/showOrdersUser");
        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "showOrdersUser", mav);

        mav.addObject("orderList", ordersUser);
        mav.addObject("listReadbleStatus", orderService.createListReadbleStatusOrders(ordersUser));
        mav.addObject("priceUser", this.getPriceWithAllOrder(ordersUser));

        return mav;
    }

    private int getPriceWithAllOrder(List<Order> ordersUser) {
        int price = 0;

        for (Order order: ordersUser) {
            price += orderService.calculetePriseForUser(order);
        }
        return price;
    }

    @PostMapping("/orders_user/delete/{idOrder}")
    @ApiOperation(value = "Удалить заказ пользователя")
    public ResponseEntity<String> deleteOrderUser(
            @ApiParam(value = "Id заказа который удаляют.", required = true)  @PathVariable String idOrder,
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ) throws OrderExeption {
        orderService.deletePayOrder(Long.parseLong(idOrder), user.getId());
        user = userService.getUserById(user.getId());

        List<Order> orderListPay = orderService.getOrderUserPay(user);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cashUser", user.getCash());
        jsonObject.put("priseOrders", this.getPriceWithAllOrder(orderListPay));

        return new ResponseEntity<>(
                JSONObject.quote(jsonObject.toString()),
                HttpStatus.OK);
    }

    @PostMapping("/orders_user/show_product_in_order/{idOrder}")
    @ApiOperation(value = "Отображает корзину для покупателей или страницу продавца для работы с заказами пользователей.")
    public ModelAndView showOrdersUser(
            @ApiParam(value = "Выдергивает пользователя авторизованного") @AuthenticationPrincipal User user,
            @ApiParam(value = "??????????", required = true)  @PathVariable String idOrder
    ) {
        ModelAndView mav = new ModelAndView();

        user = userService.getUserById(user.getId());

        Order order = orderService.getOrderById(Long.parseLong(idOrder));

        mav.setViewName("/pages/for_order/containerProductsInOrder");
        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "navbar", mav);

        mav.addObject("order", order);
        this.constructPageCartUser(mav, order, user);

        mav.addObject("priceOrder", orderService.calculetePriseForUser(order));

        return mav;
    }


    @ProveRole(nameRole = {Role.SELLER})
    @GetMapping("/cart/select_user/{idUser}")
    @ApiOperation(value = "Отобразить данные пользователя выбранного продавцом.")
    public ModelAndView showSelectUser(
            @ApiParam(value = "Id выбранного пользователя.", required = true)  @PathVariable String idUser,
            @ApiParam(value = "Выдергивает пользователя авторизованного") @AuthenticationPrincipal User user
    ){
        ModelAndView mav = new ModelAndView("/pages/for_order/selectUserCart");

        user = userService.getUserById(user.getId());

        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "selectUserCart", mav);

        User selectUser = userService.getUserById(Long.parseLong(idUser));
        List<Order> ordersUser = orderService.getOrderUser(selectUser);

        mav.addObject("user", selectUser);
        mav.addObject("listStatus", ReadbleUtils.createListReadbleStatuses());
        mav.addObject("orderList", ordersUser);
        mav.addObject("listReadbleStatus", orderService.createListReadbleStatusOrders(ordersUser));
        mav.addObject("priceUser", this.getPriceWithAllOrder(ordersUser));

        return mav;
    }

    @PostMapping(value = "/cart/select_user/{idUser}/change_status", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Изменить статус сделанного заказа")
    public ResponseEntity<String> changeStatusOrderForSelectUser(
            @ApiParam(value = "Обновленный статус заказа.", required = true) @RequestBody  String stringJsonFromPage
    ){
        JSONObject jsonObject = new JSONObject(stringJsonFromPage);

        if (orderService.updateStatusOrders(jsonObject.getLong("idOrder"), jsonObject.getString("newStatusOrder"))){

            return new ResponseEntity<>(
                    HttpStatus.OK);
        }else {

            return new ResponseEntity<>(
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/cart/select_user/{idUser}/delete/{idOrder}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Удаление заказа пользователя продавцом.")
    public ResponseEntity<String> sellerDeleteOrderUser(
            @ApiParam(value = "Id удаляемого заказа.", required = true) @PathVariable String idOrder,
            @ApiParam(value = "Id пользователя у которого удаляют заказ.", required = true) @PathVariable String idUser
    ) {
        User user = userService.getUserById(Long.parseLong(idUser));

        try {
            orderService.deletePayOrder(Long.parseLong(idOrder), user.getId());
        } catch (OrderExeption orderExeption) {
            return new ResponseEntity<>(
                    HttpStatus.BAD_REQUEST
            );
        }

        List<Order> orderListPay = orderService.getOrderUserPay(user);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cashUser", user.getCash());
        jsonObject.put("priseOrders", this.getPriceWithAllOrder(orderListPay));

        return new ResponseEntity<>(
                JSONObject.quote(jsonObject.toString()),
                HttpStatus.OK);
    }

}

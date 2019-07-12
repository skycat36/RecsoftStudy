package com.recsoft.controller.product;

import com.recsoft.data.entity.Order;
import com.recsoft.data.entity.Product;
import com.recsoft.data.entity.Role;
import com.recsoft.data.entity.User;
import com.recsoft.service.OrderService;
import com.recsoft.service.ProductService;
import com.recsoft.service.UserService;
import com.recsoft.utils.ConfigureErrors;
import com.recsoft.utils.ControllerUtils;
import com.recsoft.utils.ReadbleUtils;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/create_order/{idProduct}")
    @ApiOperation(value = "Отобразить страницу создания заказа")
    public ModelAndView showNewOrder(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Id продукта который заказывают.", required = true) @PathVariable String idProduct
    ) {
        ModelAndView mav = new ModelAndView("/pages/for_order/createOrder");
        user = userService.getUserById(user.getId());

        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), mav);
        mav.addObject("product", productService.getProductById(Long.parseLong(idProduct)));
        return mav;
    }

    @PostMapping("/create_order/{idProduct}")
    @ApiOperation(value = "Создать заказ пользователя")
    public ModelAndView createNewOrder(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Id продукта который заказывают.", required = true) @PathVariable String idProduct,
            @ApiParam(value = "Колличество выбранных продуктов.", required = true) @RequestParam Integer count
    ) {
        ModelAndView mav = new ModelAndView("redirect:/product/product_list");
        Map<String, String> errors = new HashMap<>();

        Product product = productService.getProductById(Long.parseLong(idProduct));

        user = userService.getUserById(user.getId());

        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), mav);

        MessageGenerator messageGenerator;
        try {
            messageGenerator = new MessageGenerator(ControllerUtils.createPathToErroOrMessage(user.getLanguage(), MessageGenerator.FAIL_WHIS_OTHER_ERROR));
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }

        if (count == null){
            errors.put(ControllerUtils.constructError("count"), ControllerUtils.getMessageProperty(ConfigureErrors.FIELD_CAN_NOT_BE_EMPTY.toString(), "createNewOrder", messageGenerator));
        } else {

            if (count > product.getCount()){
                errors.put(ControllerUtils.constructError("count"), ControllerUtils.getMessageProperty(ConfigureErrors.COUNT_BEGER_THEN_THIS.toString(), "createNewOrder", messageGenerator));
            }

            if (count <= 0){
                errors.put(ControllerUtils.constructError("count"), ControllerUtils.getMessageProperty(ConfigureErrors.COUNT_LESS_ZERO.toString(), "createNewOrder", messageGenerator));
            }
        }

        if (errors.isEmpty()){
            if (count > product.getCount()){
                errors.put(ControllerUtils.constructError("count"), ControllerUtils.getMessageProperty(ConfigureErrors.COUNT_BEGER_THEN_THIS.toString(), "createNewOrder", messageGenerator));
            }
        }

        if (errors.isEmpty()) {
            orderService.createOrder(Long.parseLong(idProduct), "", count, user);
        }else{
            mav.addAllObjects(errors);
            mav.addObject("product", productService.getProductById(Long.parseLong(idProduct)));
            mav.addObject("count", count);
            mav.setViewName("/pages/for_order/createOrder");
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

        MessageGenerator messageGenerator;
        try {
            messageGenerator = new MessageGenerator(ControllerUtils.createPathToErroOrMessage(user.getLanguage(), MessageGenerator.FAIL_WHIS_OTHER_ERROR));
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }

        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), mav);

        switch (user.getRole().getName()){
            case Role.ADMIN: break;

            case Role.SELLER:{
                mav.setViewName("/pages/for_order/showCartSeller");
                constructPageCartSeller(mav);
                break;
            }

            case Role.USER:{
                mav.setViewName("/pages/for_order/showCartUser");
                constructPageCartUser(mav, user);

                break;
            }

            default:{
                mav = new ModelAndView("/pages/for_menu/greeting");
                errors.put("error", ControllerUtils.getMessageProperty(ConfigureErrors.THIS_USER_NO.toString(), "showCart", messageGenerator));
                mav.addAllObjects(errors);
                break;
            }
        }
        return mav;
    }

    @ApiOperation(value = "Добавляет параметры для отображения корзины покупателя.")
    private void constructPageCartUser(
            @ApiParam(value = "Модель хранящая параметры для передачи на экран.", required = true)  ModelAndView mav,
            @ApiParam(value = "Данные пользователя.", required = true) User user){

        List<Order> ordersUser = orderService.getOrderUserNotPay(user);

        mav.addObject("orderList", ordersUser);
        mav.addObject("priceUser", orderService.calculetePriseForUser(ordersUser));
    }

    @ApiOperation(value = "Добавляет параметры для отображения корзины продавца.")
    private void constructPageCartSeller(
            @ApiParam(value = "Модель хранящая параметры для передачи на экран.", required = true) ModelAndView mav){

        Role buyer = orderService.getRoleByName(Role.USER);
        mav.addObject("userList", userService.getAllUserWithRoleUser(buyer));
    }

    @PostMapping("/orders_user/delete/{idOrder}")
    @ApiOperation(value = "Удалить заказ пользователя")
    public ResponseEntity<String> deleteOrderUser(
            @ApiParam(value = "Id заказа который удаляют.", required = true)  @PathVariable String idOrder,
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ){
        orderService.deletePayOrder(Long.parseLong(idOrder), user.getId());
        user = userService.getUserById(user.getId());

        List<Order> orderListPay = orderService.getOrderUserPay(user);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cashUser", user.getCash());
        jsonObject.put("priseOrders", orderService.calculetePriseForUser(orderListPay));

        return new ResponseEntity<>(
                JSONObject.quote(jsonObject.toString()),
                HttpStatus.OK);
    }

    @PostMapping("/cart/delete_all")
    @ApiOperation(value = "Удалить все неоплаченные заказы пользователя")
    public ModelAndView deleteOrderUserWhoNotPay(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ){
        orderService.deleteAllOrdersNotPayUser(user.getId());

        return new ModelAndView("redirect:/order/cart");
    }

    @PostMapping(value = "/cart/delete_not_pay/{idOrder}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Удалить неоплаченный заказ пользователя")
    public ResponseEntity<String> deleteOrderUserWhoNotPay(
            @ApiParam(value = "Id заказа который удаляют.", required = true)  @PathVariable String idOrder,
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ){
        orderService.deleteOrderWhoNotPay(Long.parseLong(idOrder));

        user = userService.getUserById(user.getId());

        List<Order> orderListNotPay = orderService.getOrderUserNotPay(user);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("priseOrders", orderService.calculetePriseForUser(orderListNotPay));

        return new ResponseEntity<>(
                JSONObject.quote(jsonObject.toString()),
                HttpStatus.OK);
    }

    @PostMapping(value = "/cart/change_count_prod/{idOrder}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновить количество сделанных неоплаченных заказов")
    public ResponseEntity<String> changeCountOrderInCart(
            @ApiParam(value = "Id заказа который изменяют.", required = true)  @PathVariable String idOrder,
            @ApiParam(value = "Обновленное количество товара.", required = true) @RequestBody  String newCountData,
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ){

        MessageGenerator messageGenerator;
        try {
            messageGenerator = new MessageGenerator(ControllerUtils.createPathToErroOrMessage(user.getLanguage(), MessageGenerator.FAIL_WHIS_OTHER_ERROR));
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(
                    "Language nothing",
                    HttpStatus.BAD_REQUEST);
        }

        if (orderService.proveCountOrderedProduct(Long.parseLong(idOrder), Integer.parseInt(newCountData))) {
            orderService.updateCountOrderWhoNotPay(Long.parseLong(idOrder), Integer.parseInt(newCountData));
            user = userService.getUserById(user.getId());

            List<Order> orderListNotPay = orderService.getOrderUserNotPay(user);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("newCountData", Integer.parseInt(newCountData));
            jsonObject.put("priseOrders", orderService.calculetePriseForUser(orderListNotPay));

            return new ResponseEntity<>(
                    JSONObject.quote(jsonObject.toString()),
                    HttpStatus.OK);
        }
        else {

            return new ResponseEntity<>(
                    ControllerUtils.getMessageProperty(ConfigureErrors.SELECT_BAD.toString(), "changeCountOrderInCart", messageGenerator),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cart/create_list_order")
    @ApiOperation(value = "Оплатить сделанные заказы")
    public ModelAndView createOrderListUser(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Адрес куда отправлять продукт.", required = true) @RequestParam String adress,
            @ApiParam(value = "Список количества обновленных товаров .", required = true)  @RequestParam(value = "count_p[]", required = true) Integer[] countProducts
    ){
        Map<String, String> errors = new HashMap<>();
        ModelAndView mav = new ModelAndView("redirect:/order/orders_user");
        user = userService.getUserById(user.getId());

        MessageGenerator messageGenerator;
        try {
            messageGenerator = new MessageGenerator(ControllerUtils.createPathToErroOrMessage(user.getLanguage(), MessageGenerator.FAIL_WHIS_OTHER_ERROR));
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }

        List<Order> ordersNotPay = orderService.getOrderUserNotPay(user);
        for (int i = 0; i < ordersNotPay.size(); i++){
            if (!ordersNotPay.get(i).getCount().equals(countProducts[i])){
                try {
                    return ControllerUtils.createMessageForHacker(user.getLanguage());
                } catch (IOException e) {
                    log.error(e.getMessage());
                    return null;
                }
            }
        }

        orderService.updateOrderListWhoNotPay(messageGenerator, user, adress, errors);

        if (!errors.isEmpty()){
            mav.setViewName("/pages/for_order/showCartUser");
            this.constructPageCartUser(mav, user);
            ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), mav);
            mav.addAllObjects(errors);
        }

        return mav;
    }

    @GetMapping("/orders_user")
    @ApiOperation(value = "Отображает корзину для покупателей или страницу продавца для работы с заказами пользователей.")
    public ModelAndView showOrdersUser(
            @ApiParam(value = "Выдергивает пользователя авторизованного") @AuthenticationPrincipal User user
    ) {
        ModelAndView mav = new ModelAndView();

        user = userService.getUserById(user.getId());

        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), mav);

        switch (user.getRole().getName()){
            case Role.USER:{
                mav.setViewName("/pages/for_order/showOrdersUser");
                List<Order> ordersUser = orderService.getOrderUser(user);
                mav.addObject("orderList", ordersUser);
                mav.addObject("listReadbleStatus", orderService.createListReadbleStatusOrders(ordersUser));
                mav.addObject("priceUser", orderService.calculetePriseForUser(ordersUser));
                break;
            }
            default:{
                try {
                    return ControllerUtils.createMessageForHacker(user.getLanguage());
                } catch (IOException e) {
                    log.error(e.getMessage());
                    return null;
                }
            }
        }
        return mav;
    }

    @GetMapping("/cart/select_user/{idUser}")
    @ApiOperation(value = "Отобразить данные пользователя выбранного продавцом.")
    public ModelAndView showSelectUser(
            @ApiParam(value = "Id выбранного пользователя.", required = true)  @PathVariable String idUser,
            @ApiParam(value = "Выдергивает пользователя авторизованного") @AuthenticationPrincipal User user
    ){
        ModelAndView mav = new ModelAndView("/pages/for_order/selectUserCart");

        user = userService.getUserById(user.getId());

        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), mav);

        if (user.getRole().getName().equals(Role.SELLER) || user.getRole().getName().equals(Role.ADMIN)){
            User selectUser = userService.getUserById(Long.parseLong(idUser));
            List<Order> ordersUser = orderService.getOrderUser(userService.getUserById(Long.parseLong(idUser)));

            mav.addObject("user", selectUser);
            mav.addObject("listStatus", ReadbleUtils.createListReadbleStatuses());
            mav.addObject("orderList", ordersUser);
            mav.addObject("listReadbleStatus", orderService.createListReadbleStatusOrders(ordersUser));
            mav.addObject("priceUser", orderService.calculetePriseForUser(ordersUser));
        }else {
            try {
                mav = ControllerUtils.createMessageForHacker(user.getLanguage());
            } catch (IOException e) {
                log.error(e.getMessage());
                return null;
            }
        }

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
    ){
        User user = userService.getUserById(Long.parseLong(idUser));

        orderService.deletePayOrder(Long.parseLong(idOrder), user.getId());

        List<Order> orderListPay = orderService.getOrderUserPay(user);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cashUser", user.getCash());
        jsonObject.put("priseOrders", orderService.calculetePriseForUser(orderListPay));

        return new ResponseEntity<>(
                JSONObject.quote(jsonObject.toString()),
                HttpStatus.OK);
    }

}

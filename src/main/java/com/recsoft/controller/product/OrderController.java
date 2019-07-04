package com.recsoft.controller.product;

import com.recsoft.data.entity.Order;
import com.recsoft.data.entity.Product;
import com.recsoft.data.entity.Role;
import com.recsoft.data.entity.User;
import com.recsoft.service.OrderService;
import com.recsoft.service.ProductService;
import com.recsoft.service.UserService;
import com.recsoft.utils.ControllerUtils;
import com.recsoft.utils.ReadbleUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO при увеличении количества товаров в корзине происходит обновление инфы на сервере


@RestController
@RequestMapping("/order")
@Api(value = "Контроллер заказов", description = "Класс-контроллер отвечающий за работу с заказами.")
public class OrderController {

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
        ModelAndView mnv = new ModelAndView("/pages/for_order/createOrder");
        mnv.addObject("product", productService.getProductById(Long.parseLong(idProduct)));
        return mnv;
    }

    @PostMapping("/create_order/{idProduct}")
    @ApiOperation(value = "Создать заказ пользователя")
    public ModelAndView createNewOrder(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Id продукта который заказывают.", required = true) @PathVariable String idProduct,
            @ApiParam(value = "Колличество выбранных продуктов.", required = true) @RequestParam Integer count
    ) {
        ModelAndView mnv = new ModelAndView("redirect:/product/product_list");
        Map<String, String> errors = new HashMap<>();

        Product product = productService.getProductById(Long.parseLong(idProduct));

        user = userService.getUserById(user.getId());

        if (count > product.getCount()){
            errors.put(ControllerUtils.constructError("count"), "Количество выбранных товаров ме может быть больше имеющихся.");
        }

        if (count <= 0){
            errors.put(ControllerUtils.constructError("count"), "Поле не может быть отрицательным или равным 0.");
        }

        if (count == null){
            errors.put(ControllerUtils.constructError("count"), "Поле количества товаров не может быть пустым.");
        }

        if (errors.isEmpty()){
            if (count > product.getCount()){
                errors.put(ControllerUtils.constructError("count"), "Поле количества товаров не может больше имеющихся.");
            }
        }

        if (errors.isEmpty()) {
            orderService.createOrder(Long.parseLong(idProduct), "", count, user);
        }else{
            mnv.addAllObjects(errors);
            mnv.addObject("product", productService.getProductById(Long.parseLong(idProduct)));
            mnv.addObject("count", count);
            mnv.setViewName("/pages/for_order/createOrder");
        }
        return mnv;
    }

    @GetMapping("/cart")
    @ApiOperation(value = "Отображает корзину для покупателей или страницу продавца для работы с заказами пользователей.")
    public ModelAndView showCart(
            @ApiParam(value = "Выдергивает пользователя авторизованного") @AuthenticationPrincipal User user
    ) {
        ModelAndView mnv = new ModelAndView();
        Map<String, String> errors = new HashMap<>();

        user = userService.getUserById(user.getId());

        mnv.addObject("user", user);

        switch (user.getRole().getName()){
            case ControllerUtils.ADMIN: break;

            case ControllerUtils.SELLER:{
                mnv.setViewName("/pages/for_order/showCartSeller");
                constructPageCartSeller(mnv);
                break;
            }

            case ControllerUtils.USER:{
                mnv.setViewName("/pages/for_order/showCartUser");
                constructPageCartUser(mnv, user);

                break;
            }

            default:{
                mnv = new ModelAndView("/pages/for_menu/greeting");
                errors.put("error", "Карзины для такого уровня пользователя нет.");
                mnv.addAllObjects(errors);
                break;
            }
        }
        return mnv;
    }

    @ApiOperation(value = "Добавляет параметры для отображения корзины покупателя.")
    private void constructPageCartUser(
            @ApiParam(value = "Модель хранящая параметры для передачи на экран.", required = true)  ModelAndView mnv,
            @ApiParam(value = "Данные пользователя.", required = true) User user){
        List<Order> ordersUser = orderService.getOrderUserNotPay(user);
        mnv.addObject("orderList", ordersUser);
        mnv.addObject("priceUser", orderService.calculetePriseForUser(ordersUser));
    }

    @ApiOperation(value = "Добавляет параметры для отображения корзины продавца.")
    private void constructPageCartSeller(
            @ApiParam(value = "Модель хранящая параметры для передачи на экран.", required = true) ModelAndView mnv){
        Role buyer = orderService.getRoleByName(ControllerUtils.USER);
        mnv.addObject("userList", userService.getAllUserWithRoleUser(buyer));
    }

    @PostMapping("/cart/delete/{idOrder}")
    @ApiOperation(value = "Удалить заказ пользователя")
    public ModelAndView deleteOrderUser(
            @ApiParam(value = "Id продукта который заказывают.", required = true)  @PathVariable String idOrder,
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ){
        orderService.deleteOrder(Long.parseLong(idOrder), user.getId());
        return new ModelAndView("redirect:/order/orders_user");
    }

    @PostMapping("/cart/delete_not_pay/{idOrder}")
    @ApiOperation(value = "Удалить заказ пользователя")
    public ModelAndView deleteOrderUserWhoNotPay(
            @ApiParam(value = "Id продукта который заказывают.", required = true)  @PathVariable String idOrder
    ){
        orderService.deleteOrderWhoNotPay(Long.parseLong(idOrder));
        return new ModelAndView("redirect:/order/cart");
    }

    @PostMapping("/cart/delete_all")
    @ApiOperation(value = "Удалить все заказы пользователя")
    public ModelAndView deleteOrderUserWhoNotPay(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ){
        orderService.deleteAllOrdersNotPayUser(user.getId());
        return new ModelAndView("redirect:/order/cart");
    }

    @PostMapping(value = "/cart/change_count_prod/{idOrder}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновить список сделанных заказов")
    public ResponseEntity<String> changeCountOrderInCart(
            @ApiParam(value = "Id продукта который заказывают.", required = true)  @PathVariable String idOrder,
            @ApiParam(value = "Обновленное количество товара.", required = true) @RequestBody  String newCountData
    ){
        if (orderService.proveCountOrderedProduct(Long.parseLong(idOrder), Integer.parseInt(newCountData))) {
            orderService.updateCountOrderWhoNotPay(Long.parseLong(idOrder), Integer.parseInt(newCountData));
            return new ResponseEntity<>(
                    newCountData,
                    HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(
                    "Количество товаров выбрано неправильно.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cart/create_list_order")
    @ApiOperation(value = "Обновить список сделанных заказов")
    public ModelAndView createOrderListUser(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Адрес куда отправлять продукт.", required = true) @RequestParam String adress,
            @ApiParam(value = "Список количества обновленных товаров .", required = true)  @RequestParam(value = "count_p[]", required = true) Integer[] countProducts
    ){
        Map<String, String> errors = new HashMap<>();
        ModelAndView mnv = new ModelAndView("redirect:/order/orders_user");
        user = userService.getUserById(user.getId());

        List<Order> ordersNotPay = orderService.getOrderUserNotPay(user);
        for (int i = 0; i < ordersNotPay.size(); i++){
            if (!ordersNotPay.get(i).getCount().equals(countProducts[i])){
                return ControllerUtils.createMessageForHacker();
            }
        }

        orderService.updateOrderListWhoNotPay(user, adress, errors);

        if (!errors.isEmpty()){
            mnv.setViewName("/pages/for_order/showCartUser");
            this.constructPageCartUser(mnv, user);
            mnv.addObject("user", user);
            mnv.addAllObjects(errors);
        }

        return mnv;
    }

    @GetMapping("/orders_user")
    @ApiOperation(value = "Отображает корзину для покупателей или страницу продавца для работы с заказами пользователей.")
    public ModelAndView showOrdersUser(
            @ApiParam(value = "Выдергивает пользователя авторизованного") @AuthenticationPrincipal User user
    ) {
        ModelAndView mnv = new ModelAndView();

        user = userService.getUserById(user.getId());

        mnv.addObject("user", user);

        switch (user.getRole().getName()){
            case ControllerUtils.USER:{
                mnv.setViewName("/pages/for_order/showOrdersUser");
                List<Order> ordersUser = orderService.getOrderUser(user);
                mnv.addObject("orderList", ordersUser);
                mnv.addObject("listReadbleStatus", orderService.createListReadbleStatusOrders(ordersUser));
                mnv.addObject("priceUser", orderService.calculetePriseForUser(ordersUser));
                break;
            }

            default:{
                return ControllerUtils.createMessageForHacker();
            }
        }
        return mnv;
    }

    @GetMapping("/cart/select_user/{idUser}")
    @ApiOperation(value = "Отобразить данные пользователя выбранного продавцом.")
    public ModelAndView showSelectUser(
            @ApiParam(value = "Id выбранного пользователя.", required = true)  @PathVariable String idUser,
            @ApiParam(value = "Выдергивает пользователя авторизованного") @AuthenticationPrincipal User user
    ){
        ModelAndView mav = new ModelAndView("/pages/for_order/selectUserCart");

        user = userService.getUserById(user.getId());
        if (user.getRole().getName().equals(ControllerUtils.SELLER) || user.getRole().getName().equals(ControllerUtils.ADMIN)){
            User selectUser = userService.getUserById(Long.parseLong(idUser));
            List<Order> ordersUser = orderService.getOrderUser(userService.getUserById(Long.parseLong(idUser)));

            mav.addObject("user", selectUser);
            mav.addObject("listStatus", ReadbleUtils.createListReadbleStatuses());
            mav.addObject("orderList", ordersUser);
            mav.addObject("listReadbleStatus", orderService.createListReadbleStatusOrders(ordersUser));
            mav.addObject("priceUser", orderService.calculetePriseForUser(ordersUser));
        }else {
            mav = ControllerUtils.createMessageForHacker();
        }

        return mav;
    }

    @PostMapping("/cart/select_user/{idUser}")
    @ApiOperation(value = "Обновить данные о заказах выбранного пользователя.")
    public ModelAndView updateOrdersUser(
            @ApiParam(value = "Id выбранного пользователя.", required = true)  @PathVariable String idUser,
            @ApiParam(value = "Список обновленных статусов заказов.", required = true)  @RequestParam(value = "statusOrd[]", required = false) String[] statusOrd,
            @ApiParam(value = "Событие которое будет происходить при обновлении данных.", required = true)  @RequestParam(required = false) String bUpdate,
            @ApiParam(value = "Событие которое будет происходить при удалении заказа.", required = true)  @RequestParam(required = false) String bDelete,
            @ApiParam(value = "Для перенаправления на post запрос при совершении действия.", required = true) HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();

        if (bDelete != null){
            request.setAttribute(
                    View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return new ModelAndView("redirect:/order/cart/select_user/" + idUser + "/delete/" + bDelete + "");
        }

        ModelAndView mnv = new ModelAndView("redirect:/order/cart");

        if (errors.isEmpty()) {
            orderService.updateStatusOrders(userService.getUserById(Long.parseLong(idUser)), Arrays.asList(statusOrd));
        }else {
            mnv = new ModelAndView("/pages/for_order/selectUserCart");

            List<Order> ordersUser = orderService.getOrderUser(userService.getUserById(Long.parseLong(idUser)));

            mnv.addObject("user", userService.getUserById(Long.parseLong(idUser)));
            mnv.addObject("listStatus", ReadbleUtils.createListReadbleStatuses());
            mnv.addObject("orderList", ordersUser);
            mnv.addObject("listReadbleStatus", orderService.createListReadbleStatusOrders(ordersUser));
            mnv.addObject("priceUser", orderService.calculetePriseForUser(ordersUser));
            mnv.addAllObjects(errors);
        }

        return mnv;
    }

    @PostMapping("/cart/select_user/{idUser}/delete/{idOrder}")
    @ApiOperation(value = "Удаление заказа пользователя продавцом.")
    public ModelAndView sellerDeleteOrderUser(
            @ApiParam(value = "Id удаляемого заказа.", required = true) @PathVariable String idOrder,
            @ApiParam(value = "Id пользователя у которого удаляют заказ.", required = true) @PathVariable String idUser
    ){
        orderService.deleteOrder(Long.parseLong(idOrder), Long.parseLong(idUser));
        return new ModelAndView("redirect:/order/cart/select_user/" + idUser);
    }

}

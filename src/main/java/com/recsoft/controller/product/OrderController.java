package com.recsoft.controller.product;

import com.recsoft.data.entity.*;
import com.recsoft.data.exeption.UserExeption;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
@Api(value = "Контроллер заказов", description = "Класс-контроллер отвечающий за работу с заказами.")
public class OrderController {

    private final String ADMIN = "admin", SELLER = "seller", USER = "user";

    private final OrderService orderService;

    private final ProductService productService;

    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, ProductService productService, UserService userService) {
        this.orderService = orderService;
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/create_order/{idProduct}")
    @ApiOperation(value = "Отобразить страницу создания заказа")
    public ModelAndView showNewOrder(
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
            @ApiParam(value = "Адрес куда отправлять продукт.", required = true) @RequestParam String adress,
            @ApiParam(value = "Колличество выбранных продуктов.", required = true) @RequestParam String count
    ) {
        ModelAndView mnv = new ModelAndView("redirect:/product/product_list");
        Map<String, String> errors = new HashMap<>();

        Product product = productService.getProductById(Long.parseLong(idProduct));

        user = userService.getUserById(user.getId());

        if (adress.equals("")){
            errors.put(ControllerUtils.constructError("adress"), "Поле адреса не может быть пустым.");
        }

        if (count.equals("")){
            errors.put(ControllerUtils.constructError("count"), "Поле количества товаров не может быть пустым.");
        }

        if (errors.isEmpty()){

            if (Integer.parseInt(count) > product.getCount()){
                errors.put(ControllerUtils.constructError("count"), "Поле количества товаров не может больше имеющихся.");
            }

            try {
                userService.subtractCashUser(user.getId(), roundPriseForUser(product.getPrice() * Integer.parseInt(count)));
            } catch (UserExeption userExeption) {
                errors.put(ControllerUtils.constructError("price"), userExeption.getMessage());
            }
        }

        if (errors.isEmpty()) {
            orderService.createOrder(Long.parseLong(idProduct), adress, Integer.parseInt(count), user);
        }else{
            mnv.addAllObjects(errors);
            mnv.addObject("product", productService.getProductById(Long.parseLong(idProduct)));
            mnv.setViewName("/pages/for_order/createOrder");
        }
        return mnv;
    }

    @GetMapping("/basket")
    @ApiOperation(value = "Отображает корзину для покупателей или страницу продавца для работы с заказами пользователей.")
    public ModelAndView showBasket(
            @ApiParam(value = "Выдергивает пользователя авторизованного") @AuthenticationPrincipal User user
    ) {
        ModelAndView mnv = new ModelAndView();
        Map<String, String> errors = new HashMap<>();

        user = userService.getUserById(user.getId());

        mnv.addObject("user", user);

        switch (user.getRole().getName()){
            case ADMIN: break;

            case SELLER:{
                mnv.setViewName("/pages/for_order/showBasketSeller");
                constructPageBasketSeller(mnv);
                break;
            }

            case USER:{
                mnv.setViewName("/pages/for_order/showBasketUser");
                constructPageBasketUser(mnv, user);

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
    private void constructPageBasketUser(
            @ApiParam(value = "Модель хранящая параметры для передачи на экран.", required = true)  ModelAndView mnv,
            @ApiParam(value = "Данные пользователя.", required = true) User user){
        List<Order> ordersUser = orderService.getOrderUser(user);
        mnv.addObject("orderList", ordersUser);
        mnv.addObject("listReadbleStatus", createListReadbleStatusOrders(ordersUser));
        mnv.addObject("priceUser", calculetePriseForUser(ordersUser));
    }

    @ApiOperation(value = "Добавляет параметры для отображения корзины продавца.")
    private void constructPageBasketSeller(
            @ApiParam(value = "Модель хранящая параметры для передачи на экран.", required = true) ModelAndView mnv){
        Role buyer = orderService.getRoleByName(USER);
        mnv.addObject("userList", userService.getAllUserWithRoleUser(buyer));
    }

    @ApiOperation(value = "Создание читаемых статусов заказа.")
    private List<String> createListReadbleStatusOrders(
            @ApiParam(value = "Список заказов пользователя.", required = true) List<Order> orders){
        List<String> listReadbleStatus = new ArrayList<>();

        for (Order order: orders){
            listReadbleStatus.add(ReadbleUtils.createReadableStatusOrder(order.getStatus().getName()));
        }
        return listReadbleStatus;
    }

    @ApiOperation(value = "Сумма цен сделаных пользователем заказов.")
    private Double calculetePriseForUser(
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

    @PostMapping("/basket/delete/{idOrder}")
    @ApiOperation(value = "Удалить заказ пользователя")
    public ModelAndView deleteOrderUser(
            @ApiParam(value = "Id продукта который заказывают.", required = true)  @PathVariable String idOrder,
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ){
        orderService.deleteOrder(Long.parseLong(idOrder), user.getId());
        return new ModelAndView("redirect:/order/basket");
    }

    @PostMapping("/basket/delete_all")
    @ApiOperation(value = "Удалить все заказы пользователя")
    public ModelAndView deleteOrderUser(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ){
        orderService.deleteAllOrdersUser(user.getId());
        return new ModelAndView("redirect:/order/basket");
    }

    @PostMapping("/basket/update")
    @ApiOperation(value = "Удалить заказ пользователя")
    public ModelAndView updateOrderUser(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Список количества обновленных товаров .", required = true)  @RequestParam(value = "count_p[]", required = true) String[] countProducts
    ){
        Map<String, String> errors = new HashMap<>();
        List<Order> ordersUser = orderService.getOrderUser(userService.getUserById(user.getId()));
        List<Order> orderWhoNeedUpdate = new ArrayList<>();
        List<Product> productListWhoNeedUpdate = new ArrayList<>();
        Double realPriceUser = calculetePriseForUser(ordersUser);
        ModelAndView mnv = new ModelAndView("redirect:/order/basket");

        for (int i = 0; i < ordersUser.size(); i++) {
            Integer realSelectProd = Integer.parseInt(countProducts[i]);
            if (!ordersUser.get(i).getCount().equals(realSelectProd)) {
                Product productOrd = new Product();
                productOrd = ordersUser.get(i).getProduct();
                productOrd.setCount(productOrd.getCount() + ordersUser.get(i).getCount());
                productOrd.setCount(productOrd.getCount() - realSelectProd);
                ordersUser.get(i).setCount(realSelectProd);
                orderWhoNeedUpdate.add(ordersUser.get(i));
                productListWhoNeedUpdate.add(productOrd);
            }
        }

        Double newPriceUser = calculetePriseForUser(ordersUser);
        realPriceUser -= newPriceUser;

        if (realPriceUser < 0){
            try {
                userService.subtractCashUser(user.getId(), Math.abs(roundPriseForUser(realPriceUser)));
            } catch (UserExeption userExeption) {
                errors.put(ControllerUtils.constructError("price"), userExeption.getMessage());
            }
        }else {
            userService.addCashUser(user.getId(), roundPriseForUser(realPriceUser));
        }

        if (errors.isEmpty()){
            productService.updateProductList(productListWhoNeedUpdate);
            orderService.updateOrderList(orderWhoNeedUpdate);
        }else{
            mnv.setViewName("/pages/for_order/showBasketUser");
            constructPageBasketUser(mnv, user);
            mnv.addAllObjects(errors);
        }

        return mnv;
    }

    @GetMapping("/basket/select_user/{idUser}")
    @ApiOperation(value = "Отобразить данные пользователя выбранного продавцом.")
    public ModelAndView showSelectUser(
            @ApiParam(value = "Id выбранного пользователя.", required = true)  @PathVariable String idUser
    ){
        ModelAndView mav = new ModelAndView("/pages/for_order/selectUserBasket");

        List<Order> ordersUser = orderService.getOrderUser(userService.getUserById(Long.parseLong(idUser)));
        User user = userService.getUserById(Long.parseLong(idUser));
        mav.addObject("cash", user.getCash().toString());
        mav.addObject("user", user);
        mav.addObject("listStatus", ReadbleUtils.createListReadbleStatuses());
        mav.addObject("orderList", ordersUser);
        mav.addObject("listReadbleStatus", createListReadbleStatusOrders(ordersUser));
        mav.addObject("priceUser", calculetePriseForUser(ordersUser));

        return mav;
    }

    @PostMapping("/basket/select_user/{idUser}")
    @ApiOperation(value = "Обновить данные о заказах выбранного пользователя.")
    public ModelAndView updateOrdersUser(
            @ApiParam(value = "Id выбранного пользователя.", required = true)  @PathVariable String idUser,
            @ApiParam(value = "Новое значение кошелька пользователя.", required = true)  @RequestParam String cash,
            @ApiParam(value = "Список обновленных статусов заказов.", required = true)  @RequestParam(value = "statusOrd[]", required = false) String[] statusOrd,
            @ApiParam(value = "Событие которое будет происходить при обновлении данных.", required = true)  @RequestParam(required = false) String bUpdate,
            @ApiParam(value = "Событие которое будет происходить при удалении заказа.", required = true)  @RequestParam(required = false) String bDelete,
            @ApiParam(value = "Для перенаправления на post запрос при совершении действия.", required = true) HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();

        if (bDelete != null){
            request.setAttribute(
                    View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return new ModelAndView("redirect:/order/basket/select_user/" + idUser + "/delete/" + bDelete + "");
        }

        ModelAndView mnv = new ModelAndView("redirect:/order/basket");

        if (cash.equals("")){
            errors.put(ControllerUtils.constructError("cash"), "Поле кошелька не может быть пустым.");
        }

        if (errors.isEmpty()) {
            List<Order> ordersUser = orderService.getOrderUser(userService.getUserById(Long.parseLong(idUser)));
            List<Order> orderWhoNeedUpdate = new ArrayList<>();

            for (int i = 0; i < ordersUser.size(); i++) {
                Status statusRealOrder = ordersUser.get(i).getStatus();
                String newStatusName = ReadbleUtils.createStatusOrderFromReadable(statusOrd[i]);
                if (!statusRealOrder.getName().equals(newStatusName)) {
                    ordersUser.get(i).setStatus(orderService.getStatusByName(newStatusName));
                    orderWhoNeedUpdate.add(ordersUser.get(i));
                }
            }
            orderService.updateOrderList(orderWhoNeedUpdate);
        }else {

            mnv = new ModelAndView("/pages/for_order/selectUserBasket");

            List<Order> ordersUser = orderService.getOrderUser(userService.getUserById(Long.parseLong(idUser)));

            mnv.addObject("user", userService.getUserById(Long.parseLong(idUser)));
            mnv.addObject("listStatus", ReadbleUtils.createListReadbleStatuses());
            mnv.addObject("orderList", ordersUser);
            mnv.addObject("listReadbleStatus", createListReadbleStatusOrders(ordersUser));
            mnv.addObject("priceUser", calculetePriseForUser(ordersUser));
            mnv.addAllObjects(errors);
        }

        return mnv;
    }

    @PostMapping("/basket/select_user/{idUser}/delete/{idOrder}")
    @ApiOperation(value = "Удаление заказа пользователя продавцом.")
    public ModelAndView sellerDeleteOrderUser(
            @ApiParam(value = "Id удаляемого заказа.", required = true) @PathVariable String idOrder,
            @ApiParam(value = "Id пользователя у которого удаляют заказ.", required = true) @PathVariable String idUser
    ){
        orderService.deleteOrder(Long.parseLong(idOrder), Long.parseLong(idUser));
        return new ModelAndView("redirect:/order/basket/select_user/" + idUser);
    }

}

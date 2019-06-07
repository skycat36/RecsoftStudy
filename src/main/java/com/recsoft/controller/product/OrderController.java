package com.recsoft.controller.product;

import com.recsoft.utils.ControllerUtils;
import com.recsoft.data.entity.Order;
import com.recsoft.data.entity.Role;
import com.recsoft.data.entity.User;
import com.recsoft.service.OrderService;
import com.recsoft.service.ProductService;
import com.recsoft.service.UserService;
import com.recsoft.utils.ReadbleUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final String ADMIN = "admin", SELLER = "seller", USER = "user";

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("/create_order/{idProduct}")
    @ApiOperation(value = "List products")
    public ModelAndView showNewOrder(
            @PathVariable String idProduct
    ) {
        ModelAndView mnv = new ModelAndView("/pages/for_order/createOrder");
        mnv.addObject("product", productService.getProductById(Long.parseLong(idProduct)));
        return mnv;
    }

    @PostMapping("/create_order/{idProduct}")
    @ApiOperation(value = "List products")
    public ModelAndView createNewOrder(
            @AuthenticationPrincipal User user,
            @PathVariable String idProduct,
            @RequestParam String adress,
            @RequestParam String count
    ) {
        ModelAndView mnv = new ModelAndView("redirect:/product/product_list");
        Map<String, String> errors = new HashMap<>();

        if (adress.equals("")){
            errors.put(ControllerUtils.constructError("adress"), "Поле адреса не может быть пустым.");
        }

        if (adress.equals("")){
            errors.put(ControllerUtils.constructError("count"), "Поле количества товаров не может быть пустым.");
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
    @ApiOperation(value = "List products")
    public ModelAndView showBasket(
            @AuthenticationPrincipal User user
    ) {
        ModelAndView mnv = new ModelAndView();
        Map<String, String> errors = new HashMap<>();

        mnv.addObject("user", user);
        mnv.addObject("cash", user.getCash());

        switch (user.getRole().getName()){
            case ADMIN: break;

            case SELLER:{
                mnv = new ModelAndView("/pages/for_order/showBasketSeller");
                constructPageSeller(mnv);
                break;
            }

            case USER:{
                mnv = new ModelAndView("/pages/for_order/showBasketUser");
                constructPageUser(mnv, user);

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

    private void constructPageUser(ModelAndView mnv, User user){
        List<Order> ordersUser = orderService.getOrderUser(user);
        mnv.addObject("orderList", ordersUser);
        mnv.addObject("listReadbleStatus", createListReadbleStatusOrders(ordersUser));
        mnv.addObject("priceUser", calculetePriseForUser(ordersUser));
    }

    private void constructPageSeller(ModelAndView mnv){
        Role buyer = orderService.getRoleByName(USER);
        mnv.addObject("userList", userService.getAllUserWithRoleUser(buyer));
    }

    private List<String> createListReadbleStatusOrders(List<Order> orders){
        List<String> listReadbleStatus = new ArrayList<>();

        for (Order order: orders){
            listReadbleStatus.add(ReadbleUtils.createReadableStatusOrder(order.getStatus().getName()));
        }
        return listReadbleStatus;
    }

    private Double calculetePriseForUser(List<Order> ordersUser){
        Double prise = 0.0;

        for (Order order: ordersUser){
            prise += order.getCount() * order.getProduct().getPrice();
        }

        return prise;
    }


    @PostMapping("/basket/delete/{idOrder}")
    public ModelAndView deleteOrderUser(
            @PathVariable String idOrder
    ){
        orderService.deleteOrder(Long.parseLong(idOrder));
        return new ModelAndView("redirect:/order/basket");
    }

}

package com.recsoft.controller.other;

import com.recsoft.data.entity.Status;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/* Отображение стартовой страницы и страниц входа и выхода.
 * @author Евгений Попов */
@RestController
@RequestMapping("/")
@Api(value = "Start Resource", description = "shows start view")
public class GreetingController {

    /*
     * @return ModelAndView - отображение стартовой страницы приложения.*/
    @GetMapping
    public ModelAndView greeting(){
        ModelAndView mav = new ModelAndView("/pages/for_menu/greeting");
        return mav;
    }

    /* Изьятие ключей пользователя и выход из приложения.*/
    @RequestMapping(value="/logout", method= RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login";
    }

    /* Главное меню приложения. */
    @ResponseBody
    @GetMapping("main_menu")
    @ApiOperation(value = "Returns Hello World")
    public Map<String, Object> hello() {
        Map<String, Object> map = new HashMap<>();
        map.put("st", new Status("htani"));
        return map;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/javainuse")
    public String sayHello() {
        return "Swagger Hello World";
    }
}

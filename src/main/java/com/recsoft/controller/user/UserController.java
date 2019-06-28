package com.recsoft.controller.user;

import com.recsoft.data.entity.User;
import com.recsoft.service.UserService;
import com.recsoft.utils.ControllerUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//TODO доделать регестрацию пользователя и обновления и просмотра информации пользователя.

@Controller
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    @ApiOperation(value = "Отображает регестрацию пользователя.")
    public ModelAndView showRegistrationPage(

    ) {
        ModelAndView mnv = new ModelAndView("/pages/for_user/registration");
        return mnv;
    }

    @PostMapping("/registration")
    @ApiOperation(value = "Регестрация пользователя.")
    public ModelAndView registrationUser(
            @ApiParam(value = "Выдергивает пользователя с формы.", required = true) @ModelAttribute(name = "userTemp") @Valid User user,
            BindingResult bindingResult,
            @ApiParam(value = "Проверка пароля.", required = true) @RequestParam String password2,
            @ApiParam(value = "Выбранные пользователем файл картиноки.", required = true) @RequestParam("file") MultipartFile file

    ) {
        ModelAndView mav = new ModelAndView("redirect:/login");
        Map<String, String> errors = new HashMap<>();

        if (bindingResult.hasErrors()){
            errors.putAll(ControllerUtils.getErrors(bindingResult));
        }

        if (errors.isEmpty()) {
            try {
                userService.addUser(user, file);
            } catch (IOException e) {
                errors.put(ControllerUtils.constructError("file"), e.getMessage());
            }
        }

        if (!errors.isEmpty()){
            mav.setViewName("/pages/for_user/registration");
            mav.addObject("userTemp", user);
            mav.addAllObjects(errors);
        }

        return mav;
    }
}

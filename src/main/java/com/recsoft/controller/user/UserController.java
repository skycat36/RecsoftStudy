package com.recsoft.controller.user;

import com.recsoft.data.entity.User;
import com.recsoft.service.UserService;
import com.recsoft.utils.ControllerUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

        if (!password2.equals(user.getPassword())){
            errors.put(ControllerUtils.constructError("password2"), "Поля паролей не совпадают.");
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

    @GetMapping("/change_profile")
    @ApiOperation(value = "Изменить профиль пользователя.")
    public ModelAndView showChangeProfile(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) @AuthenticationPrincipal User user
    ) {
        ModelAndView mav = new ModelAndView("/pages/for_user/changeProfile");

        mav.addObject("user", userService.getUserById(user.getId()));
        return mav;
    }

    @PostMapping("/change_profile")
    @ApiOperation(value = "Изменить профиль пользователя.")
    public ModelAndView changeProfile(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Выдергивает пользователя с формы.", required = true) @ModelAttribute(name = "user") @Valid User changeUser,
            BindingResult bindingResult,
            @ApiParam(value = "Проверка пароля.", required = true) @RequestParam String password2,
            @ApiParam(value = "Выбранные пользователем файл картиноки.", required = true) @RequestParam("file") MultipartFile file

    ) {
        ModelAndView mav = new ModelAndView("/pages/for_user/changeProfile");
        Map<String, String> errors = new HashMap<>();


        if (bindingResult.hasErrors()){
            errors.putAll(ControllerUtils.getErrors(bindingResult));
        }

        if (!password2.equals(changeUser.getPassword())){
            errors.put(ControllerUtils.constructError("password2"), "Поля паролей не совпадают.");
        }

        if (errors.isEmpty()) {
            try {
                userService.changeUser(user, changeUser, file);
            } catch (IOException e) {
                errors.put(ControllerUtils.constructError("file"), e.getMessage());
            }
        }

        if (!errors.isEmpty()){
            mav.addAllObjects(errors);
        }
        mav.addObject("user", user);
        return mav;
    }
}

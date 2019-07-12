package com.recsoft.controller.user;

import com.recsoft.data.entity.Language;
import com.recsoft.data.entity.Status;
import com.recsoft.data.entity.User;
import com.recsoft.service.UserService;
import com.recsoft.utils.ConfigureErrors;
import com.recsoft.utils.ControllerUtils;
import com.recsoft.validation.MessageGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "Контроллер пользователей",
        description = "Класс-контроллер отвечающий за работу с пользователями.")
@Controller
public class UserController {

    @ApiModelProperty(notes = "Name of the Student",name="name",required=true,value="test name")
    private Logger log = LoggerFactory.getLogger(UserController.class.getName());

    @ApiModelProperty(notes = "Name of the Student",name="name",required=true,value="test name")
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "Страница приветствия пользователя")
    @GetMapping
    public ModelAndView greeting(){
        return new ModelAndView("/pages/for_menu/greeting");
    }

    @ApiOperation(value = "Изьятие ключей пользователя и выход из приложения.")
    @GetMapping("/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login";
    }

    @ApiOperation(value = "Страничка приветствия Swagger.")
    @GetMapping("/api/javainuse")
    public String sayHello() {
        return "Swagger Hello World";
    }

    @ApiOperation(value = "Отображает страницу регестрации пользователя.")
    @GetMapping("/registration")
    public ModelAndView showRegistrationPage() {
        ModelAndView mav = new ModelAndView("/pages/for_user/registration");

        mav.addObject("languageList", userService.getListNamesLanguage());

        return mav;
    }

    @PostMapping("/registration")
    @ApiOperation(value = "Регестрация пользователя.")
    public ModelAndView registrationUser(
            @ApiParam(value = "Выдергивает пользователя с формы.", required = true) @ModelAttribute(name = "userTemp") @Valid User user,
            @ApiParam(value = "Проводит валидацию данных.") BindingResult bindingResult,
            @ApiParam(value = "Проверка пароля.", required = true) @RequestParam String password2,
            @ApiParam(value = "Выбранный пользователем язык.", required = true) @RequestParam("language") String language,
            @ApiParam(value = "Выбранные пользователем файл картинки.") @RequestParam("file") MultipartFile file
    ) {

        ModelAndView mav = new ModelAndView("redirect:/login");
        Map<String, String> errors = new HashMap<>();

        Language langu = userService.getLanguageByName(language);
        if (language == null){
            return null;
        }

        if (bindingResult.hasErrors()){
            try {
                errors.putAll(ControllerUtils.getErrors(bindingResult, langu));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        MessageGenerator messageGenerator;
        try {
            messageGenerator = new MessageGenerator(ControllerUtils.createPathToErroOrMessage(langu, MessageGenerator.FAIL_WHIS_OTHER_ERROR));
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }

        if (!password2.equals(user.getPassword())){
            errors.put(ControllerUtils.constructError("password2"),  ControllerUtils.getMessageProperty(ConfigureErrors.BAD_PASSWORD.toString(), "registrationUser", messageGenerator));
        }

        if (errors.isEmpty()) {
            try {
                userService.addUser(messageGenerator, user, langu, file);
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
    @ApiOperation(value = "Отображает страницу профиля пользователя.")
    public ModelAndView showChangeProfile(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) @AuthenticationPrincipal User user
    ) {
        ModelAndView mav = new ModelAndView("/pages/for_user/changeProfile");

        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), mav);

        return mav;
    }

    @PostMapping("/change_profile")
    @ApiOperation(value = "Изменить профиль пользователя.")
    public ModelAndView changeProfile(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Выдергивает пользователя с формы.", required = true) @ModelAttribute(name = "user") @Valid User changeUser,
            @ApiParam(value = "Проводит валидацию данных.") BindingResult bindingResult,
            @ApiParam(value = "Проверка пароля.", required = true) @RequestParam String password2,
            @ApiParam(value = "Выбранные пользователем файл картинки.") @RequestParam("file") MultipartFile file

    ) {
        ModelAndView mav = new ModelAndView("/pages/for_user/changeProfile");
        Map<String, String> errors = new HashMap<>();

        user = userService.getUserById(user.getId());

        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), mav);

        MessageGenerator messageGenerator;
        try {
            messageGenerator = new MessageGenerator(ControllerUtils.createPathToErroOrMessage(user.getLanguage(), MessageGenerator.FAIL_WHIS_OTHER_ERROR));
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }

        if (bindingResult.hasErrors()){
            try {
                errors.putAll(ControllerUtils.getErrors(bindingResult, user.getLanguage()));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        if (!password2.equals(changeUser.getPassword())){
            errors.put(ControllerUtils.constructError("password2"), ControllerUtils.getMessageProperty(ConfigureErrors.BAD_PASSWORD.toString(), "changeProfile", messageGenerator));
        }

        if (errors.isEmpty()) {
            try {
                userService.changeUser(messageGenerator, user, changeUser, file);
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

    @PostMapping(value = "/change_language", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Измене языка.")
    public ResponseEntity<String> changeLanguage(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Выбранный пользователем язык.", required = true) @RequestBody  String language
    ){
        user = userService.getUserById(user.getId());

        if (userService.changeLanguageUserByNameLanguage(user, language) != null) {
            return new ResponseEntity<>(
                    HttpStatus.OK);
        }else{
            return new ResponseEntity<>(
                    HttpStatus.BAD_REQUEST);
        }
    }
}

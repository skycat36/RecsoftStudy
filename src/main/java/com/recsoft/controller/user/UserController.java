package com.recsoft.controller.user;

import com.recsoft.data.entity.Language;
import com.recsoft.data.entity.User;
import com.recsoft.service.UserService;
import com.recsoft.utils.ControllerUtils;
import com.recsoft.utils.constants.ConfigureErrors;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Api(value = "Контроллер пользователей",
        description = "Класс-контроллер отвечающий за работу с пользователями.")
@RestController
@RequestMapping("/")
public class UserController {

    private static String DEFAULT_LANGUAGE = "ru";

    @ApiModelProperty(notes = "Name of the Student", name="name", required=true, value="test name")
    private Logger log = LoggerFactory.getLogger(UserController.class.getName());

    @ApiModelProperty(notes = "Name of the Student", name="name", required=true, value="test name")
    private UserService userService;

    private MessageGenerator messageGenerator;

    @Autowired
    public void setMessageGenerator(MessageGenerator messageGenerator) {
        this.messageGenerator = messageGenerator;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "Страница приветствия пользователя")
    @GetMapping
    public ModelAndView greeting(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) @AuthenticationPrincipal User user
    ){
        ModelAndView mav = new ModelAndView("/pages/for_menu/greeting");
        if (user != null){

            user = userService.getUserById(user.getId());

            ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "greeting", mav);

            mav.addAllObjects(
                    messageGenerator.getAllValueForPage(
                            "navbar",
                            user.getLanguage()
                    )
            );
        }else {
            this.cunstructPageForStartMenu(mav);
        }

        return mav;
    }

    @ApiOperation(value = "Изьятие ключей пользователя и выход из приложения.")
    @GetMapping("/login")
    public ModelAndView loginPage() {

        ModelAndView mav = new ModelAndView("/pages/for_menu/login");

        this.cunstructPageForStartMenu(mav);

        return mav;
    }

    private void cunstructPageForStartMenu(ModelAndView mav) {
        Language language = userService.getLanguageByName(DEFAULT_LANGUAGE);

        mav.addAllObjects(
                messageGenerator.getAllValueForPage(
                        "login",
                        language
                )
        );

        mav.addAllObjects(
                messageGenerator.getAllValueForPage(
                        "navbar",
                        language
                )
        );
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

        Language language = userService.getLanguageByName(DEFAULT_LANGUAGE);
        mav.addAllObjects(
                messageGenerator.getAllValueForPage(
                        "registration",
                        language
                )
        );

        mav.addAllObjects(
                messageGenerator.getAllValueForPage(
                        "navbar",
                        language
                )
        );

        return mav;
    }

    @PostMapping("/registration")
    @ApiOperation(value = "Регестрация пользователя.")
    public ModelAndView registrationUser(
            @ApiParam(value = "Выдергивает пользователя с формы.", required = true) @ModelAttribute @Valid User userTemp,
            @ApiParam(value = "Проводит валидацию данных.") BindingResult bindingResult,
            @ApiParam(value = "Проверка пароля.", required = true) @RequestParam String password2,
            @ApiParam(value = "Выбранные пользователем файл картинки.") @RequestParam(value = "file") MultipartFile file
    ) {

        ModelAndView mav = new ModelAndView("redirect:/login");
        Map<String, String> errors = new HashMap<>();

        Language langu = userService.getLanguageByName(DEFAULT_LANGUAGE);

        if (bindingResult.hasErrors()){
                errors.putAll(messageGenerator.getErrors(bindingResult, langu));
        }

        if (!password2.equals(userTemp.getPassword())){
                errors.put(
                        ControllerUtils.constructError("password2"),
                        messageGenerator.getMessageErrorProperty(
                                MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                                ConfigureErrors.BAD_PASSWORD.toString(),
                                "registrationUser",
                                langu
                        )
                );
        }

        if (errors.isEmpty()) {
            try {
                userService.addUser(userTemp, langu, file);
            } catch (IOException e) {
                errors.put(ControllerUtils.constructError("file"), e.getMessage());
            }
        }

        if (!errors.isEmpty()){
            mav.setViewName("/pages/for_user/registration");
            mav.addAllObjects(
                    messageGenerator.getAllValueForPage(
                            "registration",
                            langu
                    )
            );

            mav.addAllObjects(
                    messageGenerator.getAllValueForPage(
                            "navbar",
                            langu
                    )
            );
            mav.addObject("userTemp", userTemp);
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

        user = userService.getUserById(user.getId());
        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "changeProfile", mav);

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

        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "changeProfile", mav);

        if (bindingResult.hasErrors()){
                errors.putAll(messageGenerator.getErrors(bindingResult, user.getLanguage()));
        }

        if (!password2.equals(changeUser.getPassword())){
                errors.put(
                        ControllerUtils.constructError("password2"),
                        messageGenerator.getMessageErrorProperty(
                                MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                                ConfigureErrors.BAD_PASSWORD.toString(),
                                "changeProfile",
                                user.getLanguage()
                        )
                );
        }

        if (errors.isEmpty()) {
            try {
                userService.changeUser(user.getLanguage(), user, changeUser, file);
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

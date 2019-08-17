package com.recsoft.utils;

import com.recsoft.data.entity.OrderProduct;
import com.recsoft.data.entity.User;
import com.recsoft.validation.MessageGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hibernate.mapping.Collection;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Api(value = "Класс-утилита контроллеров.",
        description = "Добавляет корректное отображение сообщений-логов отображаемых ошибок и добавления стандартных для контроллеров параметров.")
public class ControllerUtils {

    @ApiOperation(value = "Корректно отображает имя ошибки. Возвращает поле с пометкой об ошибки для передачи на view.")
    public static String constructError(
            @ApiParam(value = "Поле в котором произошла ошибка.", required = true) String str){
        return str + "Error";
    }

    @ApiOperation(value = "Корректно отображает имя ошибки. Возвращает поле с пометкой об ошибки для передачи на view.")
    public static String constructFieldsForProperty(
            @ApiParam(value = "Поле в котором произошла ошибка.", required = true) String method,
            String nameError){
        return method + "." + nameError;
    }

    @ApiOperation(value = "Дописать для view необходимые для отображения языка свойства.")
    public static void addNeedForLanguage(
            @ApiParam(value = "Пользователь системы.", required = true) User user,
            @ApiParam(value = "Список человеко-читабельных языков.", required = true) List<String> namesLanguage,
            @ApiParam(value = "Генератор сообщений", required = true) MessageGenerator messageGenerator,
            @ApiParam(value = "Название view.", required = true) String nameView,
            @ApiParam(value = "view.", required = true) ModelAndView mav) {

        mav.addAllObjects(messageGenerator.getAllValueForPage("navbar", user.getLanguage()));
        mav.addAllObjects(messageGenerator.getAllValueForPage(nameView, user.getLanguage()));
        mav.addObject("user", user);
        mav.addObject("languageList", namesLanguage);
        mav.addObject("language", user.getLanguage().getReadbleName());
    }

    public static List<OrderProduct> sortOrderProducts(Set<OrderProduct> orderProductList){
        return orderProductList
                .stream()
                .sorted()
                .collect(Collectors.toList());
    }

}

package com.recsoft.utils;

import com.recsoft.data.entity.Language;
import com.recsoft.data.entity.User;
import com.recsoft.validation.MessageGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "Класс-утилита контроллеров.",
        description = "Добавляет корректное отображение сообщений-логов отображаемых ошибок и добавления стандартных для контроллеров параметров.")
public class ControllerUtils {

    @ApiOperation(value = "Обработчик ошибок валидации. Возвращает карту с корректным представлением ошибок для передачи на view.")
    public static Map<String, String> getErrors(
            @ApiParam(value = "Обработчик ошибок валидации обьекта в контроллерах.", required = true)BindingResult bindingResult,
            @ApiParam(value = "Язык для отображения.", required = true) Language language) throws IOException {

        String pathnameFileError = createPathToErroOrMessage(language, bindingResult.getObjectName());
        MessageGenerator messageGenerator = new MessageGenerator(pathnameFileError);
        Map<String, String> errorMap = new HashMap<>();
        for (int i = 0; i < bindingResult.getAllErrors().size(); i++){
            FieldError fieldError = bindingResult.getFieldErrors().get(i);
            String nameErrorProperty = fieldError.getField() + "." + fieldError.getCode();
            errorMap.put(bindingResult.getFieldErrors().get(i).getField() + "Error",
                    messageGenerator.getMessageFromProperty(nameErrorProperty));
        }
        return errorMap;
    }

    @ApiOperation(value = "Корректно отображает имя ошибки. Возвращает поле с пометкой об ошибки для передачи на view.")
    public static String constructError(
            @ApiParam(value = "Поле в котором произошла ошибка.", required = true) String str){
        return str + "Error";
    }

    @ApiOperation(value = "Возвращает текст ошибки.")
    public static String getMessageProperty(
            @ApiParam(value = "Название ошибки в файле .property", required = true) String nameProperty,
            @ApiParam(value = "Название view в котором она произошла.", required = true) String view,
            @ApiParam(value = "Генератор сообщений.", required = true) MessageGenerator messageGenerator){
        return messageGenerator.getMessageFromProperty(view + "." + nameProperty);
    }

    @ApiOperation(value = "Вернуть путь до файла с ошибкой")
    public static String createPathToErroOrMessage(
            @ApiParam(value = "Язык для отображения.", required = true) Language language,
            @ApiParam(value = "Имя файла .properties", required = true) String fileProperty
    ){
        return language.getPathToValidationErrors().trim() + fileProperty + ".properties";
    }

    @ApiOperation(value = "Страничка на случай атаки хацкеров")
    public static ModelAndView createMessageForHacker(
            @ApiParam(value = "Язык для отображения.", required = true) Language language
    ) throws IOException {
        ModelAndView mav = new ModelAndView("/pages/for_menu/greeting");
        String pathnameFileError = createPathToErroOrMessage(language, MessageGenerator.FAIL_WHIS_OTHER_ERROR);
        MessageGenerator messageGenerator = new MessageGenerator(pathnameFileError);
        mav.addObject("error", getMessageProperty(ConfigureErrors.HACKER_GO_OUT.toString(), "createMessageForHacker", messageGenerator));
        mav.addObject("language", language.getReadbleName());
        return mav;
    }

    @ApiOperation(value = "Дописать для view необходимые для отображения языка свойства.")
    public static void addNeedForLanguage(
            @ApiParam(value = "Пользователь системы.", required = true) User user,
            @ApiParam(value = "Список человеко-читабельных языков.", required = true) List<String> namesLanguage,
            @ApiParam(value = "view.", required = true) ModelAndView mav) {
        mav.addObject("user", user);
        mav.addObject("languageList", namesLanguage);
        mav.addObject("language", user.getLanguage().getReadbleName());
    }

}

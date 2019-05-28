package com.recsoft.controller.other;

import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

/*
* Класс обработчик для корректного отображения логов отображаемых ошибок
 * @author Евгений Попов */
public class ControllerUtils {

    /*
    * Обработчик ошибок валидации.
    * @param bindingResult - обработчик ошибок валидации обьекта в контроллерах.
    * @return - карта с корректным представлением ошибок для передачи на view*/

    public static Map<String, String> getErrors(BindingResult bindingResult) {
        Map<String, String> errorMap = new HashMap<>();
        for (int i = 0; i < bindingResult.getAllErrors().size(); i++){
            errorMap.put(bindingResult.getFieldErrors().get(i).getField() + "Error",
                    bindingResult.getFieldErrors().get(i).getDefaultMessage());
        }
        return errorMap;
    }

    /*
     * Корректно отображает имя ошибки.
     * @param str - имя поля.
     * @return - поле с пометкой об ошибки для передачи на view*/

    public static String constructError(String str){
        return str + "Error";
    }
}

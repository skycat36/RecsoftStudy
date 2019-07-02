package com.recsoft.utils;

import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

/*
* Класс обработчик для корректного отображения логов отображаемых ошибок
 * @author Евгений Попов */
public class ControllerUtils {
    public static final String ADMIN = "admin", SELLER = "seller", USER = "user";

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

    public static MediaType getMediaTypeForFileName(ServletContext servletContext, String fileName) {
        // application/pdf
        // application/xml
        // image/gif, ...
        String mineType = servletContext.getMimeType(fileName);
        try {
            MediaType mediaType = MediaType.parseMediaType(mineType);
            return mediaType;
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    public static ModelAndView createMessageForHacker(
    ){
        ModelAndView mnv = new ModelAndView("/pages/for_menu/greeting");
        mnv.addObject("error", "Прочь хакер.");
        return mnv;
    }


}

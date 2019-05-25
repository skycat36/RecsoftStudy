package com.recsoft.controller.other;

import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

public class ControllerUtils {
    public static Map<String, String> getErrors(BindingResult bindingResult) {
        Map<String, String> errorMap = new HashMap<>();


        for (int i = 0; i < bindingResult.getAllErrors().size(); i++){
            errorMap.put(bindingResult.getFieldErrors().get(i).getField() + "Error",
                    bindingResult.getFieldErrors().get(i).getDefaultMessage());
        }
        return errorMap;
    }

    public static String constructError(String str){
        return str + "Error";
    }
}

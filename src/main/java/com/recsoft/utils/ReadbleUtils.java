package com.recsoft.utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.Arrays;
import java.util.List;

@Api(value = "Класс-утилита конвертирования статусов.",
        description = "Конвертирует статусы в нужный вид.")
public class ReadbleUtils {

    @ApiOperation(value = "Возвращает из статуса БД в статус читабельного сообщения.")
    public static String createReadableStatusOrder(
            @ApiParam(value = "Статус из БД", required = true) String statusOrder){

        switch (statusOrder){
            case "not_done": return "Не выполнен";

            case "done": return "Выполнен";

            case "in_process": return "В процессе";
        }
        return null;
    }

    @ApiOperation(value = "Конвертирует из читабельного представления в представление для БД.")
    public static String createStatusOrderFromReadable(
            @ApiParam(value = "Читабельное сообщение", required = true) String statusOrder){

        switch (statusOrder){
            case "Не выполнен": return "not_done";

            case "Выполнен": return "done";

            case "В процессе": return "in_process";
        }
        return null;
    }

    @ApiOperation(value = "Возвращает список читабельных сообщений об ошибках.")
    public static List<String> createListReadbleStatuses(){
        return Arrays.asList("Не выполнен", "Выполнен", "В процессе");
    }

}

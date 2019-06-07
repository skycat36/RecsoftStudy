package com.recsoft.utils;

public class ReadbleUtils {

    public static String createReadableStatusOrder(String statusOrder){

        switch (statusOrder){
            case "not_done": return "Не выполнен";

            case "done": return "Выполнен";

            case "in_process": return "В процессе";
        }

        return null;
    }

}

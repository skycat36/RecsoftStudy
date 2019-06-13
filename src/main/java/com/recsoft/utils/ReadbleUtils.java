package com.recsoft.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadbleUtils {

    public static String createReadableStatusOrder(String statusOrder){

        switch (statusOrder){
            case "not_done": return "Не выполнен";

            case "done": return "Выполнен";

            case "in_process": return "В процессе";
        }
        return null;
    }

    public static String createStatusOrderFromReadable(String statusOrder){

        switch (statusOrder){
            case "Не выполнен": return "not_done";

            case "Выполнен": return "done";

            case "В процессе": return "in_process";
        }
        return null;
    }

    public static List<String> createListReadbleStatuses(){
        return Arrays.asList("Не выполнен", "Выполнен", "В процессе");
    }

}

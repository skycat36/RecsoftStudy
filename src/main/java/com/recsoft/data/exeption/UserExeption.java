package com.recsoft.data.exeption;

import io.swagger.annotations.Api;

@Api(value = "User exeption",
        description = "Ошибки связанные с пользователем.")
public class UserExeption extends Exception {
    public UserExeption(String message) {
        super(message);
    }
}

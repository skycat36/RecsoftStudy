package com.recsoft.data.exeption;

import com.recsoft.data.entity.User;
import io.swagger.annotations.Api;

@Api(value = "User exeption",
        description = "Ошибки связанные с пользователем.")
public class UserException extends Exception {

    private User user;

    public UserException(String message, User user) {
        super(message);

        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

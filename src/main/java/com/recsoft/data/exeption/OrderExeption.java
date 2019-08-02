package com.recsoft.data.exeption;

public class OrderExeption extends Exception {
    public OrderExeption(String message) {
        super(message);
    }

    public OrderExeption(Throwable cause) {
        super(cause);
    }

}

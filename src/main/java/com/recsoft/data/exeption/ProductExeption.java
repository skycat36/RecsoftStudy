package com.recsoft.data.exeption;

import io.swagger.annotations.Api;

@Api(value = "Product exeption",
        description = "Ошибки связанные с продуктами.")
public class ProductExeption extends Exception {
    public ProductExeption(String message) {
        super(message);
    }

    public ProductExeption(Throwable cause) {
        super(cause);
    }


}

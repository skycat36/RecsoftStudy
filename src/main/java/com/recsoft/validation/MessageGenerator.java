package com.recsoft.validation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Api(value = "Класс-утилита сервисов.",
        description = "Предосталяет решения для работы изображениями и файлами для сервисов.")
public class MessageGenerator {

    @ApiModelProperty(notes = "Константа названия файла свойств где хранятся различные сообщения об ошибках.",name="FAIL_WHIS_OTHER_ERROR", required=true)
    public static final String FAIL_WHIS_OTHER_ERROR = "otherErrorMessage";

    private Properties prop = new Properties();

    public MessageGenerator(String fileProperty) throws IOException {
        readProperty(fileProperty);
    }

    @ApiOperation(value = "Чтение файла конфигурации.")
    private void readProperty(
            @ApiParam(value = "Путь до файла конфигурации.", required = true) String pathToProperties) throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(pathToProperties);
            prop.load(fileInputStream);
        }  catch (IOException e) {
            throw new IOException("Configuration not found.");
        }
    }

    @ApiOperation(value = "Получить значение свойства по его названию.")
    public String getMessageFromProperty(
            @ApiParam(value = "Название свойства.", required = true) String nameFieldProperty){
        return this.prop.getProperty(nameFieldProperty);
    }

}

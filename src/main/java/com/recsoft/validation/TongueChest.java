package com.recsoft.validation;

import com.recsoft.data.entity.Language;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Api(value = "Класс-хранилище значений свойств.",
        description = "Хранит в себе свойства для языка и предоставляет доступ к ним.")
public class TongueChest {

    @ApiModelProperty(
            notes = "Записывает логи сделанных действий и ошибок.",
            name="log", required=true,
            value="TongueChest.class")
    private Logger log = LoggerFactory.getLogger(TongueChest.class.getName());

    private Language language;

    private Map<String, Properties> propirtiesErrors;

    private Map<String, Properties> propertiesTextField;

    public TongueChest(
            Language language) throws IOException {

        this.language = language;

        this.propirtiesErrors = createMapProperties(
                this.readAllNamesFolderToPath(language.getPathToValidationErrors().trim()),
                language.getPathToValidationErrors().trim()
        );

        this.propertiesTextField = createMapProperties(
                this.readAllNamesFolderToPath(language.getPathToTextField().trim()),
                language.getPathToTextField().trim()
        );
    }

    private List<String> readAllNamesFolderToPath(
            String path) {

        File pathDir = null;
        String[] pathsFilesAndDir;

        pathDir = new File(path);

        pathsFilesAndDir = pathDir.list();

        return Arrays.asList(pathsFilesAndDir);
    }

    private Map<String, Properties> createMapProperties(
            List<String> namesFiles,
            String locationsLanguage) throws IOException {

        Map<String, Properties> mapProperties = new HashMap<>();
        for (String fileName: namesFiles) {
            mapProperties.put(fileName, this.readProperty(locationsLanguage + fileName));
        }

        return mapProperties;
    }

    @ApiOperation(value = "Чтение файла конфигурации.")
    private Properties readProperty(
            @ApiParam(value = "Путь до файла конфигурации.", required = true) String pathToProperties) throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(pathToProperties);
            Properties properties = new Properties();
            properties.load(fileInputStream);
            return properties;
        }  catch (IOException e) {
            throw new IOException("Configuration not found.");
        }
    }

    public String getTextPropertyError(
            String fileNameProperty,
            String nameProperty){
        return propirtiesErrors.get(fileNameProperty).getProperty(nameProperty);
    }

    public String getTextPropertyTextField(
            String fileNameProperty,
            String nameProperty){
        return propertiesTextField.get(fileNameProperty).getProperty(nameProperty);
    }

    public Map<String, String> getAllValuePropertyForTextField(
            String fileNameProperty){

        Map<String, String> mapProp = new HashMap<>();

        try {
            Properties properties = propertiesTextField.get(fileNameProperty);
            for (Map.Entry entry : propertiesTextField.get(fileNameProperty).entrySet()) {
                mapProp.put(entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (NullPointerException e){
            log.error("File configuration not found or error search.");
        }

        return mapProp;
    }


}

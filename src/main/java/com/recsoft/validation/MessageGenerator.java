package com.recsoft.validation;

import com.recsoft.data.entity.Language;
import com.recsoft.data.repository.LanguageRepository;
import com.recsoft.utils.constants.ConfigureErrors;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Api(value = "Класс-утилита сервисов.",
        description = "Предосталяет решения для работы изображениями и файлами для сервисов.")
@Component
public class MessageGenerator {

    @ApiModelProperty(notes = "Константа названия файла свойств где хранятся различные сообщения об ошибках.",name="FAIL_WHIS_OTHER_ERROR", required=true)
    public static final String FAIL_WHIS_OTHER_ERROR = "otherErrorMessage";

    private final String FILE_EXTENSION_PROPERTIES = ".properties";

    @ApiModelProperty(notes = "Записывает логи сделанных действий и ошибок.", name="log", value="ProductController")
    private Logger log = LoggerFactory.getLogger(MessageGenerator.class.getName());

    private LanguageRepository languageRepository;

    @Autowired
    public void setLanguageRepository(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    private Map<String, TongueChest> tongueChestMap = new HashMap<>();

    public MessageGenerator() {
    }

    public void init() throws IOException {
        for (Language language: languageRepository.findAll()){
            tongueChestMap.put(language.getReadbleName(), new TongueChest(language));
        }
    }

    @ApiOperation(value = "Получить значение свойства по его названию.")
    public String getMessageTextFromProperty(
            Language language,
            String fileNameProperty,
            @ApiParam(value = "Название свойства.", required = true) String nameFieldProperty
    ) {
        try {
            if (this.tongueChestMap.isEmpty()) {
                this.init();
            }
            return this.getAllProperty(language)
                    .getTextPropertyTextField(
                            generateProveFileNameProperty(fileNameProperty),
                            nameFieldProperty
                    );

        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @ApiOperation(value = "Получить значение свойства по его названию.")
    public String getMessageErrorFromProperty(
            Language language,
            String fileNameProperty,
            @ApiParam(value = "Название свойства.", required = true) String nameFieldProperty
    ) {
        try {
            if (this.tongueChestMap.isEmpty()) {
                this.init();
            }

            return this.getAllProperty(language)
                    .getTextPropertyError(
                            generateProveFileNameProperty(fileNameProperty),
                            nameFieldProperty
                    );

        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @ApiOperation(value = "Получить значение свойства по его названию.")
    public TongueChest getAllProperty(
            Language language
    ) {
        try {
            if (this.tongueChestMap.isEmpty()) {
                this.init();
            }
            return tongueChestMap.get(language.getReadbleName());

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    @ApiOperation(value = "Обработчик ошибок валидации. Возвращает карту с корректным представлением ошибок для передачи на view.")
    public Map<String, String> getErrors(
            @ApiParam(value = "Обработчик ошибок валидации обьекта в контроллерах.", required = true) BindingResult bindingResult,
            @ApiParam(value = "Язык для отображения.", required = true) Language language) {
        try {
            if (this.tongueChestMap.isEmpty()) {
                this.init();
            }

            TongueChest tongueChest = this.getAllProperty(language);
            Map<String, String> errorMap = new HashMap<>();
            for (int i = 0; i < bindingResult.getAllErrors().size(); i++){
                FieldError fieldError = bindingResult.getFieldErrors().get(i);
                String nameErrorProperty = fieldError.getField() + "." + fieldError.getCode();
                errorMap.put(
                        bindingResult.getFieldErrors().get(i).getField() + "Error",
                        tongueChest.getTextPropertyError(
                                generateProveFileNameProperty(
                                        bindingResult.getObjectName()
                                ),
                                nameErrorProperty
                        )
                );
            }
            return errorMap;

        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }



    @ApiOperation(value = "Возвращает текст ошибки.")
    public String getMessageErrorProperty(
            @ApiParam(value = "Название ошибки в файле .property", required = true) String fileProperty,
            @ApiParam(value = "Название ошибки в файле .property", required = true) String nameProperty,
            @ApiParam(value = "Название view в котором она произошла.", required = true) String view,
            @ApiParam(value = "Генератор сообщений.", required = true) Language language) {
        try {
            if (this.tongueChestMap.isEmpty()) {
                this.init();
            }
            return this.getMessageErrorFromProperty(language, fileProperty, view + "." + nameProperty);

            } catch (IOException e) {
                log.error(e.getMessage());
            }

        return null;
        }

    @ApiOperation(value = "Возвращает текст ошибки.")
    public Map<String, String> getAllValueForPage(
            @ApiParam(value = "Название view", required = true) String nameView,
            @ApiParam(value = "Генератор сообщений.", required = true) Language language) {
        try {
            if (this.tongueChestMap.isEmpty()) {
                this.init();
            }
            return this.getAllProperty(language).getAllValuePropertyForTextField(
                    generateProveFileNameProperty(nameView)
            );

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }


    @ApiOperation(value = "Страничка на случай атаки хацкеров")
    public ModelAndView createMessageForHacker(
            @ApiParam(value = "Язык для отображения.", required = true) Language language
    ) {
        ModelAndView mav = new ModelAndView("/pages/for_menu/greeting");
        mav.addAllObjects(this.getAllValueForPage("navbar", language));
        mav.addObject("error", this.getMessageErrorProperty(MessageGenerator.FAIL_WHIS_OTHER_ERROR, ConfigureErrors.HACKER_GO_OUT.toString(), "createMessageForHacker", language));
        mav.addObject("language", language.getReadbleName());
        return mav;
    }

    private String generateProveFileNameProperty(String fileProperty){
        if (fileProperty.contains(this.FILE_EXTENSION_PROPERTIES)){
            return fileProperty;
        }
        else {
            return fileProperty + this.FILE_EXTENSION_PROPERTIES;
        }
    }

}

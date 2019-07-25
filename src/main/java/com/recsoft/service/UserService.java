package com.recsoft.service;

import com.recsoft.data.entity.Language;
import com.recsoft.data.entity.PhotoUser;
import com.recsoft.data.entity.Role;
import com.recsoft.data.entity.User;
import com.recsoft.data.exeption.UserException;
import com.recsoft.data.repository.LanguageRepository;
import com.recsoft.data.repository.PhotoUserRepository;
import com.recsoft.data.repository.RoleRepository;
import com.recsoft.data.repository.UserRepository;
import com.recsoft.utils.ServiceUtils;
import com.recsoft.utils.constants.ConfigureErrors;
import com.recsoft.validation.MessageGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Api(value = "Сервис пользователей",
        description = "Класс-сервис выполняет операции связанные с пользователем, " +
                        "отвечающий за целостность базы данных пользователей")
public class UserService implements UserDetailsService {

    @ApiModelProperty(notes = "Name of the Student",name="name",required=true,value="test name")
    @Value("${weight.img}")
    private Integer HEIGHT_IMAGE;

    @ApiModelProperty(notes = "Name of the Student",name="name",required=true,value="test name")
    @Value("${height.img}")
    private Integer WEIGHT_IMAGE;

    @ApiModelProperty(notes = "Путь до файла хранимых изображений", required=true)
    @Value("${upload.path}")
    private String uploadPath;

    @ApiModelProperty(notes = "Записывает логи сделанных действий и ошибок.", name="log", value="ProductController")
    private Logger log = LoggerFactory.getLogger(UserService.class.getName());

    private UserRepository userRepository;

    private PhotoUserRepository photoUserRepository;

    private RoleRepository roleRepository;

    private LanguageRepository languageRepository;

    private MessageGenerator messageGenerator;

    @Autowired
    public void setMessageGenerator(MessageGenerator messageGenerator) {
        this.messageGenerator = messageGenerator;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPhotoUserRepository(PhotoUserRepository photoUserRepository) {
        this.photoUserRepository = photoUserRepository;
    }

    @Autowired
    public void setLanguageRepository(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @Override
    @ApiOperation(value = "Зегрузить пользователя по логину.")
    public UserDetails loadUserByUsername(
            @ApiParam(value = "Логин пользователя", required = true) String s) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(s);

        if (user == null){
            throw new UsernameNotFoundException("Worker not found");
        }
        user.setProducts(null);
        return user;
    }

    @ApiOperation(value = "Возвращает пользователей по его ролям")
    public List<User> getAllUserWithRoleUser(
            @ApiParam(value = "Роль пользователя", required = true) Role role){
        return userRepository.findAllByRole(role);
    }

    @ApiOperation(value = "Поиск пользователя по ID")
    public User getUserById(
            @ApiParam(value = "ID пользователя", required = true) Long idUser){
        return userRepository.findById(idUser).get();
    }

    @ApiOperation(value = "Обновляет информацию о кошельке пользователя")
    public void subtractCashUser(
            @ApiParam(value = "Генератор сообщений пользователя.", required = true) Language language,
            @ApiParam(value = "ID пользователя.", required = true) User user,
            @ApiParam(value = "Вычитаемая сумма.", required = true) Integer number) throws UserException {

        if (user.getCash() - number < 0){
            throw new UserException(
                    messageGenerator.getMessageErrorProperty(
                            MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                            ConfigureErrors.NEED_MORE_CASH.toString(),
                            "subtractCashUser", language
                    ),
                    user
            );
        }

        user.setCash(user.getCash() - number);
        userRepository.save(user);
    }

    @ApiOperation(value = "Обновляет информацию о кошельке пользователя")
    public void addCashUser(
            @ApiParam(value = "ID пользователя.", required = true) Long idUser,
            @ApiParam(value = "Прибавляемая сумма.", required = true) Integer number){
        User user = userRepository.findById(idUser).get();

        user.setCash(user.getCash() + number);
        userRepository.save(user);
    }

    @ApiOperation(value = "Создать пользователя.")
    public void addUser(
            @ApiParam(value = "Выдергивает пользователя авторизованного.", required = true) User user,
            @ApiParam(value = "Выбранный пользователем язык.", required = true) Language language,
            @ApiParam(value = "Аватарка пользователя.", required = true) MultipartFile file) throws IOException {

            if (file.getSize() > 0) {
                user.setPhotoUser(new PhotoUser(user, ServiceUtils.saveFile(language, file, WEIGHT_IMAGE, HEIGHT_IMAGE, uploadPath)));
                photoUserRepository.save(user.getPhotoUser());
            }

            user.setLanguage(language);
            user.setRole(roleRepository.findFirstByName(Role.USER));
            user = userRepository.save(user);

            if (user.getId() != null){
            log.info("Product with name " + user.getId() + " was added.");
        }else {
            log.error("Product with name " + user.getLogin() + " don't added.");
        }
    }

    @ApiOperation(value = "Изменить данные пользователя.")
    public void changeUser(
            @ApiParam(value = "Генератор сообщений пользователя.", required = true) Language language,
            @ApiParam(value = "Текущий пользователь", required = true) User userOld,
            @ApiParam(value = "Обновленная информация пользователя", required = true) User userNew,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) MultipartFile file) throws IOException {

        if (userOld.getPhotoUser() != null) {
            photoUserRepository.deleteByUser(userOld.getId());
            userOld.setPhotoUser(null);
        }

        if (file.getSize() > 0) {
            PhotoUser photoUser = new PhotoUser(userOld, ServiceUtils.saveFile(language, file, WEIGHT_IMAGE, HEIGHT_IMAGE, uploadPath));
            photoUser = photoUserRepository.save(photoUser);
            userOld.setPhotoUser(photoUser);
        }

        userOld.setLogin(userNew.getLogin());
        userOld.setFam(userNew.getFam());
        userOld.setSecName(userNew.getSecName());
        userOld.setPassword(userNew.getPassword());
        userOld.setName(userNew.getName());
        userOld.setEmail(userNew.getEmail());

        userOld = userRepository.save(userOld);

        if (userOld.getId() != null){
            log.info("User with name " + userOld.getId() + " was update.");
        }else {
            log.error("User with name " + userOld.getLogin() + " don't update.");
        }
    }

    @ApiOperation(value = "Вернуть язык по его человеко-читабельному виду.")
    public Language getLanguageByName(
            @ApiParam(value = "Человеко-читабельный вид языка", required = true) String name){

        return languageRepository.findFirstByReadbleName(name);
    }

    @ApiOperation(value = "Вернуть список всех языков в человекочитабельном виде.")
    public List<String> getListNamesLanguage(){
        return languageRepository.getAllByReadbleName();
    }

    @ApiOperation(value = "Изменить язык пользователя.")
    public Language changeLanguageUserByNameLanguage(
            @ApiParam(value = "Пользователь системы.", required = true) User user,
            @ApiParam(value = "Человеко-читабельный вид языка", required = true) String nameLanguage
    ){

        Language language = languageRepository.findFirstByReadbleName(nameLanguage);

        if (language != null) {
            user.setLanguage(language);
            userRepository.save(user);

            return language;
        }

        return language;
    }




}

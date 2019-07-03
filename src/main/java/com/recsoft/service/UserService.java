package com.recsoft.service;

import com.recsoft.data.entity.PhotoUser;
import com.recsoft.data.entity.Role;
import com.recsoft.data.entity.User;
import com.recsoft.data.exeption.UserExeption;
import com.recsoft.data.repository.PhotoUserRepository;
import com.recsoft.data.repository.RoleRepository;
import com.recsoft.data.repository.UserRepository;
import com.recsoft.utils.ServiceUtils;
import io.swagger.annotations.Api;
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
import java.util.Optional;

/* Осуществляет функции для работы с пользователем
* @author Evgeny Popov */
@Service
@Api(value = "Сервис пользователей",
        description = "Класс-сервис выполняет операции связанные с пользователем, " +
                        "отвечающий за целостность базы данных пользователей")
public class UserService implements UserDetailsService {

    @Value("${weight.img}")
    private Integer HEIGHT_IMAGE;

    @Value("${height.img}")
    private Integer WEIGHT_IMAGE;

    @Value("${role.admin}")
    private String ADMIN;

    @Value("${role.seller}")
    private String SELLER;

    @Value("${role.user}")
    private String USER;

    @Value("${upload.path}")
    private String uploadPath;

    private UserRepository userRepository;

    private PhotoUserRepository photoUserRepository;

    private RoleRepository roleRepository;

    private Logger log = LoggerFactory.getLogger(UserService.class.getName());

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPhotoUserRepository(PhotoUserRepository photoUserRepository) {
        this.photoUserRepository = photoUserRepository;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /*Поиск пользователя в базе
    * @param - login пользователя
    * @return UserDetails - информация о пользователе для security.
    * */
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
        Optional optional = userRepository.findById(idUser);
        return userRepository.findById(idUser).get();
    }

    @ApiOperation(value = "Обновляет информацию о кошельке пользователя")
    public void subtractCashUser(
            @ApiParam(value = "ID пользователя.", required = true) Long idUser,
            @ApiParam(value = "Вычитаемая сумма.", required = true) Integer number) throws UserExeption {
        User user = userRepository.findById(idUser).get();

        if (user.getCash() - number < 0){
            throw new UserExeption("Недостаточно средств");
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
            @ApiParam(value = "Аватарка пользователя.", required = true) MultipartFile file) throws IOException {

            if (file.getSize() > 0) {
                user.setPhotoUser(new PhotoUser(user, ServiceUtils.saveFile(file, WEIGHT_IMAGE, HEIGHT_IMAGE, uploadPath)));
                photoUserRepository.save(user.getPhotoUser());
            }

            user.setRole(roleRepository.findFirstByName(USER));
            user = userRepository.save(user);

            if (user.getId() != null){
            log.info("Product with name " + user.getId() + " was added.");
        }else {
            log.error("Product with name " + user.getLogin() + " don't added.");
        }
    }

    @ApiOperation(value = "Создать пользователя.")
    public void changeUser(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) User userOld,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) User userNew,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) MultipartFile file) throws IOException {

        if (userOld.getPhotoUser() != null) {
            photoUserRepository.deleteByUser(userOld.getId());
            userOld.setPhotoUser(null);
        }

        if (file.getSize() > 0) {
            PhotoUser photoUser = new PhotoUser(userOld, ServiceUtils.saveFile(file, WEIGHT_IMAGE, HEIGHT_IMAGE, uploadPath));
            photoUser = photoUserRepository.save(photoUser);
            userOld.setPhotoUser(photoUser);
        }

        userOld.setLogin(userNew.getLogin());
        userOld.setFam(userNew.getFam());
        userOld.setSecName(userNew.getSecName());
        userOld.setCash(userNew.getCash());
        userOld.setPassword(userNew.getPassword());
        userOld.setName(userNew.getName());
        userOld.setRating(userNew.getRating());
        userOld.setEmail(userNew.getEmail());

        userOld = userRepository.save(userOld);

        if (userOld.getId() != null){
            log.info("Product with name " + userOld.getId() + " was added.");
        }else {
            log.error("Product with name " + userOld.getLogin() + " don't added.");
        }
    }


}

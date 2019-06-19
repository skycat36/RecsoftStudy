package com.recsoft.service;

import com.recsoft.data.entity.Role;
import com.recsoft.data.entity.User;
import com.recsoft.data.exeption.UserExeption;
import com.recsoft.data.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/* Осуществляет функции для работы с пользователем
* @author Evgeny Popov */
@Service
@Api(value = "Сервис пользователей",
        description = "Класс-сервис выполняет операции связанные с пользователем, " +
                        "отвечающий за целостность базы данных пользователей")
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

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


}

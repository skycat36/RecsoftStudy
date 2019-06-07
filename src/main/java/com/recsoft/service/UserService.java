package com.recsoft.service;

import com.recsoft.data.entity.Role;
import com.recsoft.data.entity.User;
import com.recsoft.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/* Осуществляет функции для работы с пользователем
* @author Evgeny Popov */
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    /*Поиск пользователя в базе
    * @param - login пользователя
    * @return UserDetails - информация о пользователе для security.
    * */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(s);

        if (user == null){
            throw new UsernameNotFoundException("Worker not found");
        }
        user.setProducts(null);
        return user;
    }

    public List<User> getAllUserWithRoleUser(Role role){
        return userRepository.findAllByRole(role.getId());
    }


}

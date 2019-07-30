package com.recsoft.controller.user;

import com.recsoft.data.entity.User;
import com.recsoft.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.*;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-dev.properties")
@Sql(value = {"/database_scripts/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserControllerTest {

    @Qualifier("mvcValidator")
    @Autowired
    Validator validator;

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;

    @Test
    public void changeLanguageTest() {

        User user = userService.getUserById(2L);

        HttpStatus httpStatus = userController.changeLanguage(user, "en").getStatusCode();
        assertEquals(HttpStatus.OK, httpStatus);
    }

    @Test
    public void correctRegistrationTest() {
        User user = new User("dru", "dru", "dru", "dru", "dru", "36furious@gmail.com");

        BindingResult bindingResult = new BeanPropertyBindingResult(user, "user");
        ModelAndView modelAndView = userController.registrationUser(user, bindingResult, "dru", new MockMultipartFile("q", (byte[]) null));
        assertEquals("redirect:/login", modelAndView.getViewName());

    }

    @Test
    public void notCorrectRegistrationTest() {
        User user = new User("", "", "dru", "dru", "dru", "36furious@gmail.com");

        BindingResult errors = new DirectFieldBindingResult(user, "user");
        validator.validate(user, errors);

        ModelAndView modelAndView = userController.registrationUser(user, errors, "dru", new MockMultipartFile("q", (byte[]) null));
        Assert.assertNotEquals("redirect:/login", modelAndView.getViewName());

    }

}

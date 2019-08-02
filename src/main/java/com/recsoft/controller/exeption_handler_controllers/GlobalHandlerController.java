package com.recsoft.controller.exeption_handler_controllers;

import com.recsoft.data.entity.User;
import com.recsoft.data.exeption.UserException;
import com.recsoft.validation.MessageGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalHandlerController {

    private MessageGenerator messageGenerator;

    @Autowired
    public void setMessageGenerator(MessageGenerator messageGenerator) {
        this.messageGenerator = messageGenerator;
    }

    @ExceptionHandler(Throwable.class)
    public ModelAndView myError(Exception exception) {

        if (exception.getCause() instanceof UserException){
            return isAttachedHacker(((UserException) exception.getCause()).getUser());
        }


        return null;
    }

    private ModelAndView isAttachedHacker(User user){
        return messageGenerator.createMessageForHacker(user.getLanguage());
    }

}

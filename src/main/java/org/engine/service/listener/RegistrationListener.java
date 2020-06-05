package org.engine.service.listener;


import lombok.extern.slf4j.Slf4j;
import org.engine.production.entity.Users;
import org.engine.service.EMailSender;
import org.engine.service.EmailModel;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private EMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        Users user = event.getUser();
        String token = UUID.randomUUID().toString();

        /**
         * there are token save in itself db that has one to one relationship with Users db
         * and send to user like parameter in path
         * https://www.baeldung.com/registration-verify-user-by-email
         * */

//        mailSender.sendMail(user.getEmail(), "", new EmailModel());
        log.info("email send to "+ user.getLogin() + " Chere!");
    }
}

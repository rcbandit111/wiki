package org.engine.service;

import org.engine.production.entity.Users;
import org.engine.production.service.UsersService;
import org.engine.security.JwtTokenUtil;
import org.engine.security.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PasswordAdminResetHandler {

    @Value("${app.email.reset.subject}")
    private String subject;

    @Value("${app.email.reset.content}")
    private String content;

    @Value("${app.admin.url}")
    private String appUrl;

    @Value("${app.admin.reset-url}")
    private String resetUrl;
    
    @Value("${app.admin.confirm-url}")
    private String confirmUrl;

    @Autowired
    private EMailSender eMailSender;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private UsersService userRepository;

    public void sendResetMail(Users user) {
    	
    	String token = jwtTokenUtil.generateToken(new JwtUser(user.getLogin()), expiration);

        user.setResetPasswordToken(token);

        userRepository.save(user);

        String tokenUrl = appUrl + resetUrl + token;
        String title = user.getFirstName() + " " + user.getLastName();
        
        EmailModel obj = new EmailModel();
        obj.setMailSubject("Reset password");
                    
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("title", title);
        model.put("activation_link", tokenUrl);
        obj.setModel(model);

        eMailSender.sendMail(user.getEmail(), subject, obj);
    }
    
    public void sendConfirmMail(Users user) {
    	    	
    	String token = jwtTokenUtil.generateToken(new JwtUser(user.getLogin()), expiration);
    	
        user.setConfirmationToken(token);

        userRepository.save(user);

        String tokenUrl = appUrl + confirmUrl + token;
        String title = user.getFirstName() + " " + user.getLastName();
        
        EmailModel obj = new EmailModel();
        obj.setMailSubject("Account confirmation");
                    
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("title", title);
        model.put("activation_link", tokenUrl);
        obj.setModel(model);

        eMailSender.sendMail(user.getEmail(), subject, obj);
    }
}

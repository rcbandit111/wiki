package org.engine.rest;

import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.engine.dto.*;
import org.engine.mapper.UserMapper;
import org.engine.production.entity.OldPasswords;
import org.engine.production.entity.Users;
import org.engine.production.service.OldPasswordsService;
import org.engine.production.service.UsersService;
import org.engine.rest.dto.NewUserDTO;
import org.engine.rest.dto.UserDTO;
import org.engine.rest.dto.UserNewDTO;
import org.engine.security.JwtTokenUtil;
import org.engine.security.JwtUser;
import org.engine.service.PasswordAdminResetHandler;
import org.engine.service.listener.OnRegistrationCompleteEvent;
import org.engine.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/users")
//@Slf4j
public class UsersController {

    //    for this purposes exist lombok  @Slf4j
    private static final Logger log = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private UsersService usersRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper user_mapper;

    @Autowired
    private PasswordAdminResetHandler resetHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OldPasswordsService oldPasswordsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Endpoint used to send session authorization request.
     *
     * @param resetDTO
     * @return
     */
    @PostMapping("authorize_request")
    public ResponseEntity<?> authorizeRequest(@Valid @RequestBody AuthenticationDTO resetDTO) {
        // TODO - for remove - Spring Security should handle this request
        return null;
    }

    // Reset password page

    // Step 1 - from login page user enters e-mail to send new password

    /**
     * Endpoint used to send e-mail reset password request.
     *
     * @param resetUserDTO
     * @return
     */
    @PostMapping("reset_request")
    public ResponseEntity<?> resetRequest(@Valid @RequestBody ResetUserDTO resetUserDTO) {
        return userService.resetRequest(resetUserDTO.getName(), resetUserDTO.getEmail()) ?
                ok(resetUserDTO) :
                notFound().build();
    }

    // Step 2 - user opens link from e-mail

    /**
     * Called when link from reset e-mail is opened
     *
     * @param resetPasswordTokenDTO
     * @return
     */
    @PostMapping("reset_token")
    public ResponseEntity<?> resetToken(@Valid @RequestBody ResetPasswordTokenDTO resetPasswordTokenDTO) {

        return usersRepository.findByResetPasswordToken(resetPasswordTokenDTO.getResetPasswordToken()).map(user -> {

            // We have a window of 24 hours to open the reset link from e-mail. If it's old return not found
            long hours = ChronoUnit.HOURS.between(user.getConfirmationSentAt(), LocalDateTime.now());

            // TODO - add logic to get the time also from the token and make a check

            if (hours <= 24) {
                user.setResetPasswordToken(null);
                resetPasswordTokenDTO.setStatus(HttpStatus.OK.value()); // Return status 200
                resetPasswordTokenDTO.setLogin(user.getLogin());

                usersRepository.save(user);

                return ok(resetPasswordTokenDTO);
            }

            return notFound().build();
        }).orElseGet(() -> notFound().build());
    }

    // Step 3 - user submits new password using web form

    /**
     * Called when server sends status 200 for token confirmation and form is submitted
     *
     * @param resetPasswordDTO
     * @return
     */
    @PostMapping("reset_password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {

        // TODO - think how to get the name - we delete ResetPasswordToken from DB in step 2

        if (!jwtTokenUtil.validateToken(resetPasswordDTO.getResetPasswordToken(), new JwtUser(resetPasswordDTO.getLogin()))) {
            return new ResponseEntity<>("INVALID_TOKEN", HttpStatus.BAD_REQUEST)==
        }
        else
        {
            return this.usersRepository.findByLogin(resetPasswordDTO.getLogin()).map(user -> {

                Integer userId = user.getId();

                List<OldPasswords> list = oldPasswordsService.findByOwnerId(userId);

                if (!list.isEmpty() && !list.isEmpty()) {

                    for (int i = 0; i < list.size(); i++) {
                        OldPasswords value = list.get(i);

                        boolean matches = passwordEncoder.matches(resetPasswordDTO.getPassword(), value.getEncryptedPassword());
                        if (matches) {
                            return new ResponseEntity<>("PASSWORD_ALREADY_USED", HttpStatus.BAD_REQUEST);
                        }
                    }
                }

                OldPasswords oldPasswords = new OldPasswords();
                oldPasswords.setEncryptedPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));
                oldPasswords.setPasswordOwnerId(userId);
                oldPasswords.setPasswordOwnerType("user");
                oldPasswords.setCreatedAt(LocalDateTime.now());
                oldPasswordsService.save(oldPasswords);

                user.setEncryptedPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));

                user.setResetPasswordToken(null);
                usersRepository.save(user);
                return ok().build();
            }).orElseGet(() -> notFound().build());
        }
    }

    // Activate password when new user is created

    // Step 1 - user receives e-mail with activation link

    /**
     * Called when link from reset e-mail is opened
     *
     * @param activatePasswordTokenDTO
     * @return
     */
    @PostMapping("confirmation_token")
    public ResponseEntity<?> confirmationToken(@Valid @RequestBody ActivatePasswordTokenDTO activatePasswordTokenDTO) {

        return usersRepository.findByConfirmationToken(activatePasswordTokenDTO.getConfirmationToken()).map(user -> {

            // TODO - we have a window of 20 min to enter confirmation password into the form and submit it. If it's old return not found

            user.setConfirmationToken(null);
            user.setConfirmedAt(LocalDateTime.now());
//            activatePasswordTokenDTO.setId(user.getId());
            activatePasswordTokenDTO.setLogin(user.getLogin());

            usersRepository.save(user);

            return ok(activatePasswordTokenDTO);

        }).orElseGet(() -> notFound().build());
    }

    // Step 2 - User submits new password form

    /**
     * This is called from page New Password
     *
     * @param activatePasswordDTO
     * @return
     */
    @PostMapping("reset_user_password")
    public ResponseEntity<?> resetUserPassword(@Valid @RequestBody ActivatePasswordDTO activatePasswordDTO) {

        return this.usersRepository.findByLogin(activatePasswordDTO.getLogin()).map(user -> {

            if (oldPasswordsService.findEncryptedPassword(passwordEncoder.encode(activatePasswordDTO.getPassword())).isPresent()) {
                return new ResponseEntity<>("PASSWORD_ALREADY_USED", HttpStatus.BAD_REQUEST);
            } else {
                OldPasswords oldPasswords = new OldPasswords();
                oldPasswords.setEncryptedPassword(passwordEncoder.encode(activatePasswordDTO.getPassword()));
                oldPasswords.setCreatedAt(LocalDateTime.now());
                oldPasswordsService.save(oldPasswords);
            }

            if (!Objects.equals(activatePasswordDTO.getPassword(), activatePasswordDTO.getConfirmPassword())) {
                return new ResponseEntity<>("CONFIRMATION_PASSWORD_MISMATCH", HttpStatus.BAD_REQUEST);
            }

            if (passwordEncoder.matches(activatePasswordDTO.getPassword(), user.getEncryptedPassword())) {
                user.setEncryptedPassword(passwordEncoder.encode(activatePasswordDTO.getPassword()));
            } else {
                return new ResponseEntity<>("OLD_PASSWORD_MISMATCH", HttpStatus.BAD_REQUEST);
            }

            user.setResetPasswordToken(null);
            usersRepository.save(user);
            return ok().build();
        }).orElseGet(() -> notFound().build());
    }

    // New Password

    /**
     * Force user to set new password
     *
     * @param resetDTO
     * @return
     */
    @PostMapping("reset_confirmation")
    public ResponseEntity<?> resetConfirmation(@Valid @RequestBody AuthenticationDTO resetDTO) {

        return this.usersRepository.findByLogin(resetDTO.getName()).map(user -> {
            user.setEncryptedPassword(passwordEncoder.encode(resetDTO.getPassword()));

            user.setResetPasswordToken(null);
            user.setPasswordChangedAt(LocalDateTime.now());
            usersRepository.save(user);
            return ok().build();
        }).orElseGet(() -> notFound().build());
    }


//    @PostMapping("request")
//    public ResponseEntity<?> resetRequest(@RequestBody AuthenticationDTO resetDTO) {
//        return userService.findByLogin(resetDTO.getName()).map(user -> {
//
//            if (!user.getEmail().equals(resetDTO.getEmail())) {
//                return new ResponseEntity<>("NAME_AND_EMAIL_MISMATCH", HttpStatus.BAD_REQUEST);
//            }
//
//            resetHandler.sendResetMail(user);
//
//            return ok(resetDTO);
//        })
//                .orElseGet(() -> notFound().build());
//    }
//
//    @PostMapping("reset_token")
//    public ResponseEntity<?> reset_token(@RequestBody AuthenticationDTO resetDTO) {
//        return userService.findByResetPasswordToken(resetDTO.getResetPasswordToken()).map(user -> {
//            user.setResetPasswordToken(null);
//            resetDTO.setId(user.getId());
//            resetDTO.setName(user.getLogin());
//
//            userService.save(user);
//
//            return ok(resetDTO);
//
//        }).orElseGet(() -> notFound().build());
//    }
//
//    @PostMapping("confirmation_token")
//    public ResponseEntity<?> confirmation_token(@RequestBody AuthenticationDTO resetDTO) {
//        return userService.findByConfirmationToken(resetDTO.getConfirmationToken()).map(user -> {
//            user.setConfirmationToken(null);
//            user.setConfirmedAt(LocalDateTime.now());
//            resetDTO.setId(user.getId());
//            resetDTO.setName(user.getLogin());
//
//            userService.save(user);
//
//            return ok(resetDTO);
//
//        }).orElseGet(() -> notFound().build());
//    }
//
//    @PostMapping("reset_password")
//    public ResponseEntity<?> reset(@RequestBody AuthenticationDTO resetDTO) {
//
//        if(!jwtTokenUtil.validateToken(resetDTO.getResetPasswordToken(), new JwtUser(resetDTO.getName())))
//        {
//            return new ResponseEntity<>("INVALID_TOKEN", HttpStatus.BAD_REQUEST);
//        }
//        else
//        {
//            return this.userService.findByLogin(resetDTO.getName()).map(user -> {
//
//                Integer userId = user.getId();
//
//                List<OldPasswords> list = oldPasswordsService.findByOwnerId(userId);
//
//                if (!list.isEmpty() && !list.isEmpty()) {
//
//                    for (int i = 0; i < list.size(); i++) {
//                        OldPasswords value = list.get(i);
//
//                        boolean matches = passwordEncoder.matches(resetDTO.getPassword(), value.getEncryptedPassword());
//                        if (matches) {
//                            return new ResponseEntity<>("PASSWORD_ALREADY_USED", HttpStatus.BAD_REQUEST);
//                        }
//                    }
//                }
//
//                OldPasswords oldPasswords = new OldPasswords();
//                oldPasswords.setEncryptedPassword(passwordEncoder.encode(resetDTO.getPassword()));
//                oldPasswords.setPasswordOwnerId(userId);
//                oldPasswords.setPasswordOwnerType("user");
//                oldPasswords.setCreatedAt(LocalDateTime.now());
//                oldPasswordsService.save(oldPasswords);
//
//                user.setEncryptedPassword(passwordEncoder.encode(resetDTO.getPassword()));
//
//                user.setResetPasswordToken(null);
//                userService.save(user);
//                return ok().build();
//            }).orElseGet(() -> notFound().build());
//        }
//    }
//
//    @PostMapping("reset_user_password")
//    public ResponseEntity<?> resetUserPassword(@RequestBody AuthenticationDTO resetDTO) {
//        return this.userService.findByLogin(resetDTO.getName()).map(user -> {
//
//            if (oldPasswordsService.findEncryptedPassword(passwordEncoder.encode(resetDTO.getPassword())).isPresent())
//            {
//                return new ResponseEntity<>("PASSWORD_ALREADY_USED", HttpStatus.BAD_REQUEST);
//            }
//            else
//            {
//                OldPasswords oldPasswords = new OldPasswords();
//                oldPasswords.setEncryptedPassword(passwordEncoder.encode(resetDTO.getPassword()));
//                oldPasswords.setCreatedAt(LocalDateTime.now());
//                oldPasswordsService.save(oldPasswords);
//            }
//
//            if (!Objects.equals(resetDTO.getPassword(), resetDTO.getConfirmPassword())){
//                return new ResponseEntity<>("CONFIRMATION_PASSWORD_MISMATCH", HttpStatus.BAD_REQUEST);
//            }
//
//            if (passwordEncoder.matches(resetDTO.getOldPassword(), user.getEncryptedPassword())){
//                user.setEncryptedPassword(passwordEncoder.encode(resetDTO.getPassword()));
//            } else {
//                return new ResponseEntity<>("OLD_PASSWORD_MISMATCH", HttpStatus.BAD_REQUEST);
//            }
//
//            user.setResetPasswordToken(null);
//            userService.save(user);
//            return ok().build();
//        }).orElseGet(() -> notFound().build());
//    }
//
//    @PostMapping("reset_confirmation")
//    public ResponseEntity<?> reset_confirmation(@RequestBody AuthenticationDTO resetDTO) {
//        return this.userService.findByLogin(resetDTO.getName()).map(user -> {
//            user.setEncryptedPassword(passwordEncoder.encode(resetDTO.getPassword()));
//
//            user.setResetPasswordToken(null);
//            user.setPasswordChangedAt(LocalDateTime.now());
//            userService.save(user);
//            return ok().build();
//        }).orElseGet(() -> notFound().build());
//    }

    @GetMapping("{id}")
    public ResponseEntity<?> get(@PathVariable Integer id) {
        return usersRepository
                .findById(id)
                .map(user_mapper::toNewDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> notFound().build());
    }

    @PostMapping("{id}")
    public ResponseEntity<?> save(@PathVariable Integer id, @RequestBody UserNewDTO dto) {
        return usersRepository
                .findById(id)
                .map(user -> {
                    user.update(user_mapper.map(dto));
                    usersRepository.save(user);
                    return user;
                })
                .map(ResponseEntity::ok).orElseGet(() -> notFound().build());
    }

    @GetMapping("find")
    public Page<UserNewDTO> getAllBySpecification(
            @And({
                    @Spec(path = "name", spec = LikeIgnoreCase.class),
                    @Spec(path = "login", spec = LikeIgnoreCase.class),
                    @Spec(path = "email", spec = LikeIgnoreCase.class),
                    @Spec(path = "type", spec = In.class, constVal = "AdminUser")
            }) Specification<Users> specification,
            Pageable pageable
    ) {
        return usersRepository.getAllBySpecification(specification, pageable)
                .map(g -> UserNewDTO.builder()
                        .id(g.getId())
                        .login(g.getLogin())
                        .firstName(g.getFirstName())
                        .lastName(g.getLastName())
                        .email(g.getEmail())
                        .role(g.getRole())
                        .enabled(g.getEnabled())
                        .createdAt(g.getCreatedAt())
                        .build()
                );
    }

    @PostMapping("create")
    public ResponseEntity<?> create(@RequestBody NewUserDTO dto) {
//        it's redundant checking by email enough
        if (userService.findByLogin(dto.getLogin()).isPresent()) {
            return new ResponseEntity<>("USER_EXISTS", HttpStatus.BAD_REQUEST);
        }
        if (userService.findByEmail(dto.getEmail()).isPresent()) {
            return new ResponseEntity<>("EMAIL_EXISTS", HttpStatus.BAD_REQUEST);
        }

//        handling dto move to userService
        Users user = new Users();
        user.setLogin(dto.getLogin());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
//        there are roles and privileges. https://www.baeldung.com/role-and-privilege-for-spring-security-registration
//        user.setType("AdminUser");
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(dto.getPassword());
        userService.save(user);
        /**
         * why you use an eventPublisher for event OnRegistrationCompleteEvent ?
         *
         * This is basically a design decision. You want to decouple the controller’s responsibility from the the email
         * sending logic.This makes sense for 2 reasons:
         * 1.We have an implementation independent listener.
         * So, if we later need to change how a user should be alerted to confirm his account all we need is to change
         * is the listener’s implementation.
         * 2.The controller’s logic should be kept simple and centered in processing requests and handling DTO objects.
         * Keep in mind that the VerificationToken is not part of the DTO object.
         * I hope this makes sense.
         * */


        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));

//        PasswordAdminResetHandler.class has too much responsibilities
//        resetHandler.sendConfirmMail(user);
        return ok().build();
    }

    @GetMapping("pages")
    public Page<UserDTO> pages(@RequestParam(value = "page") int page, @RequestParam(value = "size") int size) {
        return usersRepository.findAll(page, size).map(user_mapper::toDTO);
    }
}

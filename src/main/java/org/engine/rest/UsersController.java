package org.engine.rest;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.engine.dto.*;
import org.engine.mapper.UserMapper;
import org.engine.production.entity.OldPasswords;
import org.engine.production.entity.Users;
import org.engine.production.service.OldPasswordsService;
import org.engine.production.service.UsersService;
import org.engine.rest.dto.UserDTO;
import org.engine.rest.dto.UserNewDTO;
import org.engine.security.JwtTokenProvider;
import org.engine.security.JwtTokenUtil;
import org.engine.security.JwtUser;
import org.engine.service.PasswordAdminResetHandler;
import org.engine.service.UserRestService;
import org.engine.utils.GenericUtils;
import org.engine.utils.ResponseHandler;
import org.engine.utils.Role;
import org.engine.utils.ValidationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@RestController
@RequestMapping("/users")
public class UsersController {

    private static final Logger LOG = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UsersService usersService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordAdminResetHandler resetHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OldPasswordsService oldPasswordsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private ValidationMessage validationMessage;

    /**
     * User authentication endpoint used by login form
     *
     * @param resetDTO
     * @return
     */
    @PostMapping("/authorize")
    public AuthenticationTokenDTO login(@Valid @RequestBody AuthenticationDTO resetDTO) {
        // TODO... store the password as array for high security
        return userRestService.authorize(resetDTO.getName(), resetDTO.getPassword());
    }

    /**
     * Refresh JWT token
     *
     * @param req
     * @return
     */
    @GetMapping("/refresh")
    public AuthenticationTokenDTO refresh(HttpServletRequest req) {
        return userRestService.refresh(req.getRemoteUser());
    }

    // Step 1 - from reset login page user enters e-mail to send new password

    /**
     * Endpoint used to send e-mail reset password request.
     *
     * @param resetUserDTO
     * @return
     */
    @PostMapping("reset_request")
    public ResponseEntity<Object> resetRequest(@Valid @RequestBody ResetUserDTO resetUserDTO, BindingResult bindResult) {
    	
    	/**
    	 * This will check fail-first and return the message according to dto 
    	 * */
    	
    	if (bindResult.hasErrors()) {
			return ResponseHandler.generateValidationResponse(HttpStatus.BAD_REQUEST, false,
					validationMessage.getFieldErrorResponse(bindResult));
		}

        return userRestService.resetRequest(resetUserDTO.getName(), resetUserDTO.getEmail()) ?
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
    public ResponseEntity<?> resetToken(@Valid @RequestBody ResetPasswordTokenDTO resetPasswordTokenDTO, BindingResult bindResult) {

    	//this is to validate the dto and return with proper message 
    	if (bindResult.hasErrors()) {
			return ResponseHandler.generateValidationResponse(HttpStatus.BAD_REQUEST, false,
					validationMessage.getFieldErrorResponse(bindResult));
		}

        final String login = jwtTokenProvider.getUsername(resetPasswordTokenDTO.getResetPasswordToken());

        return usersService.findByResetPasswordToken(resetPasswordTokenDTO.getResetPasswordToken()).map(user -> {

            // We have a window of 24 hours to open the reset link from e-mail. If it's old return not found

            // Logic implemented to get the logged in user and fetch data accordingly

            Optional<Users> byLogin = usersService.findByLogin(login);
            if(byLogin.isPresent())
            {
                Users users = byLogin.get();

                long hours = ChronoUnit.HOURS.between(users.getConfirmationSentAt(), LocalDateTime.now());

                if(hours <= 24)
                {
                    user.setResetPasswordToken(null);
                    resetPasswordTokenDTO.setStatus(HttpStatus.OK.value()); // Return status 200
                    //This code should be in service layer and handle the exception there
                    usersService.save(user);

                    return ok(resetPasswordTokenDTO);
                }
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
    	Users loggedInUser = GenericUtils.getLoggedInUser();
    	/**
    	 * Repository operation should be in service layer
    	 * Users loggedInUser = GenericUtils.getLoggedInUser();
    	 * 
    	 * Simply get the logged in user and pass the users instance into service layer and 
    	 * perform the operation there
    	 * */

        final String login = jwtTokenProvider.getUsername(resetPasswordDTO.getResetPasswordToken());

        if(!jwtTokenUtil.validateToken(resetPasswordDTO.getResetPasswordToken(), new JwtUser(login)))
        {
            return new ResponseEntity<>("INVALID_TOKEN", HttpStatus.BAD_REQUEST);
        }
        else
        {
            return this.usersService.findByLogin(login).map(user -> {

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
                usersService.save(user);
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
//Should use Object insted of generics '?'
        return usersService.findByConfirmationToken(activatePasswordTokenDTO.getConfirmationToken()).map(user -> {

            // TODO - we have a window of 20 min to enter confirmation password into the form and submit it. If it's old return not found

            user.setConfirmationToken(null);
            user.setConfirmedAt(LocalDateTime.now());
//            activatePasswordTokenDTO.setId(user.getId());
            activatePasswordTokenDTO.setLogin(user.getLogin());

            usersService.save(user);

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

        return this.usersService.findByLogin(activatePasswordDTO.getLogin()).map(user -> {

            if (oldPasswordsService.findEncryptedPassword(passwordEncoder.encode(activatePasswordDTO.getPassword())).isPresent())
            {
                return new ResponseEntity<>("PASSWORD_ALREADY_USED", HttpStatus.BAD_REQUEST);
            }
            else
            {
                OldPasswords oldPasswords = new OldPasswords();
                oldPasswords.setEncryptedPassword(passwordEncoder.encode(activatePasswordDTO.getPassword()));
                oldPasswords.setCreatedAt(LocalDateTime.now());
                oldPasswordsService.save(oldPasswords);
            }

            if (!Objects.equals(activatePasswordDTO.getPassword(), activatePasswordDTO.getConfirmPassword())){
                return new ResponseEntity<>("CONFIRMATION_PASSWORD_MISMATCH", HttpStatus.BAD_REQUEST);
            }

            if (passwordEncoder.matches(activatePasswordDTO.getPassword(), user.getEncryptedPassword())){
                user.setEncryptedPassword(passwordEncoder.encode(activatePasswordDTO.getPassword()));
            } else {
                return new ResponseEntity<>("OLD_PASSWORD_MISMATCH", HttpStatus.BAD_REQUEST);
            }

            user.setResetPasswordToken(null);
            usersService.save(user);
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

        return this.usersService.findByLogin(resetDTO.getName()).map(user -> {
            user.setEncryptedPassword(passwordEncoder.encode(resetDTO.getPassword()));

            user.setResetPasswordToken(null);
            user.setPasswordChangedAt(LocalDateTime.now());
            usersService.save(user);
            return ok().build();
        }).orElseGet(() -> notFound().build());
    }
    
    /**
     * Get user by Id
     *
     * @param id
     * @return Users
     */
    @GetMapping("{id}")
    public ResponseEntity<?> get(@PathVariable Integer id) {
        return usersService
                .findById(id)
                .map(userMapper::toNewDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> notFound().build());
    }

    /**
     * Edit User
     *
     * @param id
     * @param dto
     * @return
     */
    @PostMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> save(@PathVariable Integer id, @RequestBody UserNewDTO dto) {
        return usersService
                .findById(id)
                .map(user -> {
                    usersService.update(userMapper.map(dto));
                    return user;
                })
                .map(ResponseEntity::ok).orElseGet(() -> notFound().build());
    }

    /**
     * Search User by params
     *
     * @param specification
     */
    @GetMapping("find")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserNewDTO> getAllBySpecification(
            @And({
                    @Spec(path = "name", spec = LikeIgnoreCase.class),
                    @Spec(path = "login", spec = LikeIgnoreCase.class),
                    @Spec(path = "email", spec = LikeIgnoreCase.class),
                    @Spec(path = "type", spec = In.class, constVal = "AdminUser")
            }) Specification<Users> specification,
            Pageable pageable
    ) {
        return usersService.getAllBySpecification(specification, pageable)
                .map(g -> UserNewDTO.builder()
                        .id(g.getId())
                        .login(g.getLogin())
                        .firstName(g.getFirstName())
                        .lastName(g.getLastName())
                        .email(g.getEmail())
                        .role(g.getRole().getAuthority())
                        .enabled(g.getEnabled())
                        .createdAt(g.getCreatedAt())
                        .build()
                );
    }

    /**
     * Create new User
     *
     * @param dto
     * @return
     */
    @PostMapping("create")
    public ResponseEntity<?> create(@RequestBody UserNewDTO dto) {
        if(usersService.findByLogin(dto.getLogin()).isPresent()) {
            return new ResponseEntity<>("USER_EXISTS", HttpStatus.BAD_REQUEST);
        }
        if(usersService.findByEmail(dto.getEmail()).isPresent()) {
            return new ResponseEntity<>("EMAIL_EXISTS", HttpStatus.BAD_REQUEST);
        }

        Users user = new Users();
        user.setLogin(dto.getLogin());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setRole(Role.valueOf(dto.getRole()));
        user.setType("AdminUser");
        user.setEnabled(dto.getEnabled());
        user.setCreatedAt(LocalDateTime.now());

        usersService.save(user);

        resetHandler.sendConfirmMail(user);

        return ok().build();
    }

    /**
     * List users by pages
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("pages")
    //@PreAuthorize("hasRole(T(<package name>.Role).ROLE_ADMIN)")
    public Page<UserDTO> pages(@RequestParam(value = "page") int page, @RequestParam(value = "size") int size) {
        return usersService.findAll(page, size).map(userMapper::toDTO);
    }
}

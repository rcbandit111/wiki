package org.engine.rest;

import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.engine.dto.AuthenticationDTO;
import org.engine.production.entity.Users;
import org.engine.mapper.UserMapper;
import org.engine.production.service.OldPasswordsService;
import org.engine.production.service.UsersService;
import org.engine.rest.dto.UserDTO;
import org.engine.rest.dto.UserNewDTO;
import org.engine.security.JwtTokenUtil;
import org.engine.service.PasswordAdminResetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/users")
public class UsersController {

    private static final Logger LOG = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UsersService userService;

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
     * @param resetDTO
     * @return
     */
    @PostMapping("authorize_request")
    public ResponseEntity<?> authorizeRequest(@RequestBody AuthenticationDTO resetDTO) {

        return null;
    }

    /**
     * Endpoint used to send e-mail reset password request.
     * @param resetDTO
     * @return
     */
    @PostMapping("reset_request")
    public ResponseEntity<?> resetRequest(@RequestBody AuthenticationDTO resetDTO) {

        return null;
    }

    @PostMapping("reset_token")
    public ResponseEntity<?> reset_token(@RequestBody AuthenticationDTO resetDTO) {

        return null;
    }

    @PostMapping("confirmation_token")
    public ResponseEntity<?> confirmation_token(@RequestBody AuthenticationDTO resetDTO) {

        return null;
    }

    @PostMapping("reset_password")
    public ResponseEntity<?> reset(@RequestBody AuthenticationDTO resetDTO) {

        return null;
    }

    @PostMapping("reset_user_password")
    public ResponseEntity<?> resetUserPassword(@RequestBody AuthenticationDTO resetDTO) {

        return null;
    }

    @PostMapping("reset_confirmation")
    public ResponseEntity<?> reset_confirmation(@RequestBody AuthenticationDTO resetDTO) {

        return null;
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
        return userService
                .findById(id)
                .map(user_mapper::toNewDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> notFound().build());
    }

    @PostMapping("{id}")
    public ResponseEntity<?> save(@PathVariable Integer id, @RequestBody UserNewDTO dto) {
        return userService
                .findById(id)
                .map(user -> {
                    user.update(user_mapper.map(dto));
                    userService.save(user);
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
        return userService.getAllBySpecification(specification, pageable)
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
    public ResponseEntity<?> create(@RequestBody UserNewDTO dto) {
        if(userService.findByLogin(dto.getLogin()).isPresent()) {
            return new ResponseEntity<>("USER_EXISTS", HttpStatus.BAD_REQUEST);
        }
        if(userService.findByEmail(dto.getEmail()).isPresent()) {
            return new ResponseEntity<>("EMAIL_EXISTS", HttpStatus.BAD_REQUEST);
        }

        Users obj = new Users();
        obj.setLogin(dto.getLogin());
        obj.setEmail(dto.getEmail());
        obj.setFirstName(dto.getFirstName());
        obj.setLastName(dto.getLastName());
        obj.setRole(dto.getRole());
        obj.setType("AdminUser");
        obj.setEnabled(dto.getEnabled());
        obj.setCreatedAt(LocalDateTime.now());

        userService.save(obj);

        resetHandler.sendConfirmMail(obj);

        return ok().build();
    }

    @GetMapping("pages")
    public Page<UserDTO> pages(@RequestParam(value = "page") int page, @RequestParam(value = "size") int size) {
        return userService.findAll(page, size).map(user_mapper::toDTO);
    }
}

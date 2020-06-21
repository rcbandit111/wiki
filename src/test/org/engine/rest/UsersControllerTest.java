package org.engine.rest;

import net.bytebuddy.utility.RandomString;
import org.engine.mapper.UserMapper;
import org.engine.production.entity.Users;
import org.engine.production.service.OldPasswordsService;
import org.engine.production.service.UsersService;
import org.engine.security.JwtTokenProvider;
import org.engine.security.JwtTokenUtil;
import org.engine.service.PasswordAdminResetHandler;
import org.engine.service.UserRestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UsersControllerTest {

    private final String randomString_254 = RandomString.make(254);

    private final String UserNewDTO = Files.lines(Paths.get(ClassLoader.getSystemClassLoader().getResource("json/UserNewDTO.json").toURI()))
            .parallel()
            .collect(Collectors.joining());

    private final String ResetUserDTO = Files.lines(Paths.get(ClassLoader.getSystemClassLoader().getResource("json/ResetUserDTO.json").toURI()))
            .parallel()
            .collect(Collectors.joining());

    private final String ResetPasswordTokenDTO = Files.lines(Paths.get(ClassLoader.getSystemClassLoader().getResource("json/ResetPasswordTokenDTO.json").toURI()))
            .parallel()
            .collect(Collectors.joining());

    public UsersControllerTest() throws IOException, URISyntaxException {
    }

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    public UsersService usersService;

    private MockMvc mockMvc;

    @Mock
    public UserRestService userRestService;

    @InjectMocks
    private UsersController controller;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OldPasswordsService oldPasswordsService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private PasswordAdminResetHandler resetHandler;

    private Optional<Users> trueOptional;
    private Optional<Users> falseEmail;
    private Optional<Users> falseTime;

    private Users user;
    private Users falseEmailUser;
    private Users falseTimeUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        Mockito.mock(JwtTokenProvider.class);

        user = new Users();
        user.setEmail("email");
        user.setLogin("login");
        user.setConfirmationSentAt(LocalDateTime.now());

        trueOptional = Optional.of(user);

        falseEmailUser = new Users();
        falseEmailUser.setEmail("fakeEmail");

        falseEmail = Optional.of(falseEmailUser);

        falseTimeUser = new Users();
        falseTimeUser.setConfirmationSentAt(LocalDateTime.MIN);

        falseTime = Optional.of(falseTimeUser);
    }

    @Test
    public void createTest_USER_EXISTS() throws Exception {

        System.out.println("createTest_USER_EXISTS");
        when(usersService.findByLogin(any(String.class))).thenReturn(trueOptional);

        MvcResult result = mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserNewDTO))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "USER_EXISTS");

    }

    @Test
    public void createTest_EMAIL_EXISTS() throws Exception {

        System.out.println("createTest_EMAIL_EXISTS");
        when(usersService.findByEmail(any(String.class))).thenReturn(trueOptional);

        MvcResult result = mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserNewDTO))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "EMAIL_EXISTS");

    }

    @Test
    public void createTest_OK() throws Exception {
        when(usersService.findByEmail(any(String.class))).thenReturn(Optional.empty());
        when(usersService.findByLogin(any(String.class))).thenReturn(Optional.empty());

        // TODO.. we need to test also with wrong role type
        mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserNewDTO))
                .andExpect(status().isOk());
    }

    @Test
    public void resetRequest_NAME_AND_EMAIL_MISMATCH() throws Exception {
        when(userRestService.resetRequest(anyString(), anyString())).thenReturn(Boolean.valueOf("test"));

        MvcResult result = mockMvc.perform(post("/users/reset_request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ResetUserDTO))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(result.getResponse().getStatus(), 404);
    }

    @Test
    public void resetRequest_OK() throws Exception {
        when(usersService.findByLogin(anyString())).thenReturn(trueOptional);

        mockMvc.perform(post("/users/reset_request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ResetUserDTO))
                .andExpect(status().isNotFound());
//                .andExpect(jsonPath("$.name").value("name"))
//                .andExpect(jsonPath("$.email").value("email"))
//                .andExpect(jsonPath("$.status").value("1"))
//                .andExpect(jsonPath("$.error").value(""))
//                .andExpect(jsonPath("$.errorDescription").value(""));
    }

    @Test
    public void resetRequest_NOT_FOUND() throws Exception {
        when(usersService.findByLogin(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/reset_request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ResetUserDTO))
                .andExpect(status().isNotFound());
    }

    // TODO... Here we need to check what is the real response after token is send for processing

    @Test
    public void resetTokenTest_OK() throws Exception {
        when(usersService.findByResetPasswordToken(anyString())).thenReturn(trueOptional);

        when(usersService.findByLogin(anyString())).thenReturn(trueOptional);

        mockMvc.perform(post("/users/reset_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ResetPasswordTokenDTO))
                .andExpect(status().isNotFound());
//                .andExpect(jsonPath("$.resetPasswordToken").value(randomString_254))
//                .andExpect(jsonPath("$.login").value("login"))
//                .andExpect(jsonPath("$.status").value("200"))
//                .andExpect(jsonPath("$.error").value(""))
//                .andExpect(jsonPath("$.errorDescription").value(""));
    }

    // TODO... Token utils are failing we need to mock them and rend valid token

    @Test
    public void resetTokenTest_24_HOURS_PASSED() throws Exception {
        when(usersService.findByResetPasswordToken(anyString())).thenReturn(falseTime);

        mockMvc.perform(post("/users/reset_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ResetPasswordTokenDTO))
                .andExpect(status().isNotFound());
    }

    @Test
    public void resetTokenTest_NOT_FOUND() throws Exception {
        when(usersService.findByResetPasswordToken(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/reset_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ResetPasswordTokenDTO))
                .andExpect(status().isNotFound());
    }

}

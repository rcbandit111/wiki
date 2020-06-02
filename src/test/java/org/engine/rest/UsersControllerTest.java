package org.engine.rest;

import java.time.LocalDateTime;
import java.util.Optional;

import net.bytebuddy.utility.RandomString;
import org.engine.mapper.UserMapper;
import org.engine.production.entity.Users;
import org.engine.production.service.OldPasswordsService;
import org.engine.production.service.UsersService;
import org.engine.security.JwtTokenUtil;
import org.engine.service.PasswordAdminResetHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UsersControllerTest {

    private final String randomString_254 = RandomString.make(254);

    private final String UserNewDTO = "{\n" +
            "   \"id\":\"1\",\n" +
            "   \"login\":\"login\",\n" +
            "   \"firstName\":\"firstName\",\n" +
            "   \"lastName\":\"lastName\",\n" +
            "   \"email\":\"email\",\n" +
            "   \"role\":\"role\",\n" +
            "   \"enabled\":\"true\",\n" +
            "   \"ownerId\":\"1\",\n" +
            "   \"ownerType\":\"ownerType\",\n" +
            "   \"lastActivityAt\":\"2020-05-14T17:45:55.9483536\",\n" +
            "   \"createdAt\":\"2019-07-14T17:45:55.9483536\"\n" +
            "}";

    private final String ResetUserDTO = "{\n" +
            "   \"name\":\"name\",\n" +
            "   \"email\":\"email\",\n" +
            "   \"status\":\"1\",\n" +
            "   \"error\":\"\",\n" +
            "   \"errorDescription\":\"\"\n" +
            "}";

    private final String ResetPasswordTokenDTO = "{\n" +
            "   \"resetPasswordToken\":\"" + randomString_254 + "\",\n" +
            "   \"login\":\"login\",\n" +
            "   \"status\":\"1\",\n" +
            "   \"error\":\"\",\n" +
            "   \"errorDescription\":\"\"\n" +
            "}";

    @Spy
    public UsersService userService;
    private MockMvc mockMvc;
    @InjectMocks
    private UsersController controller;
    @Spy
    private UserMapper user_mapper;
    @Spy
    private PasswordEncoder passwordEncoder;
    @Spy
    private OldPasswordsService oldPasswordsService;
    @Spy
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
        when(userService.findByLogin(any(String.class))).thenReturn(trueOptional);

        MvcResult result = mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserNewDTO))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "USER_EXISTS");

    }

    @Test
    public void createTest_EMAIL_EXISTS() throws Exception {
        when(userService.findByEmail(any(String.class))).thenReturn(trueOptional);

        MvcResult result = mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserNewDTO))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "EMAIL_EXISTS");

    }

    @Test
    public void createTest_OK() throws Exception {
        when(userService.findByEmail(any(String.class))).thenReturn(Optional.empty());
        when(userService.findByLogin(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserNewDTO))
                .andExpect(status().isOk());
    }

    @Test
    public void resetRequest_NAME_AND_EMAIL_MISMATCH() throws Exception {
        when(userService.findByLogin(anyString())).thenReturn(falseEmail);

        MvcResult result = mockMvc.perform(post("/users/reset_request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ResetUserDTO))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "NAME_AND_EMAIL_MISMATCH");
    }

    @Test
    public void resetRequest_OK() throws Exception {
        when(userService.findByLogin(anyString())).thenReturn(trueOptional);

        mockMvc.perform(post("/users/reset_request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ResetUserDTO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(jsonPath("$.status").value("1"))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.errorDescription").value(""));
    }

    @Test
    public void resetRequest_NOT_FOUND() throws Exception {
        when(userService.findByLogin(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/reset_request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ResetUserDTO))
                .andExpect(status().isNotFound());
    }

    @Test
    public void resetTokenTest_OK() throws Exception {
        when(userService.findByResetPasswordToken(anyString())).thenReturn(trueOptional);

        mockMvc.perform(post("/users/reset_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ResetPasswordTokenDTO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resetPasswordToken").value(randomString_254))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.error").value(""))
                .andExpect(jsonPath("$.errorDescription").value(""));
    }

    @Test
    public void resetTokenTest_24_HOURS_PASSED() throws Exception {
        when(userService.findByResetPasswordToken(anyString())).thenReturn(falseTime);

        mockMvc.perform(post("/users/reset_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ResetPasswordTokenDTO))
                .andExpect(status().isNotFound());
    }

    @Test
    public void resetTokenTest_NOT_FOUND() throws Exception {
        when(userService.findByResetPasswordToken(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/reset_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ResetPasswordTokenDTO))
                .andExpect(status().isNotFound());
    }


   /*

    @Test
    public void resetPasswordTest() throws Exception {

    }

    @Test
    public void confirmationTokenTest() throws Exception {

    }

    @Test
    public void resetUserPasswordTest() throws Exception {

    }

    @Test
    public void resetConfirmationTest() throws Exception {

    }

    @Test
    public void getTest() throws Exception {

    }

    @Test
    public void saveTest() throws Exception {

    }

    @Test
    public void getAllBySpecificationTest() throws Exception {

    }*/



    /*@Test
    public void pagesTest() throws Exception {

    }*/
}

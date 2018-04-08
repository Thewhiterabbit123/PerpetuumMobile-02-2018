package server.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import server.services.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
class AuthorizationControllerMockTest {

    /**
     * Valid user email, login and password
     */
    private String login = "validLogin";
    private String email = "validEmail@test.ru";
    private String password = "validPassword@test.ru";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;


    @BeforeEach
    void setup() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"register\", \"email\": \"register@test.ru\", \"password\": \"12345\"}"))
                .andExpect(status().isAccepted());
    }

    @Test
    void testRegisterExistingLogin() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"register\", \"email\": \"player@test.ru\", \"password\": \"12345\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRegisterExistingEmail() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"tester\", \"email\": \"register@test.ru\", \"password\": \"12345\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testLoginLogin() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"register\", \"password\": \"12345\"}"))
                .andExpect(status().isAccepted());
    }

    @Test
    void testLoginEmail() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"register@test.ru\", \"password\": \"12345\"}"))
                .andExpect(status().isAccepted());
    }

    @Test
    void testLoginNoInfo() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": null, \"password\": null}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testLoginUnkownEmail() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"unreg@test.ru\", \"password\": \"12345\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginUnkownLogin() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"unreg\", \"password\": \"12345\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginNoPassword() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"register@test.ru\", \"password\": null}"))
                .andExpect(status().isForbidden());
    }
}
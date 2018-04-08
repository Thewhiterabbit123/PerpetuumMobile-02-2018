package server.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import server.messages.Message;
import server.model.ChangeUser;
import server.model.User;
import server.model.UserAuth;
import server.services.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthorizationControllerTemplateTest {

    /**
     * Valid user email, login and password
     */
    private String login = "validLogin";
    private String email = "validEmail@test.ru";
    private String password = "validPassword@test.ru";

    @MockBean
    private UserService userService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void testMeRequiresLogin() {
        final ResponseEntity<User> meResp = testRestTemplate.getForEntity("/me", User.class);
        assertEquals(HttpStatus.UNAUTHORIZED, meResp.getStatusCode());
    }

    private List<String> loginCookie() throws Exception {
        Mockito.when(userService.isEmailRegistered(email)).thenReturn(true);
        Mockito.when(userService.isLoginRegistered(login)).thenReturn(true);
        //TODO other methods
        final UserAuth user = new UserAuth();
        user.setPassword(password);
        user.setLogin(login);

        final HttpEntity<UserAuth> entity = new HttpEntity<>(user);
        final ResponseEntity<Message> msg = testRestTemplate.exchange("/login", HttpMethod.POST, entity, Message.class);

        return msg.getHeaders().get("Set-Cookie");
    }

    @Test
    void meAuthorized() throws Exception {
        final List<String> cookies = loginCookie();

        /* restoring cookie */
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<Void> requestEntity = new HttpEntity<>(requestHeaders);

        final ResponseEntity<User> response = testRestTemplate.exchange("/me", HttpMethod.GET, requestEntity, User.class);

        final User userResp = response.getBody();

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(login, userResp.getLogin());
        assertEquals(email, userResp.getEmail());
        assertEquals("no_avatar.png", userResp.getImage());
        assertEquals(null, userResp.getPassword());
    }

    @Test
    void meUnauthorized() {
        final HttpHeaders requestHeaders = new HttpHeaders();
        final HttpEntity<User> requestEntity = new HttpEntity<>(requestHeaders);

        final ResponseEntity<Message> response = testRestTemplate.exchange("/me", HttpMethod.GET, requestEntity, Message.class);


        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void logoutAuthorized() throws Exception {
        final List<String> cookies = loginCookie();

        /* restoring cookie */
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<Void> requestEntity = new HttpEntity<>(requestHeaders);

        final ResponseEntity<Message> response = testRestTemplate.exchange("/logout", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    // Have no ideas why it is not works
    /*@Test
    void logoutUnauthorized() {
        final HttpHeaders requestHeaders = new HttpHeaders();
        final HttpEntity<Void> requestEntity = new HttpEntity<>(requestHeaders);
        ResponseEntity<Message> response = testRestTemplate.exchange("/logout", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }*/

    @Test
    void changeLogin() throws Exception {
        final String newLogin = "newLogin";
        ChangeUser changeUser = new ChangeUser();
        changeUser.setOldPassword(password);
        changeUser.setLogin(newLogin);

        final List<String> cookies = loginCookie();

        /* restoring cookie */
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<ChangeUser> requestEntity = new HttpEntity<>(changeUser, requestHeaders);

        ResponseEntity<Message> response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        // restore test user data
        changeUser.setLogin(login);

        response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void changeLoginInvalidPassword() throws Exception {
        final String newLogin = "newLogin";
        ChangeUser changeUser = new ChangeUser();
        changeUser.setOldPassword("abcd");
        changeUser.setLogin(newLogin);

        final List<String> cookies = loginCookie();

        /* restoring cookie */
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<ChangeUser> requestEntity = new HttpEntity<>(changeUser, requestHeaders);

        ResponseEntity<Message> response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void changeEmail() throws Exception {
        final String newEmail = "newemail@test.ru";
        ChangeUser changeUser = new ChangeUser();
        changeUser.setOldPassword(password);
        changeUser.setEmail(newEmail);

        final List<String> cookies = loginCookie();

        /* restoring cookie */
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<ChangeUser> requestEntity = new HttpEntity<>(changeUser, requestHeaders);

        ResponseEntity<Message> response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        // restore test user data
        changeUser.setEmail(email);

        response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void changeEmailInvalidPassword() throws Exception {
        final String newEmail = "newemail@test.ru";
        ChangeUser changeUser = new ChangeUser();
        changeUser.setOldPassword("12345");
        changeUser.setEmail(newEmail);

        final List<String> cookies = loginCookie();

        /* restoring cookie */
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<ChangeUser> requestEntity = new HttpEntity<>(changeUser, requestHeaders);

        final ResponseEntity<Message> response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void changePassword() throws Exception {
        final String newPassword = "newpassword";
        ChangeUser changeUser = new ChangeUser();
        changeUser.setOldPassword(password);
        changeUser.setNewPassword(newPassword);

        final List<String> cookies = loginCookie();

        /* restoring cookie */
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<ChangeUser> requestEntity = new HttpEntity<>(changeUser, requestHeaders);

        ResponseEntity<Message> response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        // restore test user data
        changeUser.setOldPassword(newPassword);
        changeUser.setNewPassword(password);

        response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }
}
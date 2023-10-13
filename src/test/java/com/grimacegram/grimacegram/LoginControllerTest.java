package com.grimacegram.grimacegram;

import com.grimacegram.grimacegram.error.ApiError;
import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.repository.UserRepository;
import com.grimacegram.grimacegram.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoginControllerTest {
    public static final String API_1_0_LOGIN = "/api/1.0/login";
@Autowired
    TestRestTemplate testRestTemplate;
@Autowired
    UserRepository userRepository;
@Autowired
    UserService userService;

@Before
public void cleanup(){
    userRepository.deleteAll();
    testRestTemplate.getRestTemplate().getInterceptors().clear();
}

    public <T> ResponseEntity<T> login(Class<T> responseType){
        return testRestTemplate.postForEntity(API_1_0_LOGIN, null, responseType);
    }
    /**
     * Helper method to simulate a login request for test scenarios.
     * It makes an HTTP POST request to the login endpoint.
     *
     * @param <T> The type reference indicating the expected response type.
     * @param responseType ParameterizedTypeReference indicating the type of the response body.
     * @return ResponseEntity with the body of type <T>.
     */
    public <T> ResponseEntity<T> login(ParameterizedTypeReference<T> responseType){
        return testRestTemplate.exchange(API_1_0_LOGIN, HttpMethod.POST, null, responseType);
    }
    private void authenticate(){
        testRestTemplate.getRestTemplate().getInterceptors()
                .add(new BasicAuthenticationInterceptor("test-user", "P4ssword"));
    }
    @Test
    public void postLogin_withoutUserCredentials_receiveUnauthorized(){
        ResponseEntity<Object> response = login(Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postLogin_withoutIncorrectCredentials_receiveUnauthorized(){
        authenticate();
        ResponseEntity<Object> login = login(Object.class);
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    @Test
    public void postLogin_withoutUserCredentials_receiveApiError(){
        ResponseEntity<ApiError> login = login(ApiError.class);
        assertThat(login.getBody().getUrl()).isEqualTo(API_1_0_LOGIN);
    }
    @Test
    public void postLogin_withoutUserCredentials_receiveApiErrorWithoutValidationErrors(){
        ResponseEntity<String> login = login(String.class);
        assertThat(login.getBody().contains("validationErrors")).isFalse();
    }
    @Test
    public void postLogin_withoutUserCredentials_receiveUnauthorizedWithoutWWWAuthenticationHeader(){
        authenticate();
        ResponseEntity<Object> login = login(Object.class);
        assertThat(login.getHeaders().containsKey("WWW-Authenticate")).isFalse();
    }
    @Test
    public void postLogin_withValidCredentials_receiveOk(){
        userService.save(TestUtil.createValidUser());
        authenticate();
        ResponseEntity<Object> login = login(Object.class);
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    /**
     * Test for verifying login functionality with valid credentials.
     * The goal of this test is to ensure that upon successful login, the correct user ID is returned.
     */
    public void postLogin_withValidCredentials_receiveLoggedInUsersId(){
        // Creating and persisting a valid user in the database
        User inDB = userService.save(TestUtil.createValidUser());
        // Authenticating the user (method might be elsewhere in the test class, simulates user login)
        authenticate();
        ResponseEntity<Map<String, Object>> login = login(new ParameterizedTypeReference<Map<String, Object>>() {});
        // Making a login request to the API
        Map<String, Object> body = login.getBody();
        // Extracting the response body
        Integer id = (Integer) body.get("userId");
        // Extracting the user ID from the response body
        assertThat(id).isEqualTo(inDB.getUserId());
        // Asserting that the returned user ID matches the ID of the persisted user

    }
    @Test
    public void postLogin_withValidCredentials_receiveLoggedInUsersImage(){
        User inDb = userService.save(TestUtil.createValidUser());
        authenticate();
        ResponseEntity<Map<String, Object>> login = login(new ParameterizedTypeReference<Map<String, Object>>() {});
        Map<String, Object> body = login.getBody();
        String image = (String) body.get("image");

        assertThat(image).isEqualTo(inDb.getImage());
    }
    @Test
    public void postLogin_withValidCredentials_receiveLoggedInUsersDisplayName(){
        User inDb = userService.save(TestUtil.createValidUser());
        authenticate();
        ResponseEntity<Map<String, Object>> login = login(new ParameterizedTypeReference<Map<String, Object>>() {});
        Map<String, Object> body = login.getBody();
        String displayName = (String) body.get("userDisplayName");

        assertThat(displayName).isEqualTo(inDb.getUserDisplayName());
    }
    @Test
    public void postLogin_withValidCredentials_receiveLoggedInUsersUsername(){
        User inDb = userService.save(TestUtil.createValidUser());
        authenticate();
        ResponseEntity<Map<String, Object>> login = login(new ParameterizedTypeReference<Map<String, Object>>() {});
        Map<String, Object> body = login.getBody();
        String username = (String) body.get("username");

        assertThat(username).isEqualTo(inDb.getUsername());
    }
    @Test
    public void postLogin_withValidCredentials_notReceiveLoggedInUsersPassword(){
        User inDb = userService.save(TestUtil.createValidUser());
        authenticate();
        ResponseEntity<Map<String, Object>> login = login(new ParameterizedTypeReference<Map<String, Object>>() {});
        Map<String, Object> body = login.getBody();

        assertThat(body.containsKey("password")).isFalse();
    }

}

package com.grimacegram.grimacegram;

import com.grimacegram.grimacegram.error.ApiError;
import com.grimacegram.grimacegram.grimace.Grimace;
import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.repository.GrimaceRepository;
import com.grimacegram.grimacegram.repository.UserRepository;
import com.grimacegram.grimacegram.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GrimaceControllerTest {
    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GrimaceRepository grimaceRepository;

    @Before
    public void cleanup() {
        grimaceRepository.deleteAll();
        userRepository.deleteAll();

        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    private void authenticate(String username){
        testRestTemplate.getRestTemplate().getInterceptors()
                .add(new BasicAuthenticationInterceptor(username, "P4ssword"));
    }

    private static final String API_1_0_GRIMACES = "/api/1.0/grimace";
    private <T> ResponseEntity<T> postGrimace(Grimace grimace, Class<T> responceType) {
        return testRestTemplate.postForEntity(API_1_0_GRIMACES, grimace, responceType);
    }
    @Test
    public void postGrimace_whenGrimaceIsValidAndUserIsAuthorized_receiveOk() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Grimace grimace = TestUtil.createValidGrimace();
        ResponseEntity<Object> response = postGrimace(grimace, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    public void postGrimace_whenGrimaceIsValidAndUserIsUnauthorized_receiveUnauthorized() {

        Grimace grimace = TestUtil.createValidGrimace();
        ResponseEntity<Object> response = postGrimace(grimace, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    @Test
    public void postGrimace_whenGrimaceIsValidAndUserIsUnauthorized_receiveApiError() {

        Grimace grimace = TestUtil.createValidGrimace();
        ResponseEntity<ApiError> response = postGrimace(grimace, ApiError.class);
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
    @Test
    public void postGrimace_whenGrimaceIsValidAndUserIsAuthorized_grimaceSavedToDatabase() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Grimace grimace = TestUtil.createValidGrimace();
        postGrimace(grimace, Object.class);

        assertThat(grimaceRepository.count()).isEqualTo(1);
    }
    @Test
    public void postGrimace_whenGrimaceIsValidAndUserIsAuthorized_grimaceSavedToDatabaseWithTimestamp() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Grimace grimace = TestUtil.createValidGrimace();
        postGrimace(grimace, Object.class);

        Grimace inDB = grimaceRepository.findAll().get(0);

        assertThat(inDB.getTimestamp()).isNotNull();
    }
    @Test
    public void postGrimace_whenGrimaceContentIsValidAndUserIsAuthorized_receiveBadRequest() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Grimace grimace = new Grimace();
        ResponseEntity<Object> response = postGrimace(grimace, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postGrimace_whenGrimaceContentLessThan10CharactersAndUserIsAuthorized_receiveBadRequest() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Grimace grimace = new Grimace();
        grimace.setContent("123456789");
        ResponseEntity<Object> response = postGrimace(grimace, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postGrimace_whenGrimaceContentIs5000CharactersAndUserIsAuthorized_receiveOk() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Grimace grimace = new Grimace();
        String veryLongString = IntStream.rangeClosed(1, 5000).mapToObj(i -> "x").collect(Collectors.joining());
        grimace.setContent(veryLongString);
        ResponseEntity<Object> response = postGrimace(grimace, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    public void postGrimace_whenGrimaceContentMore5000CharactersAndUserIsAuthorized_receiveBadRequest() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Grimace grimace = new Grimace();
        String veryLongString = IntStream.rangeClosed(1, 5000).mapToObj(i -> "x").collect(Collectors.joining());
        grimace.setContent(veryLongString+1);
        ResponseEntity<Object> response = postGrimace(grimace, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postGrimace_whenGrimaceContentNullAndUserIsAuthorized_receiveApiErrorWithValidationErrors() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Grimace grimace = new Grimace();
        ResponseEntity<ApiError> response = postGrimace(grimace, ApiError.class);
        Map<String, String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("content")).isNotNull();
    }
    @Test
    public void postGrimace_whenGrimaceIsValidAndUserIsAuthorized_grimaceSavedWithAuthenticatedUserInfo() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Grimace grimace = TestUtil.createValidGrimace();
        postGrimace(grimace, Object.class);

        Grimace inDB = grimaceRepository.findAll().get(0);

        assertThat(inDB.getUser().getUsername()).isEqualTo("user1");
    }
    @Test
    public void postGrimace_whenGrimaceIsValidAndUserIsAuthorized_grimaceCanBeAccessedFromUserEntity() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Grimace grimace = TestUtil.createValidGrimace();
        postGrimace(grimace, Object.class);

        User inDBUser = userRepository.findByUsername("user1");

        assertThat(inDBUser.getGrimaceList().size()).isEqualTo(1);
    }

}

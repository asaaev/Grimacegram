package com.grimacegram.grimacegram;

import com.grimacegram.grimacegram.error.ApiError;
import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.repository.UserRepository;
import com.grimacegram.grimacegram.shared.GenericResponse;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testing")
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerTest {

    public static final String API_1_0_USERS = "/api/1.0/users";
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserRepository userRepository;

    @Before
    public void cleanDb(){
        userRepository.deleteAll();
    }
    private User createValidUser (){
        User user = new User();
        user.setUserName("test-user");
        user.setUserDisplayName("test-display");
        user.setUserPassword("P4ssword");
        return user;
    }

    public <T> ResponseEntity<T> postSignup(Object request, Class<T> response){
        return testRestTemplate.postForEntity(API_1_0_USERS, request, response);
    }

//    ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, createValidUser(), Object.class);

    @Test
    public void postUser_whenUserIsValid_receiveOk(){
        User user = createValidUser();
        ResponseEntity<Object> response = postSignup(user, Object.class);

        System.out.println();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postUser_whenUserIsValid_savedToDatabase(){
        User user = createValidUser();
        testRestTemplate.postForEntity(API_1_0_USERS, user, Object.class);

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    public void postUser_whenUserIsValid_receiveSuccessMessage(){
        User user = createValidUser();
        ResponseEntity<GenericResponse> response = postSignup(user, GenericResponse.class);

        assertThat(response.getBody().getMessage()).isNotNull();
    }

    @Test
    public void postUser_whenUserIsValid_passwordIsHasheredInDb(){
        User user = createValidUser();
        testRestTemplate.postForEntity(API_1_0_USERS, user, Object.class);

        List<User> users = userRepository.findAll();
        User inDB = users.get(0);
        assertThat(inDB.getUserPassword()).isNotEqualTo(user.getUserPassword());
    }

    @Test
    public void postUser_whenUserHasNullUsername_receiveBadRequest(){
        User user = createValidUser();
        user.setUserName(null);

        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasNullDisplayname_receiveBadRequest(){
        User user = createValidUser();
        user.setUserDisplayName(null);
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasNullPassword_receiveBadRequest(){
        User user = createValidUser();
        user.setUserPassword(null);
        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasUsernameWithLessThanRequired_receiveBadRequest(){
        User user = createValidUser();
        user.setUserName("abc");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasDisplayNameWithLessThanRequired_receiveBadRequest(){
        User user = createValidUser();
        user.setUserDisplayName("abc");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasPasswordWithLessThanRequired_receiveBadRequest(){
        User user = createValidUser();
        user.setUserPassword("P4sswd");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasUsernameExceedsTheLengthLimit_receiveBadRequest(){
        User user = createValidUser();
        String valueOf256Cars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setUserName(valueOf256Cars);
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasDisplayNameExceedsTheLengthLimit_receiveBadRequest(){
        User user = createValidUser();
        String valueOf256Cars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setUserDisplayName(valueOf256Cars);
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasPasswordExceedsTheLengthLimit_receiveBadRequest(){
        User user = createValidUser();
        String valueOf256Cars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setUserPassword(valueOf256Cars + "A1");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasPasswordWithAllLowercase_receiveBadRequest(){
        User user = createValidUser();
        user.setUserPassword("alllowercase");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasPasswordWithAllUppercase_receiveBadRequest(){
        User user = createValidUser();
        user.setUserPassword("ALLUPPERCASE");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasPasswordWithAllNumber_receiveBadRequest(){
        User user = createValidUser();
        user.setUserPassword("123456789");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserIsInvalid_receiveApiError(){
        User user = new User();
        ResponseEntity<ApiError> apiErrorResponseEntity = postSignup(user, ApiError.class);
        assertThat(apiErrorResponseEntity.getBody().getUrl()).isEqualTo(API_1_0_USERS);
    }
    @Test
    public void postUser_whenUserIsInvalid_receiveApiErrorWithValidationErrors(){
        User user = new User();
        ResponseEntity<ApiError> apiErrorResponseEntity = postSignup(user, ApiError.class);
        assertThat(apiErrorResponseEntity.getBody().getValidationErrors().size()).isEqualTo(3);
    }
    @Test
    public void postUser_whenUserHasNullUsername_receiveMessageOfNullErrorForUsername(){
        User validUser = createValidUser();
        validUser.setUserName(null);
        ResponseEntity<ApiError> apiErrorResponseEntity = postSignup(validUser, ApiError.class);
        Map<String, String> validationErrors = apiErrorResponseEntity.getBody().getValidationErrors();
        assertThat(validationErrors.get("userName")).isEqualTo("Username cannot be null");
    }
    @Test
    public void postUser_whenUserHasNullPassword_receiveMessageOfNullErrorForPassword(){
        User validUser = createValidUser();
        validUser.setUserPassword(null);
        ResponseEntity<ApiError> apiErrorResponseEntity = postSignup(validUser, ApiError.class);
        Map<String, String> validationErrors = apiErrorResponseEntity.getBody().getValidationErrors();
        assertThat(validationErrors.get("userPassword")).isEqualTo("Password cannot be null");
    }
    @Test
    public void postUser_whenUserHasNullDisplayName_receiveMessageOfNullErrorForDisplayname(){
        User validUser = createValidUser();
        validUser.setUserDisplayName(null);
        ResponseEntity<ApiError> apiErrorResponseEntity = postSignup(validUser, ApiError.class);
        Map<String, String> validationErrors = apiErrorResponseEntity.getBody().getValidationErrors();
        assertThat(validationErrors.get("userDisplayName")).isEqualTo("DisplayName cannot be null");
    }
    @Test
    public void postUser_whenUserHasInvalidLengthUserName_receiveGenericMessageOfSizeError(){
        User validUser = createValidUser();
        validUser.setUserName("abc");
        ResponseEntity<ApiError> apiErrorResponseEntity = postSignup(validUser, ApiError.class);
        Map<String, String> validationErrors = apiErrorResponseEntity.getBody().getValidationErrors();
        assertThat(validationErrors.get("userName")).isEqualTo("It must have minimum 4 and maximum 255 characters");
    }
    @Test
    public void postUser_whenUserHasInvalidPasswordPattern_receiveMessageOfPassword(){
        User validUser = createValidUser();
        validUser.setUserPassword("alllowercase");
        ResponseEntity<ApiError> apiErrorResponseEntity = postSignup(validUser, ApiError.class);
        Map<String, String> validationErrors = apiErrorResponseEntity.getBody().getValidationErrors();
        assertThat(validationErrors.get("userPassword")).isEqualTo("Password musthave at least one uppercase" +
                ", one lowercase letter and one number");
    }
    @Test
    public void postUser_whenAnotherUserHasSameUsername_receiveBadRequest(){
        userRepository.save(createValidUser());
        User validUser = createValidUser();
        ResponseEntity<Object> response = postSignup(validUser, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenAnotherUserHasSameUsername_receiveMessageOfDuplicateUsername(){
        userRepository.save(createValidUser());
        User validUser = createValidUser();
        ResponseEntity<ApiError> apiErrorResponseEntity = postSignup(validUser, ApiError.class);
        Map<String, String> validationErrors = apiErrorResponseEntity.getBody().getValidationErrors();
        assertThat(validationErrors.get("userName")).isEqualTo("This name is in use");
    }

}

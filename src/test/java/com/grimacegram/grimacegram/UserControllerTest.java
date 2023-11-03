package com.grimacegram.grimacegram;

import com.grimacegram.grimacegram.error.ApiError;
import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.repository.UserRepository;
import com.grimacegram.grimacegram.services.UserService;
import com.grimacegram.grimacegram.shared.GenericResponse;
import com.grimacegram.grimacegram.vm.UserUpdateVM;
import com.grimacegram.grimacegram.vm.UserVM;
import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.grimacegram.grimacegram.TestUtil.createValidUser;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {

    public static final String API_1_0_USERS = "/api/1.0/users";
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Before
    public void cleanDb(){
        userRepository.deleteAll();
    }

    @After
    public void resetRestTemplate() {
        testRestTemplate.getRestTemplate().setInterceptors(new ArrayList<>());
    }


    public <T> ResponseEntity<T> postSignup(Object request, Class<T> response){
        return testRestTemplate.postForEntity(API_1_0_USERS, request, response);
    }
    public <T> ResponseEntity<T> getUsers(ParameterizedTypeReference<T> responseType){
        return testRestTemplate.exchange(API_1_0_USERS, HttpMethod.GET, null, responseType);
    }
    public <T> ResponseEntity<T> getUsers(String path, ParameterizedTypeReference<T> responseType){
        return testRestTemplate.exchange(path, HttpMethod.GET, null, responseType);
    }
    public <T> ResponseEntity<T> getUser(String username, Class<T> responseType){
        String path = API_1_0_USERS + "/" + username;
        return testRestTemplate.getForEntity(path, responseType);
    }
    public <T> ResponseEntity<T> putUser(long id, HttpEntity<?> requestEntity, Class<T> responseType){
        String path = API_1_0_USERS + "/" + id;
        return testRestTemplate.exchange(path, HttpMethod.PUT, requestEntity, responseType);
    }
    private void authenticate(String username){
        testRestTemplate.getRestTemplate().getInterceptors()
                .add(new BasicAuthenticationInterceptor(username, "P4ssword"));
    }

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
        assertThat(inDB.getPassword()).isNotEqualTo(user.getPassword());
    }

    @Test
    public void postUser_whenUserHasNullUsername_receiveBadRequest(){
        User user = createValidUser();
        user.setUsername(null);

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
        user.setPassword(null);
        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasUsernameWithLessThanRequired_receiveBadRequest(){
        User user = createValidUser();
        user.setUsername("abc");
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
        user.setPassword("P4sswd");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasUsernameExceedsTheLengthLimit_receiveBadRequest(){
        User user = createValidUser();
        String valueOf256Cars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setUsername(valueOf256Cars);
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
        user.setPassword(valueOf256Cars + "A1");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasPasswordWithAllLowercase_receiveBadRequest(){
        User user = createValidUser();
        user.setPassword("alllowercase");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasPasswordWithAllUppercase_receiveBadRequest(){
        User user = createValidUser();
        user.setPassword("ALLUPPERCASE");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postUser_whenUserHasPasswordWithAllNumber_receiveBadRequest(){
        User user = createValidUser();
        user.setPassword("123456789");
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
        validUser.setUsername(null);
        ResponseEntity<ApiError> apiErrorResponseEntity = postSignup(validUser, ApiError.class);
        Map<String, String> validationErrors = apiErrorResponseEntity.getBody().getValidationErrors();
        assertThat(validationErrors.get("username")).isEqualTo("Username cannot be null");
    }
    @Test
    public void postUser_whenUserHasNullPassword_receiveMessageOfNullErrorForPassword(){
        User validUser = createValidUser();
        validUser.setPassword(null);
        ResponseEntity<ApiError> apiErrorResponseEntity = postSignup(validUser, ApiError.class);
        Map<String, String> validationErrors = apiErrorResponseEntity.getBody().getValidationErrors();
        assertThat(validationErrors.get("password")).isEqualTo("Password cannot be null");
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
        validUser.setUsername("abc");
        ResponseEntity<ApiError> apiErrorResponseEntity = postSignup(validUser, ApiError.class);
        Map<String, String> validationErrors = apiErrorResponseEntity.getBody().getValidationErrors();
        assertThat(validationErrors.get("username")).isEqualTo("It must have minimum 4 and maximum 255 characters");
    }
    @Test
    public void postUser_whenUserHasInvalidPasswordPattern_receiveMessageOfPassword(){
        User validUser = createValidUser();
        validUser.setPassword("alllowercase");
        ResponseEntity<ApiError> apiErrorResponseEntity = postSignup(validUser, ApiError.class);
        Map<String, String> validationErrors = apiErrorResponseEntity.getBody().getValidationErrors();
        assertThat(validationErrors.get("password")).isEqualTo("Password musthave at least one uppercase" +
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
        assertThat(validationErrors.get("username")).isEqualTo("This name is in use");
    }
    @Test
    public void getUsers_whenThereAreNoUsersInDB_receiveOK(){
        ResponseEntity<Object> response = getUsers(new ParameterizedTypeReference<Object>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    public void getUsers_whenThereAreNoUsersInDB_receivePageWithZeroItems(){
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<TestPage<Object>>() {});
        assertThat(response.getBody().getTotalElements()).isEqualTo(0);
    }
    @Test
    public void getUsers_whenThereIsAUsersInDB_receivePageWithUser(){
        userRepository.save(TestUtil.createValidUser());
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<TestPage<Object>>() {});
        assertThat(response.getBody().getNumberOfElements()).isEqualTo(1);
    }
    @Test
    public void getUsers_whenThereIsAUsersInDB_receiveUserWithoutPassword(){
        userRepository.save(TestUtil.createValidUser());
        ResponseEntity<TestPage<Map<String, Object>>> response = getUsers(new ParameterizedTypeReference<TestPage<Map<String, Object>>>() {});
        Map<String, Object> entity = response.getBody().getContent().get(0);
        assertThat(entity.containsKey("password")).isFalse();
    }
    @Test
    public void getUsers_whenPageIsRequestedFor3ItemsPerPageWhereTheDatabaseHas20Users_receive3Users(){
        IntStream.rangeClosed(1, 20).mapToObj(i -> "test-user-" + i)
                .map(TestUtil::createValidUser)
                .forEach(user -> userRepository.save(user));
        String path = API_1_0_USERS + "?page=0&size=3";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getContent().size()).isEqualTo(3);
    }
    @Test
    public void getUsers_whenPageSizeNotProvided_receivePageSizeAs10(){
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getSize()).isEqualTo(10);
    }
    @Test
    public void getUsers_whenPageSizeIsGreaterThan100_receivePageSizeAs100(){
        String path = API_1_0_USERS + "?size=500";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getSize()).isEqualTo(100);
    }
    @Test
    public void getUsers_whenPageSizeIsNegative_receivePageSizeAs10(){
        String path = API_1_0_USERS + "?size=-5";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getSize()).isEqualTo(10);
    }
    @Test
    public void getUsers_whenPageIsNegative_receiveFirstPage(){
        String path = API_1_0_USERS + "?page=-5";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getNumber()).isEqualTo(0);
    }
    @Test
    public void getUsers_whenUserLoggedIn_receivePageWithoutLoggedInUser(){
        userService.save(TestUtil.createValidUser("user1"));
        userService.save(TestUtil.createValidUser("user2"));
        userService.save(TestUtil.createValidUser("user3"));
        authenticate("user1");
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<TestPage<Object>>() {});
        assertThat(response.getBody().getTotalElements()).isEqualTo(2);
    }
    @Test
    public void getUserByUsername_WhenUserExist_receiveOk(){
        String username = "test-user";
        userService.save(TestUtil.createValidUser(username));
        ResponseEntity<Object> response = getUser(username, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    public void getUserByUsername_WhenUserExist_receiveUserWithoutPassword(){
        String username = "test-user";
        userService.save(TestUtil.createValidUser(username));
        ResponseEntity<String> response = getUser(username, String.class);
        assertThat(response.getBody().contains("password")).isFalse();
    }
    @Test
    public void getUserByUsername_WhenUserDoesNotExist_receiveNotFound(){
        ResponseEntity<Object> response = getUser("unknown-user", Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    public void getUserByUsername_WhenUserDoesNotExist_receiveApiError(){
        ResponseEntity<ApiError> response = getUser("unknown-user", ApiError.class);
        assertThat(response.getBody().getMessage().contains("unknown-use")).isTrue();
    }
    @Test
    public void putUser_whenUnauthorizedUserSendTheRequest_receiveUnauthorized(){
        ResponseEntity<Object> response = putUser(123, null, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    @Test
    public void putUser_whenAuthorizedUserSendsUpdateForAnotherUser_receiveForbidden(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        authenticate(user.getUsername());

        long anotherUserId = user.getUserId() + 123;
        ResponseEntity<Object> response = putUser(anotherUserId, null, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
    @Test
    public void putUser_whenUnauthorizedUserSendTheRequest_receiveApiError(){
        ResponseEntity<ApiError> response = putUser(123, null, ApiError.class);
        assertThat(response.getBody().getUrl()).contains("users/123");
    }
    @Test
    public void putUser_whenAuthorizedUserSendsUpdateForAnotherUser_receiveApiError(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        authenticate(user.getUsername());

        long anotherUserId = user.getUserId() + 123;
        ResponseEntity<ApiError> response = putUser(anotherUserId, null, ApiError.class);
        assertThat(response.getBody().getUrl()).contains("users/" + anotherUserId);
    }
    @Test
    public void putUser_whenValidRequestBodyFromAuthorizedUser_receiveOk(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        authenticate(user.getUsername());
        UserUpdateVM updatedUser = createValidUserUpdateVM();

        HttpEntity<UserUpdateVM> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<Object> response = putUser(user.getUserId(), requestEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    public void putUser_whenValidRequestBodyFromAuthorizedUser_displayNameUpdated(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        authenticate(user.getUsername());
        UserUpdateVM updatedUser = createValidUserUpdateVM();

        HttpEntity<UserUpdateVM> requestEntity = new HttpEntity<>(updatedUser);
        putUser(user.getUserId(), requestEntity, Object.class);

        User userInDB = userRepository.findByUsername("user1");
        assertThat(userInDB.getUserDisplayName()).isEqualTo(updatedUser.getDisplayName());

    }
    @Test
    public void putUser_whenValidRequestBodyFromAuthorizedUser_receiveUserVMWithUpdatedDisplayName(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        authenticate(user.getUsername());
        UserUpdateVM updatedUser = createValidUserUpdateVM();

        HttpEntity<UserUpdateVM> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserVM> response = putUser(user.getUserId(), requestEntity, UserVM.class);

        assertThat(response.getBody().getUserDisplayName()).isEqualTo(updatedUser.getDisplayName());

    }
    @Test
    public void putUser_whenValidRequestBodyWithSupportedImageFromAuthorizedUser_receiveUserVMWithRandomImageName() throws IOException {
        User user = userService.save(TestUtil.createValidUser("user1"));
        authenticate(user.getUsername());

        ClassPathResource imageResource = new ClassPathResource("profile.jpg");

        UserUpdateVM updatedUser = createValidUserUpdateVM();

        byte[] imageArr = FileUtils.readFileToByteArray(imageResource.getFile());
        String imageString = Base64.getEncoder().encodeToString(imageArr);
        updatedUser.setImage(imageString);

        HttpEntity<UserUpdateVM> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserVM> response = putUser(user.getUserId(), requestEntity, UserVM.class);

        assertThat(response.getBody().getImage()).isNotEqualTo("profile-image.png");

    }

    private UserUpdateVM createValidUserUpdateVM() {
        UserUpdateVM updatedUser = new UserUpdateVM();
        updatedUser.setDisplayName("newDisplayName");
        return updatedUser;
    }
}

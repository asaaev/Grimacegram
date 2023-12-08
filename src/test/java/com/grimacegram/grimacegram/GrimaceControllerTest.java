package com.grimacegram.grimacegram;

import com.grimacegram.grimacegram.configuration.AppConfiguration;
import com.grimacegram.grimacegram.error.ApiError;
import com.grimacegram.grimacegram.grimace.Grimace;
import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.repository.FileAttachmentRepository;
import com.grimacegram.grimacegram.repository.GrimaceRepository;
import com.grimacegram.grimacegram.repository.UserRepository;
import com.grimacegram.grimacegram.services.FileService;
import com.grimacegram.grimacegram.services.GrimaceService;
import com.grimacegram.grimacegram.services.UserService;
import com.grimacegram.grimacegram.shared.FileAttachment;
import com.grimacegram.grimacegram.vm.GrimaceVM;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.io.File;
import java.io.IOException;
import java.util.List;
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
    @Autowired
    GrimaceService grimaceService;
    @Autowired
    FileAttachmentRepository fileAttachmentRepository;
    @Autowired
    FileService fileService;
    @Autowired
    AppConfiguration appConfiguration;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Before
    public void cleanup() throws IOException {
        fileAttachmentRepository.deleteAll();
        grimaceRepository.deleteAll();
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
        FileUtils.cleanDirectory(new File(appConfiguration.getFullAttachmentsPath()));
    }

    private void authenticate(String username){
        testRestTemplate.getRestTemplate().getInterceptors()
                .add(new BasicAuthenticationInterceptor(username, "P4ssword"));
    }

    private static final String API_1_0_GRIMACES = "/api/1.0/grimace";

    private <T> ResponseEntity<T> postGrimace(Grimace grimace, Class<T> responceType) {
        return testRestTemplate.postForEntity(API_1_0_GRIMACES, grimace, responceType);
    }
    public <T> ResponseEntity <T> getGrimaces(ParameterizedTypeReference<T> responseType){
        return  testRestTemplate.exchange(API_1_0_GRIMACES, HttpMethod.GET, null, responseType);
    }
    public <T> ResponseEntity <T> getGrimacesOfUser(String username, ParameterizedTypeReference<T> responseType){
        String path = "/api/1.0/users/" + username + "/grimace";
        return  testRestTemplate.exchange(path, HttpMethod.GET, null, responseType);
    }
    public <T> ResponseEntity <T> getOldGrimaces(long grimaceId, ParameterizedTypeReference<T> responseType){
        String path = API_1_0_GRIMACES + "/" + grimaceId + "?direction=before&page=0&size=5&sort=id,desc";
        return testRestTemplate.exchange(path, HttpMethod.GET,null, responseType);
    }
    public <T> ResponseEntity <T> getOldGrimacesOfUser(long grimaceId,String username, ParameterizedTypeReference<T> responseType){
        String path = "/api/1.0/users/" + username + "/grimace" + "/" + grimaceId + "?direction=before&page=0&size=5&sort=id,desc";
        return testRestTemplate.exchange(path, HttpMethod.GET,null, responseType);
    }
    public <T> ResponseEntity <T> getNewGrimaces(long grimaceId, ParameterizedTypeReference<T> responseType){
        String path = API_1_0_GRIMACES + "/" + grimaceId + "?direction=after&sort=id,desc";
        return testRestTemplate.exchange(path, HttpMethod.GET,null, responseType);
    }
    public <T> ResponseEntity <T> getNewGrimacesOfUser(long grimaceId,String username, ParameterizedTypeReference<T> responseType){
        String path = "/api/1.0/users/" + username + "/grimace" + "/" + grimaceId + "?direction=after&sort=id,desc";
        return testRestTemplate.exchange(path, HttpMethod.GET,null, responseType);
    }
    public <T> ResponseEntity <T> getNewGrimaceCount(long grimaceId, ParameterizedTypeReference<T> responseType){
        String path = API_1_0_GRIMACES + "/" + grimaceId + "?direction=after&count=true";
        return testRestTemplate.exchange(path, HttpMethod.GET,null, responseType);
    }
    public <T> ResponseEntity <T> getNewGrimaceCountOfUser(long grimaceId, String username, ParameterizedTypeReference<T> responseType){
        String path = "/api/1.0/users/" + username + "/grimace" + "/" + grimaceId + "?direction=after&count=true";
        return testRestTemplate.exchange(path, HttpMethod.GET,null, responseType);
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
        User user = userService.save(TestUtil.createValidUser("user1"));

        authenticate("user1");
        Grimace grimace = TestUtil.createValidGrimace();
        postGrimace(grimace, Object.class);

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User inDBUser = entityManager.find(User.class, user.getUserId());
        assertThat(inDBUser.getGrimaceList().size()).isEqualTo(1);
    }
    @Test
    public void getGrimace_whenThereAreNoGrimaces_receiveOk(){
        ResponseEntity<Object> response = getGrimaces(new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    public void getGrimace_whenThereAreNoGrimaces_receivePageWithZeroItems(){
        ResponseEntity<TestPage<Object>> response = getGrimaces(new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(0);
    }
    @Test
    public void getGrimace_whenThereAreGrimaces_receivePageWithItems(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());

        ResponseEntity<TestPage<Object>> response = getGrimaces(new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(3);
    }
    @Test
    public void getGrimace_whenThereAreGrimaces_receivePageWithGrimaceVM(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());

        ResponseEntity<TestPage<GrimaceVM>> response = getGrimaces(new ParameterizedTypeReference<TestPage<GrimaceVM>>() {
        });
        GrimaceVM storedGrimace = response.getBody().getContent().get(0);
        assertThat(storedGrimace.getUser().getUsername()).isEqualTo("user1");
    }
    @Test
    public void postGrimace_whenGrimaceIsValidAndUserIsAuthorized_receiveGrimaceVM() {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");
        Grimace grimace = TestUtil.createValidGrimace();
        ResponseEntity<GrimaceVM> response = postGrimace(grimace, GrimaceVM.class);
        assertThat(response.getBody().getUser().getUsername()).isEqualTo("user1");
    }
    @Test
    public void postGrimace_whenGrimaceHasFileAttachmentAndUserIsAuthorized_fileAttachmentGrimaceRelationIsUpdatedInDatabase() throws IOException {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");

        MultipartFile file = createFile();

        FileAttachment savedFile = fileService.saveAttachment(file);

        Grimace grimace = TestUtil.createValidGrimace();
        grimace.setAttachment(savedFile);
        ResponseEntity<GrimaceVM> response = postGrimace(grimace, GrimaceVM.class);

        FileAttachment inDB = fileAttachmentRepository.findAll().get(0);
        assertThat(inDB.getGrimace().getId()).isEqualTo(response.getBody().getId());
    }
    @Test
    public void postGrimace_whenGrimaceHasFileAttachmentAndUserIsAuthorized_grimaceFileAttachmentRelationIsUpdatedInDatabase() throws IOException {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");

        MultipartFile file = createFile();

        FileAttachment savedFile = fileService.saveAttachment(file);

        Grimace grimace = TestUtil.createValidGrimace();
        grimace.setAttachment(savedFile);
        ResponseEntity<GrimaceVM> response = postGrimace(grimace, GrimaceVM.class);

        Grimace inDB = grimaceRepository.findById(response.getBody().getId()).get();
        assertThat(inDB.getAttachment().getId()).isEqualTo(savedFile.getId());
    }
    @Test
    public void postGrimace_whenGrimaceHasFileAttachmentAndUserIsAuthorized_receiveGrimaceVMWithAttachment() throws IOException {
        userService.save(TestUtil.createValidUser("user1"));
        authenticate("user1");

        MultipartFile file = createFile();

        FileAttachment savedFile = fileService.saveAttachment(file);

        Grimace grimace = TestUtil.createValidGrimace();
        grimace.setAttachment(savedFile);
        ResponseEntity<GrimaceVM> response = postGrimace(grimace, GrimaceVM.class);

        FileAttachment inDB = fileAttachmentRepository.findAll().get(0);
        assertThat(response.getBody().getAttachment().getName()).isEqualTo(savedFile.getName());
    }

    @Test
    public void getGrimacesOfUser_whenUserExists_receiveOk(){
        userService.save(TestUtil.createValidUser("user1"));
        ResponseEntity<Object> response = getGrimacesOfUser("user1", new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    public void getGrimacesOfUser_whenUserDoesNotExists_receiveNotFound(){
        ResponseEntity<Object> response = getGrimacesOfUser("unknown-user", new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    public void getGrimacesOfUser_whenUserExists_receivePageWithZeroGrimaces(){
        userService.save(TestUtil.createValidUser("user1"));
        ResponseEntity<TestPage<Object>> response = getGrimacesOfUser("user1", new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(0);
    }

    @Test
    public void getGrimaceOfUser_whenUserExistWithGrimace_receivePageWithGrimaceVM(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());

        ResponseEntity<TestPage<GrimaceVM>> response = getGrimacesOfUser("user1", new ParameterizedTypeReference<TestPage<GrimaceVM>>() {
        });
        GrimaceVM storedGrimace = response.getBody().getContent().get(0);
        assertThat(storedGrimace.getUser().getUsername()).isEqualTo("user1");
    }
    @Test
    public void getGrimaceOfUser_whenUserExistWithMultipleGrimace_receivePageWithMatchingGrimaceCount(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());

        ResponseEntity<TestPage<GrimaceVM>> response = getGrimacesOfUser("user1", new ParameterizedTypeReference<TestPage<GrimaceVM>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(3);
    }
    @Test
    public void getGrimaceOfUser_whenMultipleUserExistWithMultipleGrimace_receivePageWithMatchingGrimaceCount(){
        User userWithTreeGrimaces = userService.save(TestUtil.createValidUser("user1"));
        IntStream.rangeClosed(1,3).forEach(i -> {
            grimaceService.save(userWithTreeGrimaces, TestUtil.createValidGrimace());
        });
        User userWithFiveGrimaces = userService.save(TestUtil.createValidUser("user2"));
        IntStream.rangeClosed(1,5).forEach(i -> {
            grimaceService.save(userWithFiveGrimaces, TestUtil.createValidGrimace());
        });
        ResponseEntity<TestPage<GrimaceVM>> response = getGrimacesOfUser(userWithFiveGrimaces.getUsername(), new ParameterizedTypeReference<TestPage<GrimaceVM>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(5);
    }
    @Test
    public void getOldGrimaces_WhenThereAreNoGrimaces_receiveOk(){
        ResponseEntity<Object> response = getOldGrimaces(5, new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    public void getOldGrimaces_whenThereAreGrimaces_receivePageWithItemsProvidedId(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        Grimace fourth = grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());

        ResponseEntity<TestPage<Object>> response = getOldGrimaces(fourth.getId(), new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(3);
    }
    @Test
    public void getOldGrimaces_whenThereAreGrimaces_receivePageWithGrimaceVMBeforeProvidedId(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        Grimace fourth = grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());

        ResponseEntity<TestPage<GrimaceVM>> response = getOldGrimaces(fourth.getId(), new ParameterizedTypeReference<TestPage<GrimaceVM>>() {
        });
        assertThat(response.getBody().getContent().get(0).getDate()).isGreaterThan(0);
    }
    @Test
    public void getOldGrimacesOfUser_henUserExistThereAreNoGrimaces_receiveOk(){
        userService.save(TestUtil.createValidUser("user1"));
        ResponseEntity<Object> response = getOldGrimacesOfUser(5, "user1", new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    public void getOldGrimacesOfUser_whenUserExistAndThereAreGrimaces_receivePageWithItemsProvidedId(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        Grimace fourth = grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());

        ResponseEntity<TestPage<Object>> response = getOldGrimacesOfUser(fourth.getId(), "user1", new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(3);
    }
    @Test
    public void getOldGrimacesOfUser_whenUserExistAndThereAreGrimaces_receivePageWithGrimaceVMBeforeProvidedId(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        Grimace fourth = grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());

        ResponseEntity<TestPage<GrimaceVM>> response = getOldGrimacesOfUser(fourth.getId(), "user1", new ParameterizedTypeReference<TestPage<GrimaceVM>>() {
        });
        assertThat(response.getBody().getContent().get(0).getDate()).isGreaterThan(0);
    }
    @Test
    public void getOldGrimacesOfUser_whenUserDoesNotExistThereAreNoGrimaces_receiveNotFound(){
        ResponseEntity<Object> response = getOldGrimacesOfUser(5, "user1", new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    public void getOldGrimacesOfUser_whenUserExistAndThereAreNoGrimaces_receivePageWithZeroItemsBeforeProvidedId(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        Grimace fourth = grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());

        userService.save(TestUtil.createValidUser("user2"));

        ResponseEntity<TestPage<GrimaceVM>> response = getOldGrimacesOfUser(fourth.getId(), "user2", new ParameterizedTypeReference<TestPage<GrimaceVM>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(0);
    }
    @Test
    public void getNewGrimaces_whenThereAreGrimaces_receiveListOfItemsAfterProvidedId(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        Grimace fourth = grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());

        ResponseEntity<List<Object>> response = getNewGrimaces(fourth.getId(), new ParameterizedTypeReference<List<Object>>() {
        });
        assertThat(response.getBody().size()).isEqualTo(1);
    }
    @Test
    public void getNewGrimaces_whenThereAreGrimaces_receiveListOfGrimacesVMAfterProvidedId(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        Grimace fourth = grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());

        ResponseEntity<List<GrimaceVM>> response = getNewGrimaces(fourth.getId(), new ParameterizedTypeReference<List<GrimaceVM>>() {
        });
        assertThat(response.getBody().get(0).getDate()).isGreaterThan(0);
    }
    @Test
    public void getNewGrimacesOfUser_WhenUserExistThereAreNoGrimaces_receiveOk(){
        userService.save(TestUtil.createValidUser("user1"));
        ResponseEntity<Object> response = getNewGrimacesOfUser(5, "user1", new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    public void getNewGrimacesOfUser_whenUserExistAndThereAreGrimaces_receiveListWithItemsAfterProvidedId(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        Grimace fourth = grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());

        ResponseEntity<List<Object>> response = getNewGrimacesOfUser(fourth.getId(), "user1", new ParameterizedTypeReference<List<Object>>() {
        });
        assertThat(response.getBody().size()).isEqualTo(1);
    }
    @Test
    public void getNewGrimacesOfUser_whenUserExistAndThereAreGrimaces_receiveListWithGrimaceVMAfterProvidedId(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        Grimace fourth = grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());

        ResponseEntity<List<GrimaceVM>> response = getNewGrimacesOfUser(fourth.getId(), "user1", new ParameterizedTypeReference<List<GrimaceVM>>() {
        });
        assertThat(response.getBody().get(0).getDate()).isGreaterThan(0);
    }
    @Test
    public void getNewGrimacesOfUser_whenUserDoesNotExistThereAreNoGrimaces_receiveNotFound(){
        ResponseEntity<Object> response = getNewGrimacesOfUser(5, "user1", new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    public void getNewGrimacesOfUser_whenUserExistAndThereAreNoGrimaces_receiveListWithZeroItemsAfterProvidedId(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        Grimace fourth = grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());

        userService.save(TestUtil.createValidUser("user2"));

        ResponseEntity<List<GrimaceVM>> response = getNewGrimacesOfUser(fourth.getId(), "user2", new ParameterizedTypeReference<List<GrimaceVM>>() {
        });
        assertThat(response.getBody().size()).isEqualTo(0);
    }
    @Test
    public void getNewGrimaceCount_whenThereAreGrimaces_receiveCountAfterProvidedId(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        Grimace fourth = grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());

        ResponseEntity<Map<String, Long>> response = getNewGrimaceCount(fourth.getId(), new ParameterizedTypeReference<Map<String, Long>>() {
        });
        assertThat(response.getBody().get("count")).isEqualTo(1);
    }
    @Test
    public void getNewGrimaceCountOfUser_whenThereAreGrimaces_receiveCountAfterProvidedId(){
        User user = userService.save(TestUtil.createValidUser("user1"));
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());
        Grimace fourth = grimaceService.save(user, TestUtil.createValidGrimace());
        grimaceService.save(user, TestUtil.createValidGrimace());

        ResponseEntity<Map<String, Long>> response = getNewGrimaceCountOfUser(fourth.getId(), "user1", new ParameterizedTypeReference<Map<String, Long>>() {
        });
        assertThat(response.getBody().get("count")).isEqualTo(1);
    }
    private MultipartFile createFile() throws IOException {
        ClassPathResource imageResource = new ClassPathResource("profile.jpeg");
        byte[] fileAsByte = FileUtils.readFileToByteArray(imageResource.getFile());

        MultipartFile file = new MockMultipartFile("profile.jpeg", fileAsByte);
        return file;
    }
}

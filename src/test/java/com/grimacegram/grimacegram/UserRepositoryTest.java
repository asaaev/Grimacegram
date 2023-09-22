package com.grimacegram.grimacegram;

import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    UserRepository userRepository;

    @Test
    public void findByUsername_whenUserExists_returnsUser() {
        User user = new User();
        user.setUserName("test-user");
        user.setUserDisplayName("test-display");
        user.setUserPassword("P4sssword");

        testEntityManager.persist(user);

        User inDB = userRepository.findByUserName("test-user");
        assertThat(inDB).isNotNull();
    }
    @Test
    public void findByUsername_whenUserDoesNotExist_returnsNull(){
        User inDB = userRepository.findByUserName("nonexistinguser");
        assertThat(inDB).isNull();
    }
}

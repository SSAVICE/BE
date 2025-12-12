package teamssavice.ssavice.user.infrastructure.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import teamssavice.ssavice.user.UserFixture;
import teamssavice.ssavice.user.entity.Users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private Users user;

    @BeforeEach
    void setUp() {
        user = UserFixture.user();
    }

    @Test
    @DisplayName("email로 조회")
    void findByEmailTest() {
        // given
        String email = user.getEmail();
        userRepository.save(user);

        // when
        Users actual = userRepository.findByEmail(email).get();

        // then
        assertAll(
                () -> assertThat(actual.getName()).isEqualTo(user.getName()),
                () -> assertThat(actual.getEmail()).isEqualTo(user.getEmail()),
                () -> assertThat(actual.getPhoneNumber()).isEqualTo(user.getPhoneNumber()),
                () -> assertThat(actual.getImageUrl()).isEqualTo(user.getImageUrl())
        );
    }
}